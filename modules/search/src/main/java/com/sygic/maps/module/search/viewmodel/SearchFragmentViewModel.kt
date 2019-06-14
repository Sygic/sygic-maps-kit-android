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

package com.sygic.maps.module.search.viewmodel

import android.app.Application
import androidx.annotation.RestrictTo
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.sygic.maps.module.search.callback.SearchResultCallback
import com.sygic.maps.module.search.component.SearchFragmentInitComponent
import com.sygic.maps.tools.annotations.Assisted
import com.sygic.maps.tools.annotations.AutoFactory
import com.sygic.maps.uikit.viewmodels.common.extensions.toSdkSearchResultList
import com.sygic.maps.uikit.views.common.extensions.asSingleEvent
import com.sygic.maps.uikit.views.common.extensions.hideKeyboard
import com.sygic.maps.uikit.views.common.livedata.SingleLiveEvent
import com.sygic.maps.uikit.views.searchresultlist.data.SearchResultItem
import com.sygic.sdk.search.SearchResult

@AutoFactory
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
class SearchFragmentViewModel internal constructor(
    @Assisted initComponent: SearchFragmentInitComponent,
    app: Application
) : AndroidViewModel(app), DefaultLifecycleObserver {

    var searchResultCallback: SearchResultCallback? = null

    val hideKeyboardObservable: LiveData<Any> = SingleLiveEvent()
    val popBackStackObservable: LiveData<Any> = SingleLiveEvent()

    private var currentSearchResults: List<SearchResultItem<out SearchResult>> = listOf()

    private var lastScrollState = RecyclerView.SCROLL_STATE_IDLE

    init {
        with(initComponent) {
            this@SearchFragmentViewModel.searchResultCallback = searchResultCallback
            recycle()
        }
    }

    fun searchResultListDataChanged(searchResultListItems: List<SearchResultItem<out SearchResult>>) {
        currentSearchResults = searchResultListItems
    }

    fun onSearchResultItemClick(searchResultItem: SearchResultItem<out SearchResult>) =
        invokeCallbackAndFinish(listOf(searchResultItem))

    fun onActionSearchClick() = invokeCallbackAndFinish(currentSearchResults)

    fun onResultListScrollStateChanged(view: RecyclerView, scrollState: Int) {
        if (lastScrollState != scrollState && scrollState != RecyclerView.SCROLL_STATE_IDLE) {
            view.context.hideKeyboard(view)
        }

        view.requestFocus()
        lastScrollState = scrollState
    }

    private fun invokeCallbackAndFinish(searchResultList: List<SearchResultItem<out SearchResult>>) {
        searchResultCallback?.onSearchResult(searchResultList.toSdkSearchResultList())
        hideKeyboardObservable.asSingleEvent().call()
        popBackStackObservable.asSingleEvent().call()
    }

    override fun onCleared() {
        super.onCleared()

        searchResultCallback = null
        hideKeyboardObservable.asSingleEvent().call()
    }
}
