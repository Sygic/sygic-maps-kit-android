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
import com.sygic.samples.search.components.BrowseMapFragmentInitComponent
import com.sygic.sdk.position.GeoCoordinates
import com.sygic.sdk.search.MapSearchResult
import com.sygic.sdk.search.Search
import com.sygic.sdk.search.SearchResult
import com.sygic.sdk.search.detail.*

class SearchFromBrowseMapActivityViewModel : ViewModel(), DefaultLifecycleObserver {

    val placeBrowseMapFragmentObservable: LiveData<BrowseMapFragmentInitComponent> = SingleLiveEvent()
    val moduleConnectionObservable: LiveData<ModuleConnectionProvider> = SingleLiveEvent()
    val showToastObservable: LiveData<String> = SingleLiveEvent()

    init {
        placeBrowseMapFragmentObservable.asSingleEvent().value =
            BrowseMapFragmentInitComponent(11F, GeoCoordinates(48.145764, 17.126015))
    }

    override fun onCreate(owner: LifecycleOwner) {
        val searchConnectionProvider = SearchConnectionProvider { searchResultList ->
            searchResultList.forEach { searchResult ->
                when (searchResult) {
                    is MapSearchResult -> {
                        searchResult.loadDetails(Search.SearchDetailListener { mapSearchDetail, state ->
                            if (state == SearchResult.ResultState.Success) {
                                when (mapSearchDetail) {
                                    is DetailStreet -> showToast("[DetailStreet] $mapSearchDetail")
                                    is DetailCountry -> showToast("[DetailCountry] $mapSearchDetail")
                                    is DetailAddressPoint -> showToast("[DetailAddressPoint] $mapSearchDetail")
                                    is DetailPostalAddress -> showToast("[DetailPostalAddress] $mapSearchDetail")
                                    is DetailPostal -> showToast("[DetailPostal] CityNames: ${mapSearchDetail.cityNames}")
                                    is DetailCity -> showToast("[DetailCity] Is capital: ${mapSearchDetail.isCapital}, Population: ${mapSearchDetail.population}")
                                    is DetailPoiCategoryGroup -> showToast("[DetailPoiCategoryGroup] PoiList: ${mapSearchDetail.poiList}")
                                    is DetailPoiCategory -> showToast("[DetailPoiCategory] PoiList: ${mapSearchDetail.poiList}")
                                    is DetailPoi -> showToast("[DetailPoi] Name: ${mapSearchDetail.name}, Category: ${mapSearchDetail.categoryId}, Group: ${mapSearchDetail.groupId}")
                                    else -> showToast(mapSearchDetail.toString())
                                }
                            } else {
                                showToast("Something went wrong :( (error: $state)")
                            }
                        })
                    }
                    else -> showToast("[SearchResult] $searchResult")
                }
            }
        }
        moduleConnectionObservable.asSingleEvent().value = searchConnectionProvider
    }

    private fun showToast(string: String) {
        showToastObservable.asSingleEvent().value = string
    }
}