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