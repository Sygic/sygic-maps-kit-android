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
import com.sygic.maps.uikit.viewmodels.common.initialization.SdkInitializationManager
import com.sygic.sdk.position.GeoCoordinates
import com.sygic.sdk.search.DetailRequest
import com.sygic.sdk.search.MapSearchResult
import com.sygic.sdk.search.Search
import com.sygic.sdk.search.SearchRequest

const val MAX_RESULTS_COUNT_DEFAULT_VALUE = 20

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
class SearchManagerImpl(private val initManager: SdkInitializationManager) : SearchManager {

    private val sdkSearchEngine = Search()

    override var maxResultsCount: Int = MAX_RESULTS_COUNT_DEFAULT_VALUE

    override fun searchText(text: String, position: GeoCoordinates?) =
        initManager.onReady { sdkSearchEngine.search(SearchRequest(text, position).apply { maxResults = maxResultsCount }) }

    override fun loadMapSearchResultDetails(result: MapSearchResult, listener: Search.SearchDetailListener) =
        initManager.onReady { sdkSearchEngine.loadDetails(result, DetailRequest(), listener) }

    override fun addSearchResultsListener(listener: Search.SearchResultsListener) =
        sdkSearchEngine.addSearchResultsListener(listener)

    override fun removeSearchResultsListener(listener: Search.SearchResultsListener) =
        sdkSearchEngine.removeSearchResultsListener(listener)
}