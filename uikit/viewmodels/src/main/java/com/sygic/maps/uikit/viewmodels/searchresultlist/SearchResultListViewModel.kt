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
import androidx.lifecycle.*
import androidx.recyclerview.widget.RecyclerView
import com.sygic.maps.tools.annotations.AutoFactory
import com.sygic.maps.uikit.viewmodels.common.extensions.toSearchResults
import com.sygic.maps.uikit.viewmodels.common.search.SearchManagerClient
import com.sygic.maps.uikit.viewmodels.common.utils.searchResultStateToErrorViewSwitcherIndex
import com.sygic.maps.uikit.views.common.extensions.asMutable
import com.sygic.maps.uikit.views.common.extensions.asSingleEvent
import com.sygic.maps.uikit.views.common.extensions.hideKeyboard
import com.sygic.maps.uikit.views.common.livedata.SingleLiveEvent
import com.sygic.maps.uikit.views.searchresultlist.SearchResultList
import com.sygic.maps.uikit.views.searchresultlist.SearchResultListErrorViewSwitcherIndex
import com.sygic.maps.uikit.views.searchresultlist.adapter.DefaultStateAdapter
import com.sygic.maps.uikit.views.searchresultlist.adapter.ResultListAdapter
import com.sygic.maps.uikit.views.searchresultlist.adapter.SearchResultListAdapter
import com.sygic.maps.uikit.views.searchresultlist.data.SearchResultItem
import com.sygic.sdk.search.AutocompleteResult

/**
 * A [SearchResultListViewModel] is a basic ViewModel implementation for the [SearchResultList] class. It listens to
 * the Sygic SDK [SearchManagerClient.autocompleteResults] and updates the search result list in the [SearchResultList] view.
 */
@AutoFactory
@Suppress("unused", "MemberVisibilityCanBePrivate")
open class SearchResultListViewModel internal constructor(
    private val searchManagerClient: SearchManagerClient
) : ViewModel(), DefaultLifecycleObserver, ResultListAdapter.ClickListener<AutocompleteResult> {

    private var lastScrollState = RecyclerView.SCROLL_STATE_IDLE

    private val defaultStateAdapter: DefaultStateAdapter<AutocompleteResult> = DefaultStateAdapter()
    private val resultListAdapter: SearchResultListAdapter<AutocompleteResult> = SearchResultListAdapter()

    val onSearchResultItemClickObservable: LiveData<SearchResultItem<out AutocompleteResult>> = SingleLiveEvent()

    val errorViewSwitcherIndex = MutableLiveData(SearchResultListErrorViewSwitcherIndex.NO_RESULTS_FOUND)
    val activeAdapter: LiveData<ResultListAdapter<AutocompleteResult, ResultListAdapter.ItemViewHolder<AutocompleteResult>>> = MutableLiveData(defaultStateAdapter)

    init {
        resultListAdapter.clickListener = this
    }

    override fun onCreate(owner: LifecycleOwner) {
        searchManagerClient.autocompleteResults.observe(owner, Observer { autocompleteResults ->
            resultListAdapter.items = autocompleteResults.toSearchResults()
            activeAdapter.asMutable().value = if (inputIsNotEmpty()) resultListAdapter else defaultStateAdapter
        })
        searchManagerClient.autocompleteResultState.observe(owner, Observer {
            errorViewSwitcherIndex.asMutable().value = searchResultStateToErrorViewSwitcherIndex(it)
        })
    }

    private fun inputIsNotEmpty() = searchManagerClient.searchText.value!!.isNotEmpty()

    open fun onResultListScrollStateChanged(view: RecyclerView, scrollState: Int) {
        if (lastScrollState != scrollState && scrollState != RecyclerView.SCROLL_STATE_IDLE) {
            view.hideKeyboard()
        }

        view.requestFocus()
        lastScrollState = scrollState
    }

    override fun onSearchResultItemClick(view: View, searchResultItem: SearchResultItem<out AutocompleteResult>) {
        view.hideKeyboard()
        onSearchResultItemClickObservable.asSingleEvent().value = searchResultItem
    }
}