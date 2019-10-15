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

package com.sygic.maps.uikit.viewmodels.common.place

import androidx.annotation.RestrictTo
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sygic.maps.uikit.viewmodels.common.geocoder.ReverseGeocoderManagerClient
import com.sygic.maps.uikit.viewmodels.common.initialization.InitializationCallback
import com.sygic.maps.uikit.views.common.extensions.observeOnce
import com.sygic.maps.uikit.views.common.utils.SingletonHolder
import com.sygic.sdk.map.`object`.ProxyObject
import com.sygic.sdk.map.`object`.ProxyObjectManager
import com.sygic.sdk.map.`object`.ProxyPlace
import com.sygic.sdk.map.`object`.ViewObject
import com.sygic.sdk.map.`object`.data.payload.EmptyPayload
import com.sygic.sdk.places.PlaceLink
import com.sygic.sdk.places.PlacesManager
import com.sygic.sdk.places.PlacesManagerProvider

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
class PlacesManagerClientImpl private constructor(
    private val reverseGeocoderManagerClient: ReverseGeocoderManagerClient
) : PlacesManagerClient {

    companion object : SingletonHolder<PlacesManagerClientImpl>() {
        @JvmStatic
        fun getInstance(client: ReverseGeocoderManagerClient) = getInstance { PlacesManagerClientImpl(client) }
    }

    private val managerProvider: LiveData<PlacesManager> = object : MutableLiveData<PlacesManager>() {
        init { PlacesManagerProvider.getInstance(InitializationCallback<PlacesManager> { value = it }) }
    }

    override fun loadPlace(link: PlaceLink, listener: PlacesManager.PlaceListener) =
        managerProvider.observeOnce { it.loadPlace(link, listener) }

    override fun loadPlaceLink(proxyPlace: ProxyPlace, listener: ProxyObjectManager.PlaceLinkListener) =
        ProxyObjectManager.loadPlaceLink(proxyPlace, listener)

    override fun getViewObjectData(viewObject: ViewObject<*>, callback: PlacesManagerClient.Callback) {
        when (viewObject.objectType) {
            ViewObject.ObjectType.Map -> {
                viewObject.data.payload.let { payload ->
                    when (payload) {
                        is ProxyPlace -> getViewObjectData(
                            payload,
                            callback
                        )
                        else -> handleDefaultState(
                            viewObject,
                            callback
                        )
                    }
                }
            }

            ViewObject.ObjectType.Proxy -> {
                when ((viewObject as ProxyObject<*>).proxyObjectType) {
                    ProxyObject.ProxyObjectType.Place -> {
                        loadPlaceLink(viewObject as ProxyPlace, object : ProxyObjectManager.PlaceLinkListener {
                            override fun onPlaceLinkLoaded(link: PlaceLink) { loadPlace(link, callback) }
                            override fun onPlaceLinkError(eCode: PlacesManager.ErrorCode) { /* Currently do nothing */ }
                        })
                    }
                    else -> handleDefaultState(
                        viewObject,
                        callback
                    )
                }
            }

            ViewObject.ObjectType.Screen, ViewObject.ObjectType.Unknown -> {
                handleDefaultState(viewObject, callback)
            }
        }
    }

    private fun handleDefaultState(viewObject: ViewObject<*>, callback: PlacesManagerClient.Callback) {
        if (viewObject.data.payload is EmptyPayload) {
            reverseGeocoderManagerClient.search(viewObject.position, emptySet(), callback)
        } else {
            callback.onDataLoaded(viewObject.data)
        }
    }
}