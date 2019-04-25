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

package com.sygic.maps.module.common.initialization.manager

import android.app.Application
import androidx.annotation.RestrictTo
import com.sygic.maps.module.common.utils.getApiKey
import com.sygic.sdk.SygicEngine
import java.util.*

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
class SdkInitializationManagerImpl : SdkInitializationManager, SygicEngine.OnInitListener {

    private var initializing = false
    private var initialized = false
    private val callbacks = LinkedHashSet<SdkInitializationManager.Callback>()

    override fun initialize(application: Application, callback: SdkInitializationManager.Callback) {
        synchronized(this) {
            if (initialized) {
                callback.onSdkInitialized()
                return
            }

            callbacks.add(callback)

            if (initializing) {
                return
            }
        }

        initializing = true
        application.getApiKey()?.let { key ->
            SygicEngine.Builder(application)
                .setKeyAndSecret(application.packageName, key)
                .setInitListener(this).init()
        }
    }

    override fun onSdkInitialized() {
        synchronized(this) {
            initializing = false
            initialized = true
        }
        callbacks.forEach { it.onSdkInitialized() }
        callbacks.clear()
    }

    override fun onError(@SygicEngine.OnInitListener.InitError error: Int) {
        /* Currently do nothing */
    }
}