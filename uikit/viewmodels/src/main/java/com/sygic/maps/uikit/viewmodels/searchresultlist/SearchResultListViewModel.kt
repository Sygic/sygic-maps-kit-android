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

import android.view.View
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.sygic.maps.tools.annotations.AutoFactory
import com.sygic.maps.uikit.viewmodels.common.extensions.toSearchResultList
import com.sygic.maps.uikit.viewmodels.common.search.SearchManager
import com.sygic.maps.uikit.views.common.extensions.asSingleEvent
import com.sygic.maps.uikit.views.common.extensions.hideKeyboard
import com.sygic.maps.uikit.views.common.livedata.SingleLiveEvent
import com.sygic.maps.uikit.views.searchresultlist.SearchResultList
import com.sygic.maps.uikit.views.searchresultlist.adapter.DefaultStateAdapter
import com.sygic.maps.uikit.views.searchresultlist.adapter.ResultListAdapter
import com.sygic.maps.uikit.views.searchresultlist.adapter.SearchResultListAdapter
import com.sygic.maps.uikit.views.searchresultlist.data.SearchResultItem
import com.sygic.sdk.search.Search
import com.sygic.sdk.search.SearchResult

/**
 * A [SearchResultListViewModel] is a basic ViewModel implementation for the [SearchResultList] class. It listens to
 * the Sygic SDK [Search.SearchResultsListener] and updates the search result list in the [SearchResultList] view.
 */
@AutoFactory
@Suppress("unused", "MemberVisibilityCanBePrivate")
open class SearchResultListViewModel internal constructor(
    private val searchManager: SearchManager
) : ViewModel(), DefaultLifecycleObserver, ResultListAdapter.ClickListener<SearchResult> {

    val onSearchResultItemClickObservable: LiveData<SearchResultItem<out SearchResult>> = SingleLiveEvent()
    val searchResultListDataChangedObservable: LiveData<List<SearchResultItem<out SearchResult>>> = SingleLiveEvent()

    val activeAdapter: MutableLiveData<ResultListAdapter<SearchResult, ResultListAdapter.ItemViewHolder<SearchResult>>> = MutableLiveData()

    private val defaultStateAdapter = DefaultStateAdapter<SearchResult>()
    private val resultListAdapter = SearchResultListAdapter(this)

    private var lastScrollState = RecyclerView.SCROLL_STATE_IDLE

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

    open fun onResultListScrollStateChanged(view: RecyclerView, scrollState: Int) {
        if (lastScrollState != scrollState && scrollState != RecyclerView.SCROLL_STATE_IDLE) {
            view.hideKeyboard()
        }

        view.requestFocus()
        lastScrollState = scrollState
    }

    override fun onSearchResultItemClick(view: View, searchResultItem: SearchResultItem<out SearchResult>) {
        view.hideKeyboard()
        onSearchResultItemClickObservable.asSingleEvent().value = searchResultItem
    }

    override fun onCleared() {
        super.onCleared()

        searchManager.removeSearchResultsListener(searchResultsListener)
    }
}