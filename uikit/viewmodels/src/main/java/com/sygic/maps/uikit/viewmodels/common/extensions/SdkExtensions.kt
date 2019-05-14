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

import com.sygic.maps.uikit.viewmodels.common.sdk.search.CoordinateSearchResultItem
import com.sygic.maps.uikit.viewmodels.common.sdk.search.SearchResultItem
import com.sygic.maps.uikit.viewmodels.common.sdk.search.map.*
import com.sygic.maps.uikit.views.common.extensions.EMPTY_STRING
import com.sygic.sdk.places.LocationInfo
import com.sygic.sdk.position.GeoCoordinates
import com.sygic.sdk.search.CoordinateSearchResult
import com.sygic.sdk.search.MapSearchResult
import com.sygic.sdk.search.SearchResult
import java.util.*

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