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

import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import com.sygic.maps.uikit.viewmodels.common.extensions.getFormattedLocation
import com.sygic.maps.uikit.viewmodels.common.navigation.preview.RouteDemonstrationManagerClientImpl
import com.sygic.maps.uikit.viewmodels.common.position.PositionManagerClientImpl
import com.sygic.maps.uikit.views.placedetail.component.PlaceDetailComponent
import com.sygic.maps.uikit.views.placedetail.data.PlaceDetailData
import com.sygic.sdk.InitializationCallback
import com.sygic.sdk.context.SygicContext
import com.sygic.sdk.position.GeoCoordinates
import com.sygic.sdk.position.GeoPosition
import com.sygic.sdk.route.Route
import com.sygic.sdk.route.RoutePlan
import com.sygic.sdk.route.Router
import com.sygic.sdk.route.RouterProvider
import com.sygic.sdk.search.GeocodingResult
import com.sygic.sdk.search.ResultType

fun getLastValidLocation(lastValidLocationCallback: (GeoCoordinates) -> Unit) {
    with(PositionManagerClientImpl.getInstance(RouteDemonstrationManagerClientImpl)) {
        currentPosition.observeForever(object: Observer<GeoPosition> {
            override fun onChanged(position: GeoPosition) {
                if (position.isValid()) {
                    currentPosition.removeObserver(this)
                    lastValidLocationCallback.invoke(position.coordinates)
                }
            }
        })
        sdkPositionUpdatingEnabled.value = true
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

fun List<GeocodingResult>.isCategoryResult(): Boolean {
    return size == 1 && first().type == ResultType.PLACE_CATEGORY
}

fun List<GeocodingResult>.toGeoCoordinatesList(): List<GeoCoordinates> = map { it.location }

fun GeocodingResult.toPlaceDetailComponent(navigationButtonEnabled: Boolean = false): PlaceDetailComponent {
    return PlaceDetailComponent(
        PlaceDetailData(
            titleString = title,
            subtitleString = subtitle,
            coordinatesString = this.location.getFormattedLocation()
        ),
        navigationButtonEnabled
    )
}

fun FragmentManager.hasFragmentWithTag(tag: String) = findFragmentByTag(tag) != null