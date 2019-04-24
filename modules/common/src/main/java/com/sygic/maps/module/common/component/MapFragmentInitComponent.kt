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

package com.sygic.maps.module.common.component

import android.util.AttributeSet
import androidx.annotation.RestrictTo
import com.sygic.maps.module.common.detail.DetailsViewFactory
import com.sygic.maps.module.common.mapinteraction.MapSelectionMode
import com.sygic.maps.module.common.theme.ThemeManager
import com.sygic.maps.module.common.listener.OnMapClickListener
import com.sygic.maps.module.common.provider.ModuleConnectionProvider

const val MAP_SELECTION_MODE_DEFAULT_VALUE = MapSelectionMode.MARKERS_ONLY
const val POSITION_ON_MAP_ENABLED_DEFAULT_VALUE = false
const val COMPASS_ENABLED_DEFAULT_VALUE = false
const val COMPASS_HIDE_IF_NORTH_UP_DEFAULT_VALUE = false
const val POSITION_LOCK_FAB_ENABLED_DEFAULT_VALUE = false
const val ZOOM_CONTROLS_ENABLED_DEFAULT_VALUE = false

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
class MapFragmentInitComponent {

    var attributes: AttributeSet? = null

    @MapSelectionMode
    var mapSelectionMode: Int = MAP_SELECTION_MODE_DEFAULT_VALUE
    var positionOnMapEnabled: Boolean = POSITION_ON_MAP_ENABLED_DEFAULT_VALUE
    var compassEnabled: Boolean = COMPASS_ENABLED_DEFAULT_VALUE
    var compassHideIfNorthUp: Boolean = COMPASS_HIDE_IF_NORTH_UP_DEFAULT_VALUE
    var positionLockFabEnabled: Boolean = POSITION_LOCK_FAB_ENABLED_DEFAULT_VALUE
    var zoomControlsEnabled: Boolean = ZOOM_CONTROLS_ENABLED_DEFAULT_VALUE

    val skins: MutableMap<ThemeManager.SkinLayer, String> = mutableMapOf()

    var onMapClickListener: OnMapClickListener? = null
    var detailsViewFactory: DetailsViewFactory? = null
    var searchConnectionProvider: ModuleConnectionProvider? = null

    fun recycle() {
        attributes = null

        mapSelectionMode = MAP_SELECTION_MODE_DEFAULT_VALUE
        positionOnMapEnabled = POSITION_ON_MAP_ENABLED_DEFAULT_VALUE
        compassEnabled = COMPASS_ENABLED_DEFAULT_VALUE
        compassHideIfNorthUp = COMPASS_HIDE_IF_NORTH_UP_DEFAULT_VALUE
        positionLockFabEnabled = POSITION_LOCK_FAB_ENABLED_DEFAULT_VALUE
        zoomControlsEnabled = ZOOM_CONTROLS_ENABLED_DEFAULT_VALUE

        skins.clear()

        onMapClickListener = null
        detailsViewFactory = null
        searchConnectionProvider = null
    }
}