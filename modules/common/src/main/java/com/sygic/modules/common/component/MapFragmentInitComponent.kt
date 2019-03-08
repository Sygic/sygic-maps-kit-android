package com.sygic.modules.common.component

import android.util.AttributeSet
import androidx.annotation.RestrictTo
import com.sygic.modules.common.detail.DetailsViewFactory
import com.sygic.modules.common.mapinteraction.MapSelectionMode
import com.sygic.modules.common.theme.ThemeManager
import com.sygic.ui.common.sdk.listener.OnMapClickListener

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
    }
}