package com.sygic.modules.browsemap.extensions

import android.annotation.SuppressLint
import android.util.AttributeSet
import com.sygic.modules.browsemap.BrowseMapFragment
import com.sygic.modules.browsemap.R
import com.sygic.modules.common.component.*

@SuppressLint("Recycle")
fun BrowseMapFragment.resolveAttributes(
    mapFragmentComponent: MapFragmentComponent,
    attrs: AttributeSet?
) {
    context?.obtainStyledAttributes(attrs, R.styleable.BrowseMapFragment)?.let { typedArray ->
        if (typedArray.hasValue(R.styleable.BrowseMapFragment_sygic_map_selectionMode)) {
            mapFragmentComponent.mapSelectionMode =
                typedArray.getInt(
                    R.styleable.BrowseMapFragment_sygic_map_selectionMode,
                    MAP_SELECTION_MODE_DEFAULT_VALUE
                )
        }
        if (typedArray.hasValue(R.styleable.BrowseMapFragment_sygic_positionOnMap_enabled)) {
            mapFragmentComponent.positionOnMapEnabled =
                typedArray.getBoolean(
                    R.styleable.BrowseMapFragment_sygic_positionOnMap_enabled,
                    POSITION_ON_MAP_ENABLED_DEFAULT_VALUE
                )
        }
        if (typedArray.hasValue(R.styleable.BrowseMapFragment_sygic_compass_enabled)) {
            mapFragmentComponent.compassEnabled.value =
                typedArray.getBoolean(
                    R.styleable.BrowseMapFragment_sygic_compass_enabled,
                    COMPASS_ENABLED_DEFAULT_VALUE
                )
        }
        if (typedArray.hasValue(R.styleable.BrowseMapFragment_sygic_compass_hideIfNorthUp)) {
            mapFragmentComponent.compassHideIfNorthUp.value =
                typedArray.getBoolean(
                    R.styleable.BrowseMapFragment_sygic_compass_hideIfNorthUp,
                    COMPASS_HIDE_IF_NORTH_UP_DEFAULT_VALUE
                )
        }
        if (typedArray.hasValue(R.styleable.BrowseMapFragment_sygic_positionLockFab_enabled)) {
            mapFragmentComponent.positionLockFabEnabled.value =
                typedArray.getBoolean(
                    R.styleable.BrowseMapFragment_sygic_positionLockFab_enabled,
                    POSITION_LOCK_FAB_ENABLED_DEFAULT_VALUE
                )
        }
        if (typedArray.hasValue(R.styleable.BrowseMapFragment_sygic_zoomControls_enabled)) {
            mapFragmentComponent.zoomControlsEnabled.value =
                typedArray.getBoolean(
                    R.styleable.BrowseMapFragment_sygic_zoomControls_enabled,
                    ZOOM_CONTROLS_ENABLED_DEFAULT_VALUE
                )
        }

        typedArray.recycle()
    }
}