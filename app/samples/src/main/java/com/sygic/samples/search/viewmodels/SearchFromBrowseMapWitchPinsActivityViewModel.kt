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

package com.sygic.samples.search.viewmodels

import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.sygic.maps.module.common.provider.ModuleConnectionProvider
import com.sygic.maps.module.search.SearchFragment
import com.sygic.maps.uikit.viewmodels.common.extensions.loadDetails
import com.sygic.maps.uikit.views.common.extensions.asSingleEvent
import com.sygic.maps.uikit.views.common.livedata.SingleLiveEvent
import com.sygic.maps.uikit.views.common.utils.logInfo
import com.sygic.sdk.map.MapRectangle
import com.sygic.sdk.map.`object`.MapMarker
import com.sygic.sdk.position.GeoBoundingBox
import com.sygic.sdk.position.GeoCoordinates
import com.sygic.sdk.search.*
import com.sygic.sdk.search.detail.DetailPoiCategory
import com.sygic.sdk.search.detail.DetailPoiCategoryGroup

private const val MARGIN = 80

class SearchFromBrowseMapWitchPinsActivityViewModel : ViewModel(), ModuleConnectionProvider {

    val addMapMarkerObservable: LiveData<MapMarker> = SingleLiveEvent()
    val removeAllMapMarkersObservable: LiveData<Any> = SingleLiveEvent()
    val setCameraPositionObservable: LiveData<GeoCoordinates> = SingleLiveEvent()
    val setCameraRectangleObservable: LiveData<MapRectangle> = SingleLiveEvent()
    val setCameraZoomLevelObservable: LiveData<Float> = SingleLiveEvent()

    private val callback: ((searchResultList: List<SearchResult>) -> Unit) = { searchResultList ->
        removeAllMapMarkersObservable.asSingleEvent().call()

        if (searchResultList.isNotEmpty()) {
            if (searchResultList.isOnlyCategory()) {
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
                            addMapMarker(geoCoordinatesList.first())
                            setCameraPositionObservable.asSingleEvent().value = geoCoordinatesList.first()
                            setCameraZoomLevelObservable.asSingleEvent().value = 10F
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

    override val fragment: Fragment
        get() {
            val searchFragment = SearchFragment()
            searchFragment.setResultCallback(callback)
            return searchFragment
        }

    private fun addMapMarker(position: GeoCoordinates) {
        addMapMarkerObservable.asSingleEvent().value = MapMarker.at(position).build()
    }

    private fun setCameraRectangle(geoBoundingBox: GeoBoundingBox) {
        setCameraRectangleObservable.asSingleEvent().value =
            MapRectangle(geoBoundingBox, MARGIN, MARGIN, MARGIN, MARGIN)
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

private fun List<SearchResult>.isOnlyCategory(): Boolean {
    val firstSearchResult = first()
    return size == 1 && firstSearchResult is MapSearchResult
            && (firstSearchResult.dataType == MapSearchResult.DataType.PoiCategoryGroup
            || firstSearchResult.dataType == MapSearchResult.DataType.PoiCategory)
}