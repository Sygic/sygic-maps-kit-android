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