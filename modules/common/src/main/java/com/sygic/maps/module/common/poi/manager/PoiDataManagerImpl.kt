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

package com.sygic.maps.module.common.poi.manager

import androidx.annotation.RestrictTo
import com.sygic.sdk.map.`object`.ProxyObject
import com.sygic.sdk.map.`object`.ProxyPoi
import com.sygic.sdk.map.`object`.ViewObject
import com.sygic.sdk.map.`object`.data.payload.EmptyPayload
import com.sygic.sdk.places.Places
import com.sygic.sdk.search.ReverseGeocoder

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
class PoiDataManagerImpl : PoiDataManager {

    private val places: Places by lazy { Places() }
    private val reverseGeocoder: ReverseGeocoder by lazy { ReverseGeocoder() }

    override fun getViewObjectData(viewObject: ViewObject<*>, callback: PoiDataManager.Callback) {
        when (viewObject.objectType) {
            ViewObject.ObjectType.Map -> {
                viewObject.data.payload.let { payload ->
                    when (payload) {
                        is ProxyPoi -> getViewObjectData(payload, callback)
                        else -> handleDefaultState(viewObject, callback)
                    }
                }
            }

            ViewObject.ObjectType.Proxy -> {
                when ((viewObject as ProxyObject<*>).proxyObjectType) {
                    ProxyObject.ProxyObjectType.Poi -> places.loadPoiObject(viewObject as ProxyPoi, callback)
                    else -> handleDefaultState(viewObject, callback)
                }
            }

            ViewObject.ObjectType.Screen, ViewObject.ObjectType.Unknown -> {
                handleDefaultState(viewObject, callback)
            }
        }
    }

    private fun handleDefaultState(viewObject: ViewObject<*>, callback: PoiDataManager.Callback) {
        if (viewObject.data.payload is EmptyPayload) {
            reverseGeocoder.search(viewObject.position, callback)
        } else {
            callback.onDataLoaded(viewObject.data)
        }
    }
}