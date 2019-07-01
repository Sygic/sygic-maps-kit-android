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
import android.widget.TextView
import androidx.annotation.RestrictTo
import androidx.lifecycle.*
import com.sygic.maps.module.search.callback.SearchResultCallback
import com.sygic.maps.module.search.callback.SearchResultCallbackWrapper
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
    app: Application
) : AndroidViewModel(app), DefaultLifecycleObserver {

    val onFinishObservable: LiveData<Any> = SingleLiveEvent()

    private var searchResultCallback: SearchResultCallback? = null
    private var currentSearchResults: List<SearchResultItem<out SearchResult>> = listOf()

    override fun onCreate(owner: LifecycleOwner) {
        if (owner is SearchResultCallbackWrapper) {
            owner.searchResultCallbackProvider.observe(owner, Observer { callback ->
                searchResultCallback = callback
            })
        }
    }

    fun searchResultListDataChanged(searchResultListItems: List<SearchResultItem<out SearchResult>>) {
        currentSearchResults = searchResultListItems
    }

    fun onSearchResultItemClick(searchResultItem: SearchResultItem<out SearchResult>) =
        invokeCallbackAndFinish(listOf(searchResultItem))

    fun onActionSearchClick(view: TextView) {
        view.hideKeyboard()
        invokeCallbackAndFinish(currentSearchResults)
    }

    private fun invokeCallbackAndFinish(searchResultList: List<SearchResultItem<out SearchResult>>) {
        searchResultCallback?.onSearchResult(searchResultList.toSdkSearchResultList())
        onFinishObservable.asSingleEvent().call()
    }

    override fun onCleared() {
        super.onCleared()

        searchResultCallback = null
    }
}
