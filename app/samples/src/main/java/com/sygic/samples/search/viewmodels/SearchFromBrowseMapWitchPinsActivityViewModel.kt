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

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.sygic.maps.module.common.provider.ModuleConnectionProvider
import com.sygic.maps.module.search.provider.SearchConnectionProvider
import com.sygic.maps.uikit.viewmodels.common.extensions.loadDetails
import com.sygic.maps.uikit.views.common.extensions.asSingleEvent
import com.sygic.maps.uikit.views.common.livedata.SingleLiveEvent
import com.sygic.maps.uikit.views.common.utils.logInfo
import com.sygic.samples.search.components.BrowseMapFragmentInitComponent
import com.sygic.sdk.map.`object`.MapMarker
import com.sygic.sdk.position.GeoBoundingBox
import com.sygic.sdk.position.GeoCoordinates
import com.sygic.sdk.search.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class SearchFromBrowseMapWitchPinsActivityViewModel : ViewModel(), DefaultLifecycleObserver {

    val placeBrowseMapFragmentObservable: LiveData<BrowseMapFragmentInitComponent> = SingleLiveEvent()
    val moduleConnectionObservable: LiveData<ModuleConnectionProvider> = SingleLiveEvent()
    val addMapMarkerObservable: LiveData<MapMarker> = SingleLiveEvent() //todo
    val removeAllMapMarkersObservable: LiveData<Any> = SingleLiveEvent() //todo
    val setCameraPositionObservable: LiveData<GeoCoordinates> = SingleLiveEvent()

    private var loadDetailsCoroutineJob: Job? = null

    private val searchConnectionProvider = SearchConnectionProvider { searchResultList ->
        loadDetailsCoroutineJob?.cancel()
        removeAllMapMarkersObservable.asSingleEvent().call()

        if (searchResultList.isEmpty()) {
            return@SearchConnectionProvider
        }

        //todo: groups
        searchResultList.toGeoCoordinatesList { geoCoordinatesList ->

            if (geoCoordinatesList.isNotEmpty()) {

                if (geoCoordinatesList.size == 1) {
                    addMapMarkerObservable.asSingleEvent().value = MapMarker.at(geoCoordinatesList.first()).build()
                    setCameraPositionObservable.asSingleEvent().value = geoCoordinatesList.first() //todo: zoom level
                } else {
                    val geoBoundingBox: GeoBoundingBox = GeoBoundingBox(geoCoordinatesList.first(), geoCoordinatesList.first())

                    geoCoordinatesList.forEach { geoCoordinates ->
                        addMapMarkerObservable.asSingleEvent().value = MapMarker.at(geoCoordinates).build()
                        geoBoundingBox.union(geoCoordinates)
                    }

                    //todo: set MapRectangle here from geoBoundingBox and margins
                }
            }
        }
    }

    init {
        placeBrowseMapFragmentObservable.asSingleEvent().value =
            BrowseMapFragmentInitComponent(2F, GeoCoordinates(48.145764, 17.126015))
    }

    override fun onCreate(owner: LifecycleOwner) {
        moduleConnectionObservable.asSingleEvent().value = searchConnectionProvider
    }

    private fun List<SearchResult>.toGeoCoordinatesList(callback: (geoCoordinatesList: List<GeoCoordinates>) -> Unit) {
        val geoCoordinatesList = mutableListOf<GeoCoordinates>()
        forEach { searchResult ->
            when (searchResult) {
                is CoordinateSearchResult -> geoCoordinatesList.add(searchResult.position)
                is MapSearchResult -> {
                    loadDetailsCoroutineJob = GlobalScope.launch(Dispatchers.Main) {
                        searchResult.loadDetails(Search.SearchDetailListener { mapSearchDetail, state ->
                            if (state == SearchResult.ResultState.Success) geoCoordinatesList.add((mapSearchDetail.position))
                        })
                    }
                }
                else -> logInfo("${searchResult.javaClass.simpleName} class conversion is not implemented yet.")
            }
        }
        callback.invoke(geoCoordinatesList)
    }

    override fun onCleared() {
        super.onCleared()

        loadDetailsCoroutineJob?.cancel()
    }
}