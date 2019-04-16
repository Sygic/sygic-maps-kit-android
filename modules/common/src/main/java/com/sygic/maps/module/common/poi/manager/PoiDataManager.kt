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

package com.sygic.maps.module.common.poi.manager

import androidx.annotation.RestrictTo
import com.sygic.maps.uikit.viewmodels.common.data.PoiData
import com.sygic.maps.uikit.viewmodels.common.extensions.getFirst
import com.sygic.sdk.map.`object`.ScreenObject
import com.sygic.sdk.map.`object`.ViewObject
import com.sygic.sdk.map.`object`.data.ViewObjectData
import com.sygic.sdk.places.LocationInfo
import com.sygic.sdk.places.Place
import com.sygic.sdk.places.Places
import com.sygic.sdk.position.GeoCoordinates
import com.sygic.sdk.search.ReverseGeocoder
import com.sygic.sdk.search.ReverseSearchResult

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
interface PoiDataManager {

    fun getViewObjectData(viewObject: ViewObject<*>, callback: Callback)

    abstract class Callback : Places.PlaceListener, ReverseGeocoder.ReverseSearchResultsListener {
        abstract fun onDataLoaded(data: ViewObjectData)

        final override fun onPlaceLoaded(place: Place) {
            val placeObject = ScreenObject.at(place.coordinates)
                .withPayload(
                    PoiData(
                        name = place.name,
                        iso = place.iso,
                        poiCategory = place.category,
                        poiGroup = place.group,
                        city = place.locationInfo.getFirst(LocationInfo.LocationType.City),
                        street = place.locationInfo.getFirst(LocationInfo.LocationType.Street),
                        houseNumber = place.locationInfo.getFirst(LocationInfo.LocationType.HouseNum),
                        postal = place.locationInfo.getFirst(LocationInfo.LocationType.Postal),
                        phone = place.locationInfo.getFirst(LocationInfo.LocationType.Phone),
                        email = place.locationInfo.getFirst(LocationInfo.LocationType.Mail),
                        url = place.locationInfo.getFirst(LocationInfo.LocationType.Url)
                    )
                ).build()

            onDataLoaded(placeObject.data)
        }

        final override fun onSearchResults(results: List<ReverseSearchResult>, position: GeoCoordinates) {
            val builder =
                if (results.isEmpty()) {
                    ScreenObject.at(position)
                } else {
                    val reverseSearchResult = results.first()
                    ScreenObject.at(position).withPayload(
                        PoiData(
                            iso = reverseSearchResult.names.countryIso,
                            city = reverseSearchResult.names.city,
                            street = reverseSearchResult.names.street,
                            houseNumber = reverseSearchResult.names.houseNumber
                        )
                    )
                }

            onDataLoaded(builder.build().data)
        }
    }
}