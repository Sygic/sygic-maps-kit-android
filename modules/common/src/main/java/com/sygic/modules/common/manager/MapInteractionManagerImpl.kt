package com.sygic.modules.common.manager

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

                listeners.forEach { it.onMapObjectRequestStarted() }
                mapView.requestObjectsAtPoint(motionEvent.x, motionEvent.y) { list, _, _, _ ->
                    listeners.forEach { it.onMapObjectReceived(list.first()) }
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