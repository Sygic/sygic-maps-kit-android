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
import com.sygic.maps.module.navigation.listener.EventListener
import com.sygic.maps.module.search.SEARCH_FRAGMENT_TAG
import com.sygic.maps.module.search.SearchFragment
import com.sygic.maps.uikit.viewmodels.common.extensions.*
import com.sygic.maps.uikit.views.common.extensions.asSingleEvent
import com.sygic.maps.uikit.views.common.livedata.SingleLiveEvent
import com.sygic.maps.uikit.views.poidetail.PoiDetailBottomDialogFragment
import com.sygic.maps.uikit.views.poidetail.component.PoiDetailComponent
import com.sygic.samples.utils.isCategoryResult
import com.sygic.samples.utils.loadDetails
import com.sygic.samples.utils.toGeoCoordinatesList
import com.sygic.samples.utils.toPoiDetailComponent
import com.sygic.sdk.map.Camera
import com.sygic.sdk.map.`object`.data.ViewObjectData
import com.sygic.sdk.map.data.SimpleCameraDataModel
import com.sygic.sdk.map.data.SimpleMapDataModel
import com.sygic.sdk.position.GeoBoundingBox
import com.sygic.sdk.position.GeoCoordinates
import com.sygic.sdk.route.Route
import com.sygic.sdk.search.MapSearchResult
import com.sygic.sdk.search.Search
import com.sygic.sdk.search.SearchResult
import com.sygic.sdk.search.detail.DetailPoiCategory
import com.sygic.sdk.search.detail.DetailPoiCategoryGroup

private const val CAMERA_RECTANGLE_MARGIN = 80

class ComplexDemoActivityViewModel : ViewModel() {

    var targetPosition: GeoCoordinates? = null
    var mapDataModel: SimpleMapDataModel? = null
    var cameraDataModel: SimpleCameraDataModel? = null

    val restoreDefaultStateObservable: LiveData<Any> = SingleLiveEvent()
    val computePrimaryRouteObservable: LiveData<GeoCoordinates> = SingleLiveEvent()
    val routeComputeProgressVisibilityObservable: LiveData<Int> = SingleLiveEvent()
    val placeNavigationFragmentObservable: LiveData<EventListener> = SingleLiveEvent()
    val showPoiDetailObservable: LiveData<Pair<PoiDetailComponent, PoiDetailBottomDialogFragment.Listener>> = SingleLiveEvent()
    val hidePoiDetailObservable: LiveData<Any> = SingleLiveEvent()

    val onMapClickListener = object : OnMapClickListener {
        override fun showDetailsView() = false
        override fun onMapDataReceived(data: ViewObjectData) {
            targetPosition = data.position
            showPoiDetailObservable.asSingleEvent().value = Pair(
                PoiDetailComponent(data.toPoiDetailData(), true),
                poiDetailListener
            )
        }
    }

    val poiDetailListener = object : PoiDetailBottomDialogFragment.Listener {
        override fun onNavigationButtonClick() {
            hidePoiDetailObservable.asSingleEvent().call()
            computePrimaryRouteObservable.asSingleEvent().value = targetPosition
            routeComputeProgressVisibilityObservable.asSingleEvent().value = View.VISIBLE
            placeNavigationFragmentObservable.asSingleEvent().value = navigationEventListener
        }

        override fun onDismiss() {
            mapDataModel?.removeAllMapMarkers()
        }
    }

    val navigationEventListener = object : EventListener {
        override fun onNavigationStarted(route: Route?) {
            routeComputeProgressVisibilityObservable.asSingleEvent().value = View.GONE
        }

        override fun onNavigationDestroyed() {
            restoreDefaultStateObservable.asSingleEvent().call()
            routeComputeProgressVisibilityObservable.asSingleEvent().value = View.GONE
        }
    }

    val searchModuleConnectionProvider = object : ModuleConnectionProvider {
        override val fragment: Fragment
            get() {
                return SearchFragment().apply {
                    searchLocation = cameraDataModel?.position
                    setResultCallback(callback)
                }
            }

        override fun getFragmentTag() = SEARCH_FRAGMENT_TAG
    }

    private val callback: ((searchResultList: List<SearchResult>) -> Unit) = { searchResultList ->
        mapDataModel?.removeAllMapMarkers()

        if (searchResultList.isNotEmpty()) {
            cameraDataModel?.apply {
                movementMode = Camera.MovementMode.Free
                rotationMode = Camera.RotationMode.Free
            }

            if (searchResultList.isCategoryResult()) {
                (searchResultList.first() as MapSearchResult).loadDetails(Search.SearchDetailListener { mapSearchDetail, state ->
                    if (state == SearchResult.ResultState.Success) {
                        when (mapSearchDetail) {
                            is DetailPoiCategory -> mapSearchDetail.poiList.forEach { mapDataModel?.addMapMarker(it.position) }
                            is DetailPoiCategoryGroup -> mapSearchDetail.poiList.forEach { mapDataModel?.addMapMarker(it.position) }
                        }
                        cameraDataModel?.setMapRectangle(mapSearchDetail.boundingBox, CAMERA_RECTANGLE_MARGIN)
                    }
                })
            } else {
                searchResultList.toGeoCoordinatesList().let { geoCoordinatesList ->
                    if (geoCoordinatesList.isNotEmpty()) {

                        if (geoCoordinatesList.size == 1) {
                            with(geoCoordinatesList.first()) {
                                mapDataModel?.addMapMarker(this)
                                targetPosition = this
                                cameraDataModel?.position = this
                                cameraDataModel?.zoomLevel = 10F
                                searchResultList.first().toPoiDetailComponent()?.let {
                                    showPoiDetailObservable.asSingleEvent().value = Pair(it, poiDetailListener)
                                }
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
    }
}