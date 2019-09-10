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

package com.sygic.maps.module.common.utils

import android.util.SparseBooleanArray
import com.sygic.sdk.map.MapView
import com.sygic.sdk.map.PositionIndicator
import com.sygic.sdk.map.`object`.ClusterLayer
import com.sygic.sdk.map.`object`.MapObject
import com.sygic.sdk.map.data.SimpleMapDataModel
import com.sygic.sdk.map.data.observable.Observer

internal class BackingMapDataModel : SimpleMapDataModel() {

    internal fun dumpToModel(other: MapView.MapDataModel) {
        Observer<MapObject<*>> { mapObject, _ -> other.addMapObject(mapObject) }.also {
            observeObjectAddition(it)
            disposeObjectAddition(it)
        }

        Observer<ClusterLayer> { cluster, _ -> other.addClusterLayer(cluster) }.also {
            observeClusterLayerAddition(it)
            disposeClusterAddition(it)
        }

        Observer<List<String>> { skins, _ -> other.setSkin(skins) }.also {
            observeMapSkinChange(it)
            disposeMapSkinChange(it)
        }

        Observer<MapView.State> { state, _ ->
            run {
                other.setMapLanguage(state.locale)
                other.setMapSpeedUnits(state.speedUnits)
                state.geometryGroupsVisibility.forEach {
                    other.setGeometryGroupVisibility(it.first, it.second)
                }
                state.warningTypeVisibility.forEach {
                    other.setWarningsTypeVisibility(it.first, it.second)
                }
                state.radarWarningSettings?.let { other.setRadarWarningSettings(it) }
                state.mapWarningSettings?.let { other.setMapWarningSettings(it) }
            }
        }.also {
            observeMapViewStateChange(it)
            disposeMapViewStateChange(it)
        }

        Observer<PositionIndicator.State> {state, _ ->
            run {
                other.isPositionIndicatorVisible = state.visible
                other.positionIndicatorAccuracyColor = state.color
                other.isPositionIndicatorAccuracyVisible = state.accuracyIndicatorVisible
                other.positionIndicatorRotation = state.rotation
                other.positionIndicatorType = state.type
            }
        }.also {
            observePositionIndicatorChange(it)
            disposePositionIndicatorChange(it)
        }
    }
}

private inline fun SparseBooleanArray.forEach(action: (Pair<Int, Boolean>) -> Unit) {
    for (i in 0 until size()) {
        action(Pair(keyAt(i), valueAt(i)))
    }
}