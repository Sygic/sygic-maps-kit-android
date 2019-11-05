/*
 * Copyright (c) 2019 Sygic a.s. All rights reserved.
 *
 * This project is licensed under the MIT License.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.sygic.maps.uikit.viewmodels.common.initialization.sdk

import android.app.Application
import androidx.annotation.RestrictTo
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sygic.maps.uikit.viewmodels.common.extensions.getApiKey
import com.sygic.maps.uikit.viewmodels.common.initialization.sdk.state.InitializationState
import com.sygic.maps.uikit.viewmodels.common.remote.RemoteControlManager
import com.sygic.maps.uikit.views.common.extensions.asMutable
import com.sygic.maps.uikit.views.common.utils.SingletonHolder
import com.sygic.maps.uikit.views.common.utils.logError
import com.sygic.sdk.SygicEngine

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
class SdkInitializationManagerImpl private constructor(
    private val app: Application
) : SdkInitializationManager {

    companion object : SingletonHolder<SdkInitializationManagerImpl>() {
        @JvmStatic
        fun getInstance(app: Application) = getInstance { SdkInitializationManagerImpl(app) }
    }

    override var initializationState: LiveData<InitializationState> =
        MutableLiveData<InitializationState>(InitializationState.INITIALIZATION_NOT_STARTED)

    override fun initialize() {
        synchronized(this) {
            if (initializationState.value == InitializationState.INITIALIZED || initializationState.value == InitializationState.INITIALIZING) {
                return
            }

            initializationState.asMutable().value = InitializationState.INITIALIZING
        }

        app.getApiKey()?.let { key ->
            SygicEngine.Builder(app)
                .setKeyAndSecret(app.packageName, key)
                .setRemoteControl(RemoteControlManager)
                .setInitListener(object : SygicEngine.OnInitListener {
                    override fun onSdkInitialized() {
                        synchronized(this) { initializationState.asMutable().value = InitializationState.INITIALIZED }
                    }

                    override fun onError(@SygicEngine.OnInitListener.InitError error: Int) {
                        synchronized(this) { initializationState.asMutable().value = InitializationState.ERROR }

                        val errorType = when (error) {
                            SygicEngine.OnInitListener.InitError.InternalInit -> "Internal init"
                            SygicEngine.OnInitListener.InitError.Resources -> "Resources"
                            else -> "Unknown"
                        }
                        logError("SDK Initialization failed: $errorType error :(")
                    }
                })
                .init()
        }
    }
}