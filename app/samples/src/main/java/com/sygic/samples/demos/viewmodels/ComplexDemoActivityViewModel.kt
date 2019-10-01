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

package com.sygic.samples.demos.viewmodels

import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.sygic.maps.module.common.listener.OnMapClickListener
import com.sygic.maps.module.common.provider.ModuleConnectionProvider
import com.sygic.maps.module.navigation.NAVIGATION_FRAGMENT_TAG
import com.sygic.maps.module.navigation.NavigationFragment
import com.sygic.maps.module.search.SEARCH_FRAGMENT_TAG
import com.sygic.maps.module.search.SearchFragment
import com.sygic.maps.uikit.viewmodels.common.data.PoiData
import com.sygic.maps.uikit.viewmodels.common.extensions.addMapMarker
import com.sygic.maps.uikit.viewmodels.common.extensions.getFormattedLocation
import com.sygic.maps.uikit.viewmodels.common.extensions.loadDetails
import com.sygic.maps.uikit.viewmodels.common.extensions.removeAllMapMarkers
import com.sygic.maps.uikit.views.common.extensions.EMPTY_STRING
import com.sygic.maps.uikit.views.common.extensions.asSingleEvent
import com.sygic.maps.uikit.views.common.livedata.SingleLiveEvent
import com.sygic.maps.uikit.views.common.utils.logInfo
import com.sygic.maps.uikit.views.poidetail.component.PoiDetailComponent
import com.sygic.maps.uikit.views.poidetail.data.PoiDetailData
import com.sygic.sdk.map.Camera
import com.sygic.sdk.map.MapRectangle
import com.sygic.sdk.map.`object`.MapMarker
import com.sygic.sdk.map.`object`.data.ViewObjectData
import com.sygic.sdk.map.data.SimpleCameraDataModel
import com.sygic.sdk.map.data.SimpleMapDataModel
import com.sygic.sdk.position.GeoBoundingBox
import com.sygic.sdk.position.GeoCoordinates
import com.sygic.sdk.search.CoordinateSearchResult
import com.sygic.sdk.search.MapSearchResult
import com.sygic.sdk.search.Search
import com.sygic.sdk.search.SearchResult
import com.sygic.sdk.search.detail.DetailPoiCategory
import com.sygic.sdk.search.detail.DetailPoiCategoryGroup

private const val MARGIN = 80

class ComplexDemoActivityViewModel : ViewModel() {

    var lastDestination: GeoCoordinates? = null

    val showPoiDetailObservable: LiveData<PoiDetailComponent> = SingleLiveEvent()
    val computePrimaryRouteObservable: LiveData<GeoCoordinates> = SingleLiveEvent()

    val onMapClickListener = object : OnMapClickListener {
        override fun onMapDataReceived(data: ViewObjectData) {
            lastDestination = data.position
        }
    }

    val searchModuleConnectionProvider = object : ModuleConnectionProvider {
        override val fragment: Fragment
            get() {
                val searchFragment = SearchFragment()
                searchFragment.searchLocation = cameraDataModel?.position
                searchFragment.setResultCallback(callback)
                return searchFragment
            }

        override fun getFragmentTag() = SEARCH_FRAGMENT_TAG
    }

    val navigationModuleConnectionProvider = object : ModuleConnectionProvider {
        override val fragment: Fragment
            get() {
                mapDataModel?.removeAllMapMarkers()
                computePrimaryRouteObservable.asSingleEvent().value = lastDestination
                //todo: show and hide progress
                return NavigationFragment()
            }

        override fun getFragmentTag() = NAVIGATION_FRAGMENT_TAG
    }

    var mapDataModel: SimpleMapDataModel? = null
    var cameraDataModel: SimpleCameraDataModel? = null

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
                            is DetailPoiCategory -> mapSearchDetail.poiList.forEach { addMapMarker(it.position) }
                            is DetailPoiCategoryGroup -> mapSearchDetail.poiList.forEach { addMapMarker(it.position) }
                        }

                        setCameraRectangle(mapSearchDetail.boundingBox)
                    }
                })
            } else {
                searchResultList.toGeoCoordinatesList().let { geoCoordinatesList ->
                    if (geoCoordinatesList.isNotEmpty()) {

                        if (geoCoordinatesList.size == 1) {
                            with(geoCoordinatesList.first()) {
                                addMapMarker(this)
                                lastDestination = this
                                cameraDataModel?.position = this
                                cameraDataModel?.zoomLevel = 10F
                                searchResultList.first().toPoiDetailComponent()?.let {
                                    showPoiDetailObservable.asSingleEvent().value = it
                                }
                            }
                        } else {
                            val geoBoundingBox = GeoBoundingBox(geoCoordinatesList.first(), geoCoordinatesList.first())
                            geoCoordinatesList.forEach { geoCoordinates ->
                                addMapMarker(geoCoordinates)
                                geoBoundingBox.union(geoCoordinates)
                            }
                            setCameraRectangle(geoBoundingBox)
                        }
                    }
                }
            }
        }
    }

    private fun addMapMarker(position: GeoCoordinates) {
        mapDataModel?.addMapMarker(MapMarker.at(position).build())
    }

    private fun setCameraRectangle(geoBoundingBox: GeoBoundingBox) {
        cameraDataModel?.mapRectangle = MapRectangle(geoBoundingBox, MARGIN, MARGIN, MARGIN, MARGIN)
    }
}

private fun List<SearchResult>.toGeoCoordinatesList(): List<GeoCoordinates> {
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

private fun List<SearchResult>.isCategoryResult(): Boolean {
    val firstSearchResult = first()
    return size == 1 && firstSearchResult is MapSearchResult
            && (firstSearchResult.dataType == MapSearchResult.DataType.PoiCategoryGroup
            || firstSearchResult.dataType == MapSearchResult.DataType.PoiCategory)
}

private fun SearchResult.toPoiDetailComponent(): PoiDetailComponent? {
    return when (this) {
        is CoordinateSearchResult -> PoiDetailComponent(PoiDetailData(this.position.getFormattedLocation(), EMPTY_STRING), true)
        is MapSearchResult -> {
            val poiData = PoiData(
                name = this.poiName.text,
                street = this.street.text,
                city = this.city.text)

            PoiDetailComponent(PoiDetailData(poiData.title, poiData.description), true)
        }
        else -> {
            logInfo("${this.javaClass.simpleName} class conversion is not implemented yet.")
            null
        }
    }
}