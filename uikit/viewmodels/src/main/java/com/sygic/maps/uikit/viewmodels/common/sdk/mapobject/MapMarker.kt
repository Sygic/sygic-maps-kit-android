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

package com.sygic.maps.uikit.viewmodels.common.sdk.mapobject

import android.graphics.Bitmap
import androidx.annotation.DrawableRes
import com.sygic.maps.uikit.viewmodels.R
import com.sygic.maps.uikit.viewmodels.common.data.PoiData
import com.sygic.sdk.map.`object`.BitmapFactory
import com.sygic.sdk.map.`object`.ViewObject
import com.sygic.sdk.map.factory.DrawableFactory
import com.sygic.sdk.map.factory.SimpleBitmapFactory
import com.sygic.sdk.position.GeoCoordinates

private const val MARKER_ANCHOR_POSITION_X = 0.5f
private const val MARKER_ANCHOR_POSITION_Y = 1f

private val DEFAULT_ICON = R.drawable.ic_map_pin

// ToDo: Custom data / payload MS-4711
// ToDo: refactor SDK MapMarker, then remove it
@Suppress("unused", "MemberVisibilityCanBePrivate")
open class MapMarker : com.sygic.sdk.map.`object`.MapMarker {

    constructor(viewObject: ViewObject) : super(viewObject, DrawableFactory(DEFAULT_ICON))
    constructor(latitude: Double, longitude: Double) : super(GeoCoordinates(latitude, longitude), DrawableFactory(DEFAULT_ICON)) { this.data.coordinates = GeoCoordinates(latitude, longitude) }
    constructor(latitude: Double, longitude: Double, icon: BitmapFactory) : super(GeoCoordinates(latitude, longitude), icon) { this.data.coordinates = GeoCoordinates(latitude, longitude) }
    constructor(latitude: Double, longitude: Double, icon: BitmapFactory, data: PoiData) : super(GeoCoordinates(latitude, longitude), icon) { this.data = data }

    var data: PoiData = PoiData.EMPTY.copy()

    init {
        setAnchorPosition(MARKER_ANCHOR_POSITION_X, MARKER_ANCHOR_POSITION_Y)
    }

    class Builder {

        private val data: PoiData = PoiData.EMPTY.copy()
        private var bitmapFactory: BitmapFactory = DrawableFactory(DEFAULT_ICON)

        fun coordinates(latitude: Double, longitude: Double): Builder = coordinates(GeoCoordinates(latitude, longitude))
        fun coordinates(coordinates: GeoCoordinates): Builder = this.apply { data.coordinates = coordinates }
        fun iconDrawable(@DrawableRes iconDrawable: Int): Builder = this.apply { this.bitmapFactory = DrawableFactory(iconDrawable) }
        fun iconBitmap(iconBitmap: Bitmap): Builder = this.apply { this.bitmapFactory = SimpleBitmapFactory(iconBitmap) }
        fun title(title: String): Builder = this.apply { data.name = title }
        fun build(): MapMarker = MapMarker(data.coordinates.latitude, data.coordinates.longitude, bitmapFactory, data)
    }
}