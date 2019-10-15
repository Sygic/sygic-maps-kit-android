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

package com.sygic.maps.uikit.viewmodels.common.place

import androidx.annotation.RestrictTo
import com.sygic.maps.uikit.viewmodels.common.data.PlaceData
import com.sygic.maps.uikit.viewmodels.common.extensions.getFirst
import com.sygic.sdk.map.`object`.ProxyObjectManager
import com.sygic.sdk.map.`object`.ProxyPlace
import com.sygic.sdk.map.`object`.ScreenObject
import com.sygic.sdk.map.`object`.ViewObject
import com.sygic.sdk.map.`object`.data.ViewObjectData
import com.sygic.sdk.places.Place
import com.sygic.sdk.places.PlaceDetailAttributes
import com.sygic.sdk.places.PlaceLink
import com.sygic.sdk.places.PlacesManager
import com.sygic.sdk.position.GeoCoordinates
import com.sygic.sdk.search.ReverseGeocoder
import com.sygic.sdk.search.ReverseSearchResult

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
interface PlacesManagerClient {

    fun loadPlace(link: PlaceLink, listener: PlacesManager.PlaceListener)
    fun loadPlaceLink(proxyPlace: ProxyPlace, listener: ProxyObjectManager.PlaceLinkListener)
    fun getViewObjectData(viewObject: ViewObject<*>, callback: Callback)

    abstract class Callback : PlacesManager.PlaceListener, ReverseGeocoder.ReverseSearchResultsListener {
        abstract fun onDataLoaded(data: ViewObjectData)

        final override fun onPlaceLoaded(place: Place) {
            val placeObject = ScreenObject.at(place.link.location)
                .withPayload(
                    PlaceData(
                        name = place.link.name,
                        placeCategory = place.link.category,
                        placeGroup = place.link.group,
                        iso = place.details.getFirst(PlaceDetailAttributes.Iso),
                        city = place.details.getFirst(PlaceDetailAttributes.City),
                        street = place.details.getFirst(PlaceDetailAttributes.Street),
                        houseNumber = place.details.getFirst(PlaceDetailAttributes.HouseNum),
                        postal = place.details.getFirst(PlaceDetailAttributes.Postal),
                        phone = place.details.getFirst(PlaceDetailAttributes.Phone),
                        email = place.details.getFirst(PlaceDetailAttributes.Mail),
                        url = place.details.getFirst(PlaceDetailAttributes.Url)
                    )
                ).build()

            onDataLoaded(placeObject.data)
        }

        final override fun onSearchResults(results: List<ReverseSearchResult>, position: GeoCoordinates) {
            val builder = if (results.isEmpty()) {
                ScreenObject.at(position)
            } else {
                val reverseSearchResult = results.first()
                ScreenObject.at(position).withPayload(
                    PlaceData(
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