package com.sygic.ui.common.sdk.mapobject

import com.sygic.sdk.map.`object`.MapMarker
import com.sygic.sdk.map.`object`.ViewObject
import com.sygic.sdk.map.factory.DrawableFactory
import com.sygic.ui.common.sdk.R

private const val MARKER_ANCHOR_POSITION_X = 0.5f
private const val MARKER_ANCHOR_POSITION_Y = 1f

class DefaultMapMarker(viewObject: ViewObject) : MapMarker(viewObject, DrawableFactory(R.drawable.ic_map_pin)) {

    init {
        setAnchorPosition(MARKER_ANCHOR_POSITION_X, MARKER_ANCHOR_POSITION_Y)
    }
}