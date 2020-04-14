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

package com.sygic.samples.demo.viewmodels

import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.sygic.maps.module.common.listener.OnMapClickListener
import com.sygic.maps.module.common.provider.ModuleConnectionProvider
import com.sygic.maps.module.search.SEARCH_FRAGMENT_TAG
import com.sygic.maps.module.search.SearchFragment
import com.sygic.maps.uikit.viewmodels.common.extensions.addMapMarker
import com.sygic.maps.uikit.viewmodels.common.extensions.removeAllMapMarkers
import com.sygic.maps.uikit.viewmodels.common.extensions.setMapRectangle
import com.sygic.maps.uikit.viewmodels.common.extensions.toPlaceDetailData
import com.sygic.maps.uikit.views.common.extensions.asSingleEvent
import com.sygic.maps.uikit.views.common.livedata.SingleLiveEvent
import com.sygic.maps.uikit.views.placedetail.PlaceDetailBottomDialogFragment
import com.sygic.maps.uikit.views.placedetail.component.PlaceDetailComponent
import com.sygic.samples.utils.toGeoCoordinatesList
import com.sygic.samples.utils.toPlaceDetailComponent
import com.sygic.sdk.map.Camera
import com.sygic.sdk.map.`object`.data.ViewObjectData
import com.sygic.sdk.map.data.SimpleCameraDataModel
import com.sygic.sdk.map.data.SimpleMapDataModel
import com.sygic.sdk.position.GeoBoundingBox
import com.sygic.sdk.position.GeoCoordinates
import com.sygic.sdk.route.RoutingOptions
import com.sygic.sdk.search.GeocodingResult

private const val CAMERA_RECTANGLE_MARGIN = 80

class ComplexDemoActivityViewModel : ViewModel() {

    var targetPosition: GeoCoordinates? = null
    var mapDataModel: SimpleMapDataModel? = null
    var cameraDataModel: SimpleCameraDataModel? = null
    var routingOptions: RoutingOptions? = null
    var routingOptionsDisplayed = false

    val restoreDefaultStateObservable: LiveData<Any> = SingleLiveEvent()
    val showRouteOptionsObservable: LiveData<Any> = SingleLiveEvent()
    val computePrimaryRouteObservable: LiveData<GeoCoordinates> = SingleLiveEvent()
    val routeComputeProgressVisibilityObservable: LiveData<Int> = SingleLiveEvent()
    val showPlaceDetailObservable: LiveData<Pair<PlaceDetailComponent, PlaceDetailBottomDialogFragment.Listener>> = SingleLiveEvent()
    val hidePlaceDetailObservable: LiveData<Any> = SingleLiveEvent()

    private var lastPlaceDetailDisplayed: Pair<PlaceDetailComponent, PlaceDetailBottomDialogFragment.Listener>? = null

    val onMapClickListener = object : OnMapClickListener {
        override fun showDetailsView() = false
        override fun onMapDataReceived(data: ViewObjectData) {
            targetPosition = data.position
            lastPlaceDetailDisplayed = Pair(
                PlaceDetailComponent(data.toPlaceDetailData(), true),
                placeDetailListener
            )
            showPlaceDetailObservable.asSingleEvent().value = lastPlaceDetailDisplayed
        }
    }

    val placeDetailListener = object : PlaceDetailBottomDialogFragment.Listener {
        override fun onRouteOptionsButtonClick() {
            showRouteOptionsObservable.asSingleEvent().call()
        }

        override fun onNavigationButtonClick() {
            hidePlaceDetailObservable.asSingleEvent().call()
            computePrimaryRouteObservable.asSingleEvent().value = targetPosition
            routeComputeProgressVisibilityObservable.asSingleEvent().value = View.VISIBLE
        }

        override fun onDismiss() {
            mapDataModel?.removeAllMapMarkers()
        }
    }

    val searchModuleConnectionProvider = object : ModuleConnectionProvider {
        override val fragment: Fragment
            get() {
                return SearchFragment().apply {
                    searchLocation = cameraDataModel?.position ?: GeoCoordinates.Invalid
                    setResultCallback(callback)
                }
            }

        override fun getFragmentTag() = SEARCH_FRAGMENT_TAG
    }

    private val callback: ((results: List<GeocodingResult>) -> Unit) = { results ->
        mapDataModel?.removeAllMapMarkers()

        if (results.isNotEmpty()) {
            cameraDataModel?.apply {
                movementMode = Camera.MovementMode.Free
                rotationMode = Camera.RotationMode.Free
            }

            results.toGeoCoordinatesList().let { geoCoordinatesList ->
                if (geoCoordinatesList.isNotEmpty()) {

                    if (geoCoordinatesList.size == 1) {
                        with(geoCoordinatesList.first()) {
                            mapDataModel?.addMapMarker(this)
                            targetPosition = this
                            cameraDataModel?.position = this
                            cameraDataModel?.zoomLevel = 10F
                            lastPlaceDetailDisplayed = Pair(
                                results.first().toPlaceDetailComponent(true), placeDetailListener
                            )
                            showPlaceDetailObservable.asSingleEvent().value = lastPlaceDetailDisplayed
                        }
                    } else {
                        val geoBoundingBox = GeoBoundingBox(geoCoordinatesList.first(), geoCoordinatesList.first())
                        geoCoordinatesList.forEach { geoCoordinates ->
                            mapDataModel?.addMapMarker(geoCoordinates)
                            geoBoundingBox.union(geoCoordinates)
                        }
                        cameraDataModel?.setMapRectangle(geoBoundingBox, CAMERA_RECTANGLE_MARGIN)
                    }
                }
            }
        }
    }

    fun showLastPlaceDetail() {
        mapDataModel?.addMapMarker(targetPosition!!)
        showPlaceDetailObservable.asSingleEvent().value = lastPlaceDetailDisplayed
    }
}