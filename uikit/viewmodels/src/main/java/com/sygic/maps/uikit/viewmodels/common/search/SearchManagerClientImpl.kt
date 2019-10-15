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
import androidx.lifecycle.Transformations
import com.sygic.maps.uikit.viewmodels.common.initialization.InitializationCallback
import com.sygic.maps.uikit.viewmodels.common.search.holder.SearchResultsHolder
import com.sygic.maps.uikit.views.common.extensions.observeOnce
import com.sygic.sdk.position.GeoCoordinates
import com.sygic.sdk.search.Search
import com.sygic.sdk.search.SearchProvider
import com.sygic.sdk.search.SearchRequest

const val MAX_RESULTS_COUNT_DEFAULT_VALUE = 20

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
object SearchManagerClientImpl : SearchManagerClient {

    override var maxResultsCount: Int = MAX_RESULTS_COUNT_DEFAULT_VALUE

    private val managerProvider: LiveData<Search> = object : MutableLiveData<Search>() {
        init { SearchProvider.getInstance(InitializationCallback<Search> { value = it }) }
    }

    override val searchResults by lazy {
        Transformations.switchMap<Search, SearchResultsHolder>(managerProvider) { manager ->
            object : LiveData<SearchResultsHolder>() {

                private val searchResultsListener by lazy {
                    Search.SearchResultsListener { input, state, results ->
                        value = SearchResultsHolder(input, state, results)
                    }
                }

                override fun onActive() = manager.addSearchResultsListener(searchResultsListener)
                override fun onInactive() = manager.removeSearchResultsListener(searchResultsListener)
            }
        }
    }

    override fun searchText(text: String, position: GeoCoordinates?) =
        managerProvider.observeOnce { it.search(SearchRequest(text, position).apply { maxResults = maxResultsCount }) }
}