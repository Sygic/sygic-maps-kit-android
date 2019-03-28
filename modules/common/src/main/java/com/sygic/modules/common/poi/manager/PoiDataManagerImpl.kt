package com.sygic.modules.common.poi.manager

import androidx.annotation.RestrictTo
import com.sygic.sdk.map.`object`.*
import com.sygic.sdk.map.`object`.data.payload.EmptyPayload
import com.sygic.sdk.places.Places
import com.sygic.sdk.search.ReverseGeocoder

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
class PoiDataManagerImpl : PoiDataManager {

    private val places: Places by lazy { Places() }
    private val reverseGeocoder: ReverseGeocoder by lazy { ReverseGeocoder() }

    override fun getPayloadData(viewObject: ViewObject, callback: PoiDataManager.Callback) {
        when (viewObject.objectType) {
            ViewObject.ObjectType.Map -> {
                if (viewObject is MapMarker) {
                    viewObject.markerData.payload.let { payload ->
                        when (payload) {
                            is ProxyPoi -> getPayloadData(payload, callback)
                            is UiObject, is EmptyPayload -> reverseGeocoder.search(viewObject.position, callback)
                            else -> callback.onDataLoaded(viewObject.markerData)
                        }
                    }
                    return
                }

                reverseGeocoder.search(viewObject.position, callback)
            }

            ViewObject.ObjectType.Proxy -> {
                when ((viewObject as ProxyObject).proxyObjectType) {
                    ProxyObject.ProxyObjectType.Poi -> places.loadPoiObject(viewObject as ProxyPoi, callback)
                    else -> reverseGeocoder.search(viewObject.position, callback)
                }
            }

            ViewObject.ObjectType.Screen, ViewObject.ObjectType.Unknown -> {
                reverseGeocoder.search(viewObject.position, callback)
            }
        }
    }
}