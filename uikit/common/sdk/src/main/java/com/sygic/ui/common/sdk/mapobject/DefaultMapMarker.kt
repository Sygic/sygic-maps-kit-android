package com.sygic.ui.common.sdk.mapobject

import com.sygic.sdk.map.`object`.MapMarker
import com.sygic.sdk.map.`object`.ViewObject
import com.sygic.sdk.map.factory.DrawableFactory
import com.sygic.sdk.position.GeoCoordinates
import com.sygic.ui.common.sdk.R

private const val MARKER_ANCHOR_POSITION_X = 0.5f
private const val MARKER_ANCHOR_POSITION_Y = 1f

class DefaultMapMarker : MapMarker {

    constructor(latitude: Double, longitude: Double) : this(GeoCoordinates(latitude, longitude))
    constructor(position: GeoCoordinates) : super(position, DrawableFactory(R.drawable.ic_map_pin))
    constructor(viewObject: ViewObject) : super(viewObject, DrawableFactory(R.drawable.ic_map_pin))

    init {
        setAnchorPosition(MARKER_ANCHOR_POSITION_X, MARKER_ANCHOR_POSITION_Y)
    }
}