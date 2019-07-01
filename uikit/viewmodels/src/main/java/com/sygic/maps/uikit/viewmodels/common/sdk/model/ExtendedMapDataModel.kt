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

package com.sygic.maps.uikit.viewmodels.common.sdk.model

import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.sygic.sdk.map.`object`.MapMarker
import com.sygic.sdk.map.`object`.MapObject
import com.sygic.sdk.map.`object`.MapRoute
import com.sygic.sdk.map.data.SimpleMapDataModel
import java.util.*
import kotlin.collections.HashSet

@Suppress("MemberVisibilityCanBePrivate", "unused")
object ExtendedMapDataModel : SimpleMapDataModel(), DefaultLifecycleObserver {

    private var currentOnClickMapMarker: MapMarker? = null
    private val userMapObjects: MutableSet<MapObject<*>> = HashSet()

    fun addMapMarker(marker: MapMarker) {
        userMapObjects.add(marker)
        addMapObject(marker)
    }

    fun removeMapMarker(marker: MapMarker) {
        userMapObjects.remove(marker)
        removeMapObject(marker)
    }

    fun addMapRoute(mapRoute: MapRoute) {
        userMapObjects.add(mapRoute)
        addMapObject(mapRoute)
    }

    fun setMapRoute(mapRoute: MapRoute) {
        removeAllMapRoutes()
        addMapRoute(mapRoute)
    }

    fun removeMapRoute(mapRoute: MapRoute) {
        userMapObjects.remove(mapRoute)
        removeMapObject(mapRoute)
    }

    fun removeAllMapMarkers() = removeAllMapObjects<MapMarker>()

    fun removeAllMapRoutes() = removeAllMapObjects<MapRoute>()

    fun getUserMapObjects(): Set<MapObject<*>> = Collections.unmodifiableSet(userMapObjects)

    fun addOnClickMapMarker(onClickMapMarker: MapMarker) {
        currentOnClickMapMarker = onClickMapMarker
        addMapObject(onClickMapMarker)
    }

    fun removeOnClickMapMarker() {
        currentOnClickMapMarker?.let { removeMapObject(it) }
    }

    private inline fun <reified T : MapObject<*>> removeAllMapObjects() = with(userMapObjects) {
        forEach {
            if (it is T) {
                removeMapObject(it)
                remove(it)
            }
        }
    }

    override fun onDestroy(owner: LifecycleOwner) {
        if (owner is Fragment) owner.activity?.run { if (isFinishing) clear() }
    }
}