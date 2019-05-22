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
import com.sygic.maps.uikit.viewmodels.common.sdk.mapobject.MapMarker
import com.sygic.maps.uikit.views.common.extensions.asSingleEvent
import com.sygic.maps.uikit.views.common.livedata.SingleLiveEvent
import com.sygic.samples.search.components.BrowseMapFragmentInitComponent
import com.sygic.sdk.position.GeoCoordinates
import com.sygic.sdk.search.*

class SearchFromBrowseMapWitchPinsActivityViewModel : ViewModel(), DefaultLifecycleObserver {

    val placeBrowseMapFragmentObservable: LiveData<BrowseMapFragmentInitComponent> = SingleLiveEvent()
    val moduleConnectionObservable: LiveData<ModuleConnectionProvider> = SingleLiveEvent()
    val addMapMarkerObservable: LiveData<MapMarker> = SingleLiveEvent() //todo
    val removeAllMapMarkersObservable: LiveData<Any> = SingleLiveEvent() //todo

    private val searchConnectionProvider = SearchConnectionProvider { searchResultList ->
        removeAllMapMarkersObservable.asSingleEvent().call()

        searchResultList.forEach { searchResult ->
            when (searchResult) {
                is MapSearchResult -> { //todo
                    searchResult.loadDetails(Search.SearchDetailListener { mapSearchDetail, state ->
                        if (state == SearchResult.ResultState.Success) {
                            addMapMarkerObservable.asSingleEvent().value =
                                MapMarker(mapSearchDetail.position.latitude, mapSearchDetail.position.longitude) //todo
                        }
                    })
                }
                is CoordinateSearchResult -> {
                    addMapMarkerObservable.asSingleEvent().value =
                        MapMarker(searchResult.position.latitude, searchResult.position.longitude)
                }
                is CustomSearchResult -> {
                    searchResult.position?.let {
                        addMapMarkerObservable.asSingleEvent().value = MapMarker(it.latitude, it.longitude) //todo
                    }
                }
                else -> {
                    //Todo
                }
            }
        }
        //todo: camera movement
    }

    init {
        placeBrowseMapFragmentObservable.asSingleEvent().value =
            BrowseMapFragmentInitComponent(2F, GeoCoordinates(48.145764, 17.126015))
    }

    override fun onCreate(owner: LifecycleOwner) {
        moduleConnectionObservable.asSingleEvent().value = searchConnectionProvider
    }
}