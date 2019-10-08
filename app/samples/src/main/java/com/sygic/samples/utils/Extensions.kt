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

package com.sygic.samples.utils

import com.sygic.maps.uikit.viewmodels.common.data.PoiData
import com.sygic.maps.uikit.viewmodels.common.extensions.getFormattedLocation
import com.sygic.maps.uikit.viewmodels.common.position.PositionManagerClientImpl
import com.sygic.maps.uikit.views.common.extensions.EMPTY_STRING
import com.sygic.maps.uikit.views.common.utils.logInfo
import com.sygic.maps.uikit.views.poidetail.component.PoiDetailComponent
import com.sygic.maps.uikit.views.poidetail.data.PoiDetailData
import com.sygic.sdk.InitializationCallback
import com.sygic.sdk.context.SygicContext
import com.sygic.sdk.position.GeoCoordinates
import com.sygic.sdk.position.GeoPosition
import com.sygic.sdk.position.PositionManager
import com.sygic.sdk.route.Route
import com.sygic.sdk.route.RoutePlan
import com.sygic.sdk.route.Router
import com.sygic.sdk.route.RouterProvider
import com.sygic.sdk.search.*

fun getLastValidLocation(lastValidLocationCallback: (GeoCoordinates) -> Unit) {
    with(PositionManagerClientImpl) {
        addPositionChangeListener(object : PositionManager.PositionChangeListener {
            override fun onPositionChanged(position: GeoPosition) {
                if (position.isValid) {
                    removePositionChangeListener(this)
                    lastValidLocationCallback.invoke(position.coordinates)
                }
            }
        })
        setSdkPositionUpdatingEnabled(true)
    }
}

fun RoutePlan.getPrimaryRoute(routeComputeCallback: (route: Route) -> Unit) {
    RouterProvider.getInstance(object : InitializationCallback<Router> {
        override fun onInstance(router: Router) {
            router.computeRoute(this@getPrimaryRoute, object : Router.RouteComputeAdapter() {
                override fun onPrimaryComputeFinished(router: Router, route: Route) = routeComputeCallback.invoke(route)
            })
        }

        override fun onError(@SygicContext.OnInitListener.Result result: Int) {}
    })
}

fun MapSearchResult.loadDetails(callback: Search.SearchDetailListener) {
    SearchProvider.getInstance(object : InitializationCallback<Search> {
        override fun onInstance(search: Search) {
            search.loadDetails(this@loadDetails, DetailRequest(), callback)
        }
        override fun onError(@SygicContext.OnInitListener.Result result: Int) {}
    })
}

fun List<SearchResult>.isCategoryResult(): Boolean {
    val firstSearchResult = first()
    return size == 1 && firstSearchResult is MapSearchResult
            && (firstSearchResult.dataType == MapSearchResult.DataType.PoiCategoryGroup
            || firstSearchResult.dataType == MapSearchResult.DataType.PoiCategory)
}

fun List<SearchResult>.toGeoCoordinatesList(): List<GeoCoordinates> {
    val geoCoordinatesList = mutableListOf<GeoCoordinates>()
    forEach { searchResult ->
        when (searchResult) {
            is CoordinateSearchResult -> geoCoordinatesList.add(searchResult.position)
            is MapSearchResult -> geoCoordinatesList.add(searchResult.position)
            else -> logInfo("${searchResult.javaClass.simpleName} class conversion is not implemented yet.")
        }
    }
    return geoCoordinatesList
}

fun SearchResult.toPoiDetailComponent(): PoiDetailComponent? {
    return when (this) {
        is CoordinateSearchResult -> {
            val formattedCoordinates = this.position.getFormattedLocation()
            PoiDetailComponent(
                PoiDetailData(
                    titleString = formattedCoordinates,
                    subtitleString = EMPTY_STRING,
                    coordinatesString = formattedCoordinates
                ),
                true
            )
        }
        is MapSearchResult -> {
            val poiData = PoiData(
                name = this.poiName.text,
                street = this.street.text,
                city = this.city.text
            )
            PoiDetailComponent(
                PoiDetailData(
                    titleString = poiData.title,
                    subtitleString = poiData.description,
                    coordinatesString = this.position.getFormattedLocation()
                ),
                true
            )
        }
        else -> {
            logInfo("${this.javaClass.simpleName} class conversion is not implemented yet.")
            null
        }
    }
}