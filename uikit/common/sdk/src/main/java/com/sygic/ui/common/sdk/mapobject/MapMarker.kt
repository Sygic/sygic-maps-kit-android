package com.sygic.ui.common.sdk.mapobject

import android.graphics.Bitmap
import androidx.annotation.DrawableRes
import com.sygic.sdk.map.`object`.BitmapFactory
import com.sygic.sdk.map.`object`.ViewObject
import com.sygic.sdk.map.factory.DrawableFactory
import com.sygic.sdk.map.factory.SimpleBitmapFactory
import com.sygic.sdk.position.GeoCoordinates
import com.sygic.ui.common.sdk.R
import com.sygic.ui.common.sdk.data.PoiData

private const val MARKER_ANCHOR_POSITION_X = 0.5f
private const val MARKER_ANCHOR_POSITION_Y = 1f

private val DEFAULT_ICON = DrawableFactory(R.drawable.ic_map_pin)

// ToDo: Custom data / payload
// ToDo: refactor SDK MapMarker, then remove it
@Suppress("unused", "MemberVisibilityCanBePrivate")
open class MapMarker : com.sygic.sdk.map.`object`.MapMarker {

    constructor(viewObject: ViewObject) : super(viewObject, DEFAULT_ICON) { this.data.coordinates = viewObject.position }
    constructor(latitude: Double, longitude: Double) : super(GeoCoordinates(latitude, longitude), DEFAULT_ICON) { this.data.coordinates = GeoCoordinates(latitude, longitude) }
    constructor(latitude: Double, longitude: Double, icon: BitmapFactory) : super(GeoCoordinates(latitude, longitude), icon) { this.data.coordinates = GeoCoordinates(latitude, longitude) }
    constructor(latitude: Double, longitude: Double, icon: BitmapFactory, data: PoiData) : super(GeoCoordinates(latitude, longitude), icon) { this.data = data }

    var data: PoiData = PoiData()

    init {
        setAnchorPosition(MARKER_ANCHOR_POSITION_X, MARKER_ANCHOR_POSITION_Y)
    }

    class Builder {

        private val data: PoiData = PoiData()
        private var bitmapFactory: BitmapFactory = DEFAULT_ICON

        fun coordinates(latitude: Double, longitude: Double): Builder {
            return coordinates(GeoCoordinates(latitude, longitude))
        }

        fun coordinates(coordinates: GeoCoordinates): Builder {
            data.coordinates = coordinates
            return this
        }

        fun iconDrawable(@DrawableRes iconDrawable: Int): Builder {
            this.bitmapFactory = DrawableFactory(iconDrawable)
            return this
        }

        fun iconBitmap(iconBitmap: Bitmap): Builder {
            this.bitmapFactory = SimpleBitmapFactory(iconBitmap)
            return this
        }

        fun title(title: String): Builder {
            data.name = title
            return this
        }

        fun build(): MapMarker {
            return MapMarker(data.coordinates.latitude, data.coordinates.longitude, bitmapFactory, data)
        }
    }
}