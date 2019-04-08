package com.sygic.maps.module.common.mapinteraction.manager

import android.view.MotionEvent
import androidx.annotation.RestrictTo
import com.sygic.sdk.map.MapView
import com.sygic.sdk.map.mapgesturesdetector.listener.MapGestureAdapter
import java.util.*

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
class MapInteractionManagerImpl : MapInteractionManager {

    private val listeners = LinkedHashSet<MapInteractionManager.Listener>()

    override fun onMapReady(mapView: MapView) {
        mapView.addMapGestureListener(object : MapGestureAdapter() {
            override fun onMapClicked(motionEvent: MotionEvent, isTwoFingers: Boolean): Boolean {
                if (isTwoFingers) {
                    return false
                }

                listeners.forEach { it.onMapObjectsRequestStarted() }
                mapView.requestObjectsAtPoint(motionEvent.x, motionEvent.y) { list, _, _, _ ->
                    listeners.forEach { it.onMapObjectsReceived(list) }
                }
                return super.onMapClicked(motionEvent, false)
            }
        })
    }

    override fun addOnMapClickListener(listener: MapInteractionManager.Listener) {
        listeners.add(listener)
    }

    override fun removeOnMapClickListener(listener: MapInteractionManager.Listener) {
        listeners.remove(listener)
    }
}