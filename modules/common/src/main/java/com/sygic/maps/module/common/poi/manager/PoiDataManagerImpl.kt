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
import com.sygic.maps.uikit.viewmodels.common.data.PoiData
import com.sygic.sdk.map.`object`.ProxyObject
import com.sygic.sdk.map.`object`.ProxyPoi
import com.sygic.sdk.map.`object`.ViewObject
import com.sygic.sdk.places.Places
import com.sygic.sdk.search.ReverseGeocoder
import com.sygic.maps.uikit.viewmodels.common.sdk.mapobject.MapMarker

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
class PoiDataManagerImpl : PoiDataManager {

    private val places: Places by lazy { Places() }
    private val reverseGeocoder: ReverseGeocoder by lazy { ReverseGeocoder() }

    override fun getPoiData(viewObject: ViewObject, callback: PoiDataManager.Callback) {
        when (viewObject.objectType) {
            ViewObject.ObjectType.Map -> {
                if (viewObject is MapMarker) {
                    // ToDo: remove this checks when Marker refactor in MS-4711 is done
                    if (viewObject.data == PoiData.EMPTY) {
                        getPoiData(viewObject.payload, callback)
                    } else {
                        callback.onDataLoaded(viewObject.data)
                    }
                    return
                }

                reverseGeocoder.search(viewObject.position, callback)
            }
            ViewObject.ObjectType.Proxy -> {
                when ((viewObject as ProxyObject).proxyObjectType) {
                    ProxyObject.ProxyObjectType.Poi -> {
                        places.loadPoiObject(viewObject as ProxyPoi, callback)
                    }
                    else -> reverseGeocoder.search(viewObject.position, callback)
                }
            }
            ViewObject.ObjectType.Screen, ViewObject.ObjectType.Unknown -> {
                reverseGeocoder.search(viewObject.position, callback)
            }
        }
    }
}