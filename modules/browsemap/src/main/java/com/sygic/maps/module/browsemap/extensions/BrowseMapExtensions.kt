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

package com.sygic.maps.module.browsemap.extensions

import android.annotation.SuppressLint
import android.app.Application
import com.sygic.maps.module.browsemap.R
import com.sygic.maps.module.common.component.*

@SuppressLint("Recycle")
fun MapFragmentInitComponent.resolveAttributes(app: Application) {
    app.obtainStyledAttributes(attributes, R.styleable.BrowseMapFragment)?.let { typedArray ->
        if (typedArray.hasValue(R.styleable.BrowseMapFragment_sygic_map_selectionMode)) {
            mapSelectionMode =
                typedArray.getInt(
                    R.styleable.BrowseMapFragment_sygic_map_selectionMode,
                    MAP_SELECTION_MODE_DEFAULT_VALUE
                )
        }
        if (typedArray.hasValue(R.styleable.BrowseMapFragment_sygic_positionOnMap_enabled)) {
            positionOnMapEnabled =
                typedArray.getBoolean(
                    R.styleable.BrowseMapFragment_sygic_positionOnMap_enabled,
                    POSITION_ON_MAP_ENABLED_DEFAULT_VALUE
                )
        }
        if (typedArray.hasValue(R.styleable.BrowseMapFragment_sygic_compass_enabled)) {
            compassEnabled =
                typedArray.getBoolean(
                    R.styleable.BrowseMapFragment_sygic_compass_enabled,
                    COMPASS_ENABLED_DEFAULT_VALUE
                )
        }
        if (typedArray.hasValue(R.styleable.BrowseMapFragment_sygic_compass_hideIfNorthUp)) {
            compassHideIfNorthUp =
                typedArray.getBoolean(
                    R.styleable.BrowseMapFragment_sygic_compass_hideIfNorthUp,
                    COMPASS_HIDE_IF_NORTH_UP_DEFAULT_VALUE
                )
        }
        if (typedArray.hasValue(R.styleable.BrowseMapFragment_sygic_positionLockFab_enabled)) {
            positionLockFabEnabled =
                typedArray.getBoolean(
                    R.styleable.BrowseMapFragment_sygic_positionLockFab_enabled,
                    POSITION_LOCK_FAB_ENABLED_DEFAULT_VALUE
                )
        }
        if (typedArray.hasValue(R.styleable.BrowseMapFragment_sygic_search_enabled)) {
            searchEnabled =
                typedArray.getBoolean(
                    R.styleable.BrowseMapFragment_sygic_search_enabled,
                    SEARCH_ENABLED_DEFAULT_VALUE
                )
        }
        if (typedArray.hasValue(R.styleable.BrowseMapFragment_sygic_zoomControls_enabled)) {
            zoomControlsEnabled =
                typedArray.getBoolean(
                    R.styleable.BrowseMapFragment_sygic_zoomControls_enabled,
                    ZOOM_CONTROLS_ENABLED_DEFAULT_VALUE
                )
        }

        typedArray.recycle()
    }
}