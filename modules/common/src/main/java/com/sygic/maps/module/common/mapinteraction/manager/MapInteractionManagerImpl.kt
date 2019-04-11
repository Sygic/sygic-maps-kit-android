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