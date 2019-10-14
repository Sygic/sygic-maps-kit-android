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
import com.sygic.maps.uikit.viewmodels.common.initialization.InitializationManager
import com.sygic.maps.uikit.viewmodels.common.initialization.InitializationState
import com.sygic.maps.uikit.viewmodels.common.search.holder.SearchResultsHolder
import com.sygic.sdk.InitializationCallback
import com.sygic.sdk.context.SygicContext
import com.sygic.sdk.position.GeoCoordinates
import com.sygic.sdk.search.Search
import com.sygic.sdk.search.SearchProvider
import com.sygic.sdk.search.SearchRequest

const val MAX_RESULTS_COUNT_DEFAULT_VALUE = 20

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
object SearchManagerImpl : SearchManager {

    @InitializationState
    override var initializationState = InitializationState.INITIALIZATION_NOT_STARTED
    private val callbacks = LinkedHashSet<InitializationManager.Callback>()

    override var maxResultsCount: Int = MAX_RESULTS_COUNT_DEFAULT_VALUE

    private lateinit var search: Search

    override fun initialize(callback: InitializationManager.Callback?) {
        synchronized(this) {
            if (initializationState == InitializationState.INITIALIZED) {
                callback?.onInitialized()
                return
            }

            callback?.let { callbacks.add(it) }

            if (initializationState == InitializationState.INITIALIZING) {
                return
            }

            initializationState = InitializationState.INITIALIZING
        }

        SearchProvider.getInstance(object : InitializationCallback<Search> {
            override fun onInstance(search: Search) {
                synchronized(this) {
                    this@SearchManagerImpl.search = search
                    initializationState = InitializationState.INITIALIZED
                }
                with(callbacks) {
                    forEach { it.onInitialized() }
                    clear()
                }
            }
            override fun onError(@SygicContext.OnInitListener.Result result: Int) {
                synchronized(this) { initializationState = InitializationState.ERROR }
                with(callbacks) {
                    forEach { it.onError(result) }
                    clear()
                }
            }
        })
    }

    override val searchResults by lazy {
        object : LiveData<SearchResultsHolder>() {

            private val searchResultsListener by lazy {
                Search.SearchResultsListener { input, state, results ->
                    value = SearchResultsHolder(input, state, results)
                }
            }

            override fun onActive() = onReady { search.addSearchResultsListener(searchResultsListener) }
            override fun onInactive() = onReady { search.removeSearchResultsListener(searchResultsListener) }
        }
    }

    override fun searchText(text: String, position: GeoCoordinates?) =
        onReady { search.search(SearchRequest(text, position).apply { maxResults = maxResultsCount }) }
}