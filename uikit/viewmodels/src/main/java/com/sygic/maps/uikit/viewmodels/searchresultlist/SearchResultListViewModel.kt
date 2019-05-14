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

import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.sygic.maps.tools.annotations.AutoFactory
import com.sygic.maps.uikit.viewmodels.common.extensions.toSearchResultItem
import com.sygic.maps.uikit.viewmodels.common.sdk.search.SearchResultItem
import com.sygic.maps.uikit.viewmodels.common.search.SearchManager
import com.sygic.maps.uikit.viewmodels.searchresultlist.adapter.vh.ItemViewHolder
import com.sygic.maps.uikit.viewmodels.searchresultlist.adapter.DefaultStateAdapter
import com.sygic.maps.uikit.viewmodels.searchresultlist.adapter.SearchResultListAdapter
import com.sygic.maps.uikit.views.searchresultlist.SearchResultList
import com.sygic.sdk.search.Search

/**
 * A [SearchResultListViewModel] is a basic ViewModel implementation for the [SearchResultList] class. It listens to
 * the Sygic SDK [Search.SearchResultsListener] and updates the search result list in the [SearchResultList] view.
 */
@AutoFactory
@Suppress("unused", "MemberVisibilityCanBePrivate")
open class SearchResultListViewModel internal constructor(
    private val searchManager: SearchManager
) : ViewModel(), DefaultLifecycleObserver, SearchResultListAdapter.ClickListener {

    private val defaultStateAdapter = DefaultStateAdapter()
    private val resultListAdapter = SearchResultListAdapter()

    val activeAdapter: MutableLiveData<RecyclerView.Adapter<ItemViewHolder>> = MutableLiveData()

    private val searchResultsListener = Search.SearchResultsListener { input, state, results ->
        resultListAdapter.items = results.mapNotNull { it.toSearchResultItem() }
        activeAdapter.value = if (input.isNotEmpty()) resultListAdapter else defaultStateAdapter
    }

    init {
        resultListAdapter.clickListener = this
        searchManager.addSearchResultsListener(searchResultsListener)

        activeAdapter.value = defaultStateAdapter
    }

    override fun onSearchResultItemClick(searchResultItem: SearchResultItem<*>) {
        Log.d("Tomas", "onSearchResultItemClick() called with: searchResultItem = [$searchResultItem]") //todo
    }

    override fun onCleared() {
        super.onCleared()

        searchManager.removeSearchResultsListener(searchResultsListener)
    }
}