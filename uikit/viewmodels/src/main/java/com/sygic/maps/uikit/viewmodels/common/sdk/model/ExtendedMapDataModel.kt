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
import com.sygic.sdk.map.data.SimpleMapDataModel
import java.util.*
import kotlin.collections.HashSet

object ExtendedMapDataModel : SimpleMapDataModel(), DefaultLifecycleObserver {

    private var currentOnClickMapMarker: MapMarker? = null
    private val userMapMarkers: MutableSet<MapMarker> = HashSet()

    fun addMapMarker(marker: MapMarker) {
        userMapMarkers.add(marker)
        addMapObject(marker)
    }

    fun removeMapMarker(marker: MapMarker) {
        userMapMarkers.remove(marker)
        removeMapObject(marker)
    }

    fun removeAllMapMarkers() {
        with(userMapMarkers) {
            forEach { removeMapObject(it) }
            clear()
        }
    }

    fun getUserMapMarkers(): Set<MapMarker> = Collections.unmodifiableSet(userMapMarkers)

    fun addOnClickMapMarker(onClickMapMarker: MapMarker) {
        currentOnClickMapMarker = onClickMapMarker
        addMapObject(onClickMapMarker)
    }

    fun removeOnClickMapMarker() {
        currentOnClickMapMarker?.let { removeMapObject(it) }
    }

    override fun onDestroy(owner: LifecycleOwner) {
        if (owner is Fragment) owner.activity?.run { if (isFinishing) clear() }
    }
}