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
import androidx.lifecycle.ViewModel
import com.sygic.maps.module.common.provider.ModuleConnectionProvider
import com.sygic.maps.module.search.SearchFragment
import com.sygic.maps.uikit.viewmodels.common.extensions.addMapMarker
import com.sygic.maps.uikit.viewmodels.common.extensions.loadDetails
import com.sygic.maps.uikit.viewmodels.common.extensions.removeAllMapMarkers
import com.sygic.maps.uikit.viewmodels.common.extensions.setMapRectangle
import com.sygic.samples.utils.isCategoryResult
import com.sygic.samples.utils.toGeoCoordinatesList
import com.sygic.sdk.map.data.SimpleCameraDataModel
import com.sygic.sdk.map.data.SimpleMapDataModel
import com.sygic.sdk.position.GeoBoundingBox
import com.sygic.sdk.search.MapSearchResult
import com.sygic.sdk.search.Search
import com.sygic.sdk.search.SearchResult
import com.sygic.sdk.search.detail.DetailPoiCategory
import com.sygic.sdk.search.detail.DetailPoiCategoryGroup

private const val CAMERA_RECTANGLE_MARGIN = 80

class SearchFromBrowseMapWitchPinsActivityViewModel : ViewModel(), ModuleConnectionProvider {

    var mapDataModel: SimpleMapDataModel? = null
    var cameraDataModel: SimpleCameraDataModel? = null

    private val callback: ((searchResultList: List<SearchResult>) -> Unit) = { searchResultList ->
        mapDataModel?.removeAllMapMarkers()

        if (searchResultList.isNotEmpty()) {
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
                            mapDataModel?.addMapMarker(geoCoordinatesList.first())
                            cameraDataModel?.position = geoCoordinatesList.first()
                            cameraDataModel?.zoomLevel = 10F
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

    override val fragment: Fragment
        get() {
            val searchFragment = SearchFragment()
            searchFragment.searchLocation = cameraDataModel?.position
            searchFragment.setResultCallback(callback)
            return searchFragment
        }
}