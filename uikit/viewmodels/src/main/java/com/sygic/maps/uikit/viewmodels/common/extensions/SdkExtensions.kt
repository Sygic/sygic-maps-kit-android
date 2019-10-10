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
import androidx.annotation.DrawableRes
import com.sygic.maps.uikit.viewmodels.R
import com.sygic.maps.uikit.viewmodels.common.data.BasicData
import com.sygic.maps.uikit.viewmodels.common.data.PlaceData
import com.sygic.maps.uikit.viewmodels.common.sdk.search.CoordinateSearchResultItem
import com.sygic.maps.uikit.viewmodels.common.sdk.search.map.*
import com.sygic.maps.uikit.viewmodels.common.sdk.viewobject.SelectionType
import com.sygic.maps.uikit.viewmodels.navigation.signpost.direction.DirectionManeuverType
import com.sygic.maps.uikit.views.common.extensions.EMPTY_STRING
import com.sygic.maps.uikit.views.common.units.DistanceUnit
import com.sygic.maps.uikit.views.common.utils.Distance
import com.sygic.maps.uikit.views.common.utils.TextHolder
import com.sygic.maps.uikit.views.navigation.roadsign.data.RoadSignData
import com.sygic.maps.uikit.views.placedetail.data.PlaceDetailData
import com.sygic.maps.uikit.views.searchresultlist.data.SearchResultItem
import com.sygic.sdk.map.Camera
import com.sygic.sdk.map.MapRectangle
import com.sygic.sdk.map.MapView
import com.sygic.sdk.map.`object`.*
import com.sygic.sdk.map.`object`.data.ViewObjectData
import com.sygic.sdk.map.`object`.data.payload.EmptyPayload
import com.sygic.sdk.navigation.routeeventnotifications.DirectionInfo
import com.sygic.sdk.navigation.routeeventnotifications.SignpostInfo
import com.sygic.sdk.navigation.routeeventnotifications.SignpostInfo.SignElement.SignElementType
import com.sygic.sdk.places.PlaceDetail
import com.sygic.sdk.position.GeoBoundingBox
import com.sygic.sdk.position.GeoCoordinates
import com.sygic.sdk.search.CoordinateSearchResult
import com.sygic.sdk.search.MapSearchResult
import com.sygic.sdk.search.SearchResult
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
                ProxyObject.ProxyObjectType.Place -> SelectionType.PLACE
                else -> SelectionType.OTHER
            }
        }
        else -> SelectionType.OTHER
    }
}

fun List<PlaceDetail>.getFirst(attr: String): String? {
    // for each type of Place information, there could be multiple results, for instance multiple mail or phone info - get first
    return firstOrNull { it.first == attr }?.second
}

fun GeoCoordinates.getFormattedLocation(): String {
    if (!isValid) {
        return EMPTY_STRING
    }

    return String.format(Locale.US, "%.6f, %.6f", latitude, longitude)
}

fun ViewObjectData.toPlaceDetailData(): PlaceDetailData {
    val coordinatesText: String = position.getFormattedLocation()
    val titleText: String = if (payload is BasicData) (payload as BasicData).title else coordinatesText
    val subtitleText: String = if (payload is BasicData) (payload as BasicData).description else EMPTY_STRING
    val urlText: String? = if (payload is PlaceData) (payload as PlaceData).url else null
    val emailText: String? = if (payload is PlaceData) (payload as PlaceData).email else null
    val phoneText: String? = if (payload is PlaceData) (payload as PlaceData).phone else null
    return PlaceDetailData(titleText, subtitleText, urlText, emailText, phoneText, coordinatesText)
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

fun MapView.MapDataModel.addMapMarker(marker: MapMarker) {
    addMapObject(marker)
}

fun MapView.MapDataModel.addMapMarker(position: GeoCoordinates) {
    addMapObject(MapMarker.at(position).build())
}

fun MapView.MapDataModel.removeMapMarker(marker: MapMarker?) {
    marker?.let { removeMapObject(it) }
}

fun MapView.MapDataModel.addMapRoute(mapRoute: MapRoute) {
    addMapObject(mapRoute)
}

fun MapView.MapDataModel.setMapRoute(mapRoute: MapRoute) {
    removeAllMapRoutes()
    addMapRoute(mapRoute)
}

fun MapView.MapDataModel.removeMapRoute(mapRoute: MapRoute) {
    removeMapObject(mapRoute)
}

fun MapView.MapDataModel.removeAllMapMarkers() = removeAllMapObjects<MapMarker>()

fun MapView.MapDataModel.removeAllMapRoutes() = removeAllMapObjects<MapRoute>()

private inline fun <reified T : MapObject<*>> MapView.MapDataModel.removeAllMapObjects() =
    mapObjects.forEach { if (it is T) removeMapObject(it) }

fun Camera.CameraModel.setMapRectangle(geoBoundingBox: GeoBoundingBox, margin: Int) {
    mapRectangle = MapRectangle(geoBoundingBox, margin, margin, margin, margin)
}

fun SearchResult.toSearchResultItem(): SearchResultItem<out SearchResult>? {
    return when (this) {
        is MapSearchResult -> {
            when (dataType) {
                MapSearchResult.DataType.Country -> CountryResultItem(this)
                MapSearchResult.DataType.Postal -> PostalResultItem(this)
                MapSearchResult.DataType.City -> CityResultItem(this)
                MapSearchResult.DataType.Street -> StreetResultItem(this)
                MapSearchResult.DataType.AddressPoint -> AddressPointResultItem(this)
                MapSearchResult.DataType.PostalAddress -> PostalAddressResultItem(this)
                MapSearchResult.DataType.PoiCategoryGroup -> PoiCategoryGroupResultItem(this)
                MapSearchResult.DataType.PoiCategory -> PoiCategoryResultItem(this)
                MapSearchResult.DataType.Poi -> PoiResultItem(this)
                else -> null
            }
        }
        is CoordinateSearchResult -> CoordinateSearchResultItem(this)
        else -> null
    }
}

fun List<SearchResult>.toSearchResultList(): List<SearchResultItem<out SearchResult>> =
    mapNotNull { it.toSearchResultItem() }

fun List<SearchResultItem<out SearchResult>>.toSdkSearchResultList(): List<SearchResult> = mapNotNull { it.dataPayload }

fun List<SignpostInfo.SignElement>.concatItems(): String = StringBuilder().apply {
    this@concatItems.forEach {
        if (isNotEmpty()) append(", ")
        append(it.text)
    }
}.toString()

fun List<SignpostInfo>.getNaviSignInfoOnRoute(): SignpostInfo? {
    this.filter { it.isOnRoute }.let { isOnRouteList ->
        if (isOnRouteList.isEmpty()) {
            return null
        }

        return isOnRouteList.maxBy { it.priority }?.let { it } ?: isOnRouteList[0]
    }
}

fun SignpostInfo.roadSigns(maxRoadSignsCount: Int = 3): List<RoadSignData> {
    return signElements
        .asSequence()
        .filter { it.elementType == SignElementType.RouteNumber }
        .filter { it.roadNumberFormat.insideNumber.isNotEmpty() }
        .take(if (signElements.hasPictogram()) maxRoadSignsCount - 1 else maxRoadSignsCount)
        .toList()
        .toRoadSignDataList()
}

fun SignpostInfo.createInstructionText(): TextHolder {
    val acceptedSignElements = signElements
        .filter { element ->
            element.elementType.let {
                it == SignElementType.ExitNumber || it == SignElementType.PlaceName || it == SignElementType.OtherDestination
            }
        }
        // The sort is stable, so it will only move the ExitNumbers to the top and leave the others as they are.
        .sortedByDescending { it.elementType == SignElementType.ExitNumber }

    return if (acceptedSignElements.any { it.elementType == SignElementType.ExitNumber }) {
        TextHolder.from(R.string.exit_number, acceptedSignElements.concatItems())
    } else {
        TextHolder.from(acceptedSignElements.concatItems())
    }
}

fun List<SignpostInfo.SignElement>.hasPictogram(): Boolean = any { it.elementType == SignElementType.Pictogram }

fun List<SignpostInfo.SignElement>.toRoadSignDataList(): List<RoadSignData> {
    return map {
        with(it.roadNumberFormat) {
            RoadSignData(roadSignBackgroundDrawableRes(), insideNumber, roadSignForegroundColorRes())
        }
    }
}

fun DirectionInfo.getDistanceWithUnits(distanceUnit: DistanceUnit): String =
    Distance.getFormattedDistance(distanceUnit, distance)

@DrawableRes
fun DirectionInfo.getDirectionDrawable(directionManeuverType: DirectionManeuverType): Int {
    val routeManeuver = when (directionManeuverType) {
        DirectionManeuverType.PRIMARY -> primary
        DirectionManeuverType.SECONDARY -> secondary
    }

    return if (routeManeuver.isValid) routeManeuver.getDirectionDrawable() else 0
}

fun DirectionInfo.createInstructionText(): TextHolder {
    with(primary) {
        if (!isValid) {
            return TextHolder.empty
        }

        if (isRoundabout()) {
            return getDirectionInstruction().let {
                if (it != 0) TextHolder.from(it, roundaboutExit) else TextHolder.empty
            }
        }

        with(nextRoadText()) {
            if (isNotEmpty()) return TextHolder.from(this)
        }

        getDirectionInstruction().let {
            return if (it != 0) TextHolder.from(it) else TextHolder.empty
        }
    }
}