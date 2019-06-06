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

package com.sygic.maps.uikit.viewmodels.common.extensions

import android.os.Parcelable
import com.sygic.maps.uikit.viewmodels.common.data.BasicData
import com.sygic.maps.uikit.viewmodels.common.data.PoiData
import com.sygic.maps.uikit.viewmodels.common.sdk.viewobject.SelectionType
import com.sygic.maps.uikit.views.common.extensions.EMPTY_STRING
import com.sygic.maps.uikit.views.poidetail.data.PoiDetailData
import com.sygic.sdk.map.`object`.MapMarker
import com.sygic.sdk.map.`object`.MapObject
import com.sygic.sdk.map.`object`.ProxyObject
import com.sygic.sdk.map.`object`.ViewObject
import com.sygic.sdk.map.`object`.data.ViewObjectData
import com.sygic.sdk.map.`object`.data.payload.EmptyPayload
import com.sygic.sdk.places.LocationInfo
import com.sygic.sdk.position.GeoCoordinates
import java.util.*

@SelectionType
fun ViewObject<*>.getSelectionType(): Int {
    return when (this) {
        is MapObject<*> -> {
            return when (this.mapObjectType) {
                MapObject.MapObjectType.Marker -> SelectionType.MARKER
                MapObject.MapObjectType.Route -> SelectionType.ROUTE
                else -> SelectionType.OTHER
            }
        }
        is ProxyObject<*> -> {
            return when (this.proxyObjectType) {
                ProxyObject.ProxyObjectType.Poi -> SelectionType.POI
                else -> SelectionType.OTHER
            }
        }
        else -> SelectionType.OTHER
    }
}

fun LocationInfo.getFirst(@LocationInfo.LocationType locationType: Int): String? {
    // for each type of POI information, there could be multiple results, for instance multiple mail or phone info - get first
    return locationData?.get(locationType)?.firstOrNull()
}

fun GeoCoordinates.getFormattedLocation(): String {
    if (!isValid) {
        return EMPTY_STRING
    }

    return String.format(Locale.US, "%.6f, %.6f", latitude, longitude)
}

fun ViewObjectData.toPoiDetailData(): PoiDetailData {
    val coordinatesText: String = position.getFormattedLocation()
    val titleText: String = if (payload is BasicData) (payload as BasicData).title else coordinatesText
    val subtitleText: String = if (payload is BasicData) (payload as BasicData).description else EMPTY_STRING
    val urlText: String? = if (payload is PoiData) (payload as PoiData).url else null
    val emailText: String? = if (payload is PoiData) (payload as PoiData).email else null
    val phoneText: String? = if (payload is PoiData) (payload as PoiData).phone else null
    return PoiDetailData(titleText, subtitleText, urlText, emailText, phoneText, coordinatesText)
}

fun MapMarker.getCopyWithPayload(payload: Parcelable): MapMarker {
    if (data.payload !is EmptyPayload) {
        return this
    }

    return MapMarker
        .at(position)
        .withLabel(data.label)
        .withIcon(data.bitmapFactory)
        .setAnchorPosition(data.anchorPosition)
        .setClickableArea(
            data.clickableArea.left,
            data.clickableArea.top,
            data.clickableArea.right,
            data.clickableArea.bottom
        )
        .setMinZoomLevel(data.minZoomLevel)
        .setMaxZoomLevel(data.maxZoomLevel)
        .withPayload(payload)
        .build()
}