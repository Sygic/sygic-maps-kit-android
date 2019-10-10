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

package com.sygic.maps.uikit.viewmodels.common.geocoder

import androidx.annotation.RestrictTo
import com.sygic.maps.uikit.viewmodels.common.initialization.InitializationManager
import com.sygic.maps.uikit.viewmodels.common.initialization.InitializationState
import com.sygic.sdk.InitializationCallback
import com.sygic.sdk.context.SygicContext
import com.sygic.sdk.position.GeoCoordinates
import com.sygic.sdk.search.ReverseGeocoder
import com.sygic.sdk.search.ReverseGeocoderProvider

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
object ReverseGeocoderManagerClientImpl : ReverseGeocoderManagerClient {

    @InitializationState
    override var initializationState = InitializationState.INITIALIZATION_NOT_STARTED
    private val callbacks = LinkedHashSet<InitializationManager.Callback>()

    private lateinit var reverseGeocoder: ReverseGeocoder

    override fun initialize(callback: InitializationManager.Callback?) {
        synchronized(this) {
            if (initializationState == InitializationState.INITIALIZED) {
                callback?.onInitialized()
                return
            }

            callback?.let { callbacks.add(it) }

            if (initializationState == InitializationState.INITIALIZING) {
                return
            }

            initializationState = InitializationState.INITIALIZING
        }

        ReverseGeocoderProvider.getInstance(object : InitializationCallback<ReverseGeocoder> {
            override fun onInstance(reverseGeocoder: ReverseGeocoder) {
                synchronized(this) {
                    this@ReverseGeocoderManagerClientImpl.reverseGeocoder = reverseGeocoder
                    initializationState = InitializationState.INITIALIZED
                }
                with(callbacks) {
                    forEach { it.onInitialized() }
                    clear()
                }
            }
            override fun onError(@SygicContext.OnInitListener.Result result: Int) {
                synchronized(this) { initializationState = InitializationState.ERROR }
                with(callbacks) {
                    forEach { it.onError(result) }
                    clear()
                }
            }
        })
    }

    override fun search(position: GeoCoordinates, filter: Set<Int>, listener: ReverseGeocoder.ReverseSearchResultsListener) =
        onReady { reverseGeocoder.search(position, filter, listener) }
}