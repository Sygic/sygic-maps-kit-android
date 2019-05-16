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

package com.sygic.maps.uikit.viewmodels.searchresultlist

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sygic.maps.tools.annotations.AutoFactory
import com.sygic.maps.uikit.viewmodels.common.extensions.toSearchResultList
import com.sygic.maps.uikit.viewmodels.common.search.SearchManager
import com.sygic.maps.uikit.views.common.extensions.asSingleEvent
import com.sygic.maps.uikit.views.common.livedata.SingleLiveEvent
import com.sygic.maps.uikit.views.searchresultlist.SearchResultList
import com.sygic.maps.uikit.views.searchresultlist.adapter.DefaultStateAdapter
import com.sygic.maps.uikit.views.searchresultlist.adapter.ResultListAdapter
import com.sygic.maps.uikit.views.searchresultlist.adapter.SearchResultListAdapter
import com.sygic.maps.uikit.views.searchresultlist.data.SearchResultItem
import com.sygic.sdk.search.Search

/**
 * A [SearchResultListViewModel] is a basic ViewModel implementation for the [SearchResultList] class. It listens to
 * the Sygic SDK [Search.SearchResultsListener] and updates the search result list in the [SearchResultList] view.
 */
@AutoFactory
@Suppress("unused", "MemberVisibilityCanBePrivate")
open class SearchResultListViewModel internal constructor(
    private val searchManager: SearchManager
) : ViewModel(), DefaultLifecycleObserver, ResultListAdapter.ClickListener {

    private val defaultStateAdapter = DefaultStateAdapter()
    private val resultListAdapter = SearchResultListAdapter()

    val onSearchResultItemClickObservable: LiveData<SearchResultItem<*>> = SingleLiveEvent()
    val searchResultListDataChangedObservable: LiveData<List<SearchResultItem<*>>> = SingleLiveEvent()

    val activeAdapter: MutableLiveData<ResultListAdapter<ResultListAdapter.ItemViewHolder>> = MutableLiveData()

    private val searchResultsListener = Search.SearchResultsListener { input, _, results ->
        results.toSearchResultList().let {
            resultListAdapter.items = it
            searchResultListDataChangedObservable.asSingleEvent().value = it
        }

        activeAdapter.value = if (input.isNotEmpty()) resultListAdapter else defaultStateAdapter
    }

    init {
        activeAdapter.value = defaultStateAdapter
        searchManager.addSearchResultsListener(searchResultsListener)
    }

    override fun onSearchResultItemClick(searchResultItem: SearchResultItem<*>) {
        onSearchResultItemClickObservable.asSingleEvent().value = searchResultItem
    }

    override fun onCleared() {
        super.onCleared()

        searchManager.removeSearchResultsListener(searchResultsListener)
    }
}