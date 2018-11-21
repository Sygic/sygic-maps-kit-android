package com.sygic.modules.common.mapinteraction.manager

import androidx.annotation.RestrictTo
import com.sygic.sdk.map.MapView
import com.sygic.sdk.map.`object`.ViewObject

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
interface MapInteractionManager {

    interface Listener {
        fun onMapObjectsRequestStarted()
        fun onMapObjectsReceived(viewObjects: List<ViewObject>)
    }

    fun onMapReady(mapView: MapView)
    fun addOnMapClickListener(listener: Listener)
    fun removeOnMapClickListener(listener: Listener)
}