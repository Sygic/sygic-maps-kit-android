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

package com.sygic.maps.uikit.viewmodels.common.search

import androidx.annotation.RestrictTo
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sygic.maps.uikit.viewmodels.common.initialization.InitializationCallback
import com.sygic.maps.uikit.viewmodels.common.search.state.ResultState
import com.sygic.maps.uikit.viewmodels.common.search.state.toResultState
import com.sygic.maps.uikit.views.common.extensions.EMPTY_STRING
import com.sygic.maps.uikit.views.common.extensions.observeOnce
import com.sygic.sdk.position.GeoCoordinates
import com.sygic.sdk.search.*

const val MAX_RESULTS_COUNT_DEFAULT_VALUE = 10

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
object SearchManagerClientImpl : SearchManagerClient {

    private val managerProvider: LiveData<SearchManager> = object : MutableLiveData<SearchManager>() {
        init { SearchManagerProvider.getInstance(InitializationCallback<SearchManager> { value = it }) }
    }

    private val autocompleteResultListener = object : SearchManager.AutocompleteResultListener {
        override fun onAutocomplete(autocompleteResult: List<AutocompleteResult>) {
            autocompleteResultState.value = ResultState.SUCCESS
            autocompleteResults.value = autocompleteResult
        }
        override fun onAutcompleteError(code: SearchManager.ErrorCode) {
            autocompleteResultState.value = code.toResultState()
            autocompleteResults.value = emptyList()
        }
    }

    override val searchText by lazy { MutableLiveData<String>(EMPTY_STRING) }

    override val searchLocation by lazy { MutableLiveData<GeoCoordinates?>() }

    override val maxResultsCount by lazy { MutableLiveData<Int>(MAX_RESULTS_COUNT_DEFAULT_VALUE) }

    override val autocompleteResults by lazy { MutableLiveData<List<AutocompleteResult>>(emptyList()) }

    override val autocompleteResultState by lazy { MutableLiveData<ResultState>(ResultState.UNKNOWN) }

    override fun geocodeResult(result: AutocompleteResult, callback: (result: GeocodingResult) -> Unit) {
        managerProvider.observeOnce {
            it.geocode(GeocodeLocationRequest(locationId = result.locationId), object : SearchManager.GeocodingResultListener {
                override fun onGeocodingResult(geocodingResult: GeocodingResult) { callback.invoke(geocodingResult) }
                override fun onGeocodingResultError(code: SearchManager.ErrorCode) { /* Currently do nothing */ }
            })
        }
    }

    override fun geocodeAllResults(callback: (results: List<GeocodingResult>) -> Unit) {
        managerProvider.observeOnce {
            it.geocode(SearchRequest(
                searchInput = searchText.value!!,
                maxResultCount = maxResultsCount.value!!,
                location = searchLocation.value ?: GeoCoordinates.Invalid
            ), object : SearchManager.GeocodingResultsListener {
                override fun onGeocodingResults(geocodingResults: List<GeocodingResult>) { callback.invoke(geocodingResults) }
                override fun onGeocodingResultsError(code: SearchManager.ErrorCode) { /* Currently do nothing */ }
            })
        }
    }

    init {
        searchText.observeForever { text ->
            managerProvider.observeOnce {
                it.autocomplete(SearchRequest(
                    searchInput = text,
                    maxResultCount = maxResultsCount.value!!,
                    location = searchLocation.value ?: GeoCoordinates.Invalid
                ), autocompleteResultListener)
            }
        }
    }
}