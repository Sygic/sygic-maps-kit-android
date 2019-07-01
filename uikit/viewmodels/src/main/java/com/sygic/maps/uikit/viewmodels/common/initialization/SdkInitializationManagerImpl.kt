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

package com.sygic.maps.uikit.viewmodels.common.initialization

import android.app.Application
import androidx.annotation.RestrictTo
import com.sygic.maps.uikit.viewmodels.R
import com.sygic.maps.uikit.viewmodels.common.extensions.getApiKey
import com.sygic.sdk.SygicEngine
import java.util.*

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
class SdkInitializationManagerImpl(
    private val app: Application
) : SdkInitializationManager, SygicEngine.OnInitListener {

    @InitializationState
    override var initializationState = InitializationState.INITIALIZATION_NOT_STARTED
    private val callbacks = LinkedHashSet<SdkInitializationManager.Callback>()

    override fun initialize(callback: SdkInitializationManager.Callback) {
        synchronized(this) {
            if (initializationState == InitializationState.INITIALIZED) {
                callback.onSdkInitialized()
                return
            }

            callbacks.add(callback)

            if (initializationState == InitializationState.INITIALIZING) {
                return
            }

            initializationState = InitializationState.INITIALIZING
        }

        app.getApiKey()?.let { key ->
            SygicEngine.Builder(app)
                .setKeyAndSecret(app.packageName, key)
                .setOnlineRoutingServiceKey(app.getString(R.string.online_routing_service_key))
                .setInitListener(this)
                .init()
        }
    }

    override fun onSdkInitialized() {
        synchronized(this) { initializationState = InitializationState.INITIALIZED }
        callbacks.forEach { it.onSdkInitialized() }
        callbacks.clear()
    }

    override fun onError(@SygicEngine.OnInitListener.InitError error: Int) {
        synchronized(this) { initializationState = InitializationState.ERROR }
        callbacks.forEach { it.onError(error) }
        callbacks.clear()
    }
}