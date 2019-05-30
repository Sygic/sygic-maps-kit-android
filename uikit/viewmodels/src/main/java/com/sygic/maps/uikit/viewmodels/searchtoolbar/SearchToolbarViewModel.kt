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

package com.sygic.maps.uikit.viewmodels.searchtoolbar

import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sygic.maps.tools.annotations.Assisted
import com.sygic.maps.tools.annotations.AutoFactory
import com.sygic.maps.uikit.viewmodels.common.search.SearchManager
import com.sygic.maps.uikit.viewmodels.searchtoolbar.component.SearchToolbarInitComponent
import com.sygic.maps.uikit.views.common.extensions.EMPTY_STRING
import com.sygic.maps.uikit.views.common.extensions.asSingleEvent
import com.sygic.maps.uikit.views.common.livedata.SingleLiveEvent
import com.sygic.maps.uikit.views.searchtoolbar.SearchToolbar
import com.sygic.maps.uikit.views.searchtoolbar.SearchToolbarIconStateSwitcherIndex
import com.sygic.sdk.position.GeoCoordinates
import com.sygic.sdk.search.Search
import kotlinx.coroutines.*

private const val DEFAULT_SEARCH_DELAY = 300L

/**
 * A [SearchToolbarViewModel] is a basic ViewModel implementation for the [SearchToolbar] class. It listens to the
 * [SearchToolbar] input [EditText] changes and use the [SearchManager] to process search query request to the Sygic SDK
 * [Search] after the specified [searchDelay]. It also listens to the Sygic SDK [Search.SearchResultsListener] and set
 * appropriate state to the [SearchToolbar] state view.
 */
@AutoFactory
@Suppress("unused", "MemberVisibilityCanBePrivate")
open class SearchToolbarViewModel internal constructor(
    @Assisted initComponent: SearchToolbarInitComponent,
    private val searchManager: SearchManager
) : ViewModel(), DefaultLifecycleObserver {

    val iconStateSwitcherIndex: MutableLiveData<Int> = MutableLiveData()
    val inputText: MutableLiveData<CharSequence> = object: MutableLiveData<CharSequence>() {
        override fun setValue(value: CharSequence) {
            if (value != this.value) {
                super.setValue(value)
                search(value.toString())
            }
        }
    }

    var searchLocation: GeoCoordinates? = null
    var searchDelay: Long = DEFAULT_SEARCH_DELAY
    var maxResultsCount: Int
        get() = searchManager.maxResultsCount
        set(value) {
            searchManager.maxResultsCount = value
        }

    private val searchResultsListener = Search.SearchResultsListener { _, _, _ ->
        iconStateSwitcherIndex.value = SearchToolbarIconStateSwitcherIndex.MAGNIFIER
    }

    private var searchCoroutineJob: Job? = null
    private var lastSearchedString: String = EMPTY_STRING

    val onActionSearchClickObservable: LiveData<Any> = SingleLiveEvent()
    val searchToolbarFocused: MutableLiveData<Boolean> = MutableLiveData()

    init {
        with(initComponent) {
            this@SearchToolbarViewModel.inputText.value = initialSearchInput
            this@SearchToolbarViewModel.searchLocation = initialSearchLocation
            this@SearchToolbarViewModel.maxResultsCount = maxResultsCount
            recycle()
        }

        searchToolbarFocused.value = true
        iconStateSwitcherIndex.value = SearchToolbarIconStateSwitcherIndex.MAGNIFIER

        searchManager.addSearchResultsListener(searchResultsListener)
    }

    private fun search(input: String) {
        lastSearchedString = input
        searchCoroutineJob?.cancel()
        searchCoroutineJob = GlobalScope.launch(Dispatchers.Main) {
            delay(searchDelay)
            searchTextInput(input)
        }
    }

    private fun searchTextInput(input: String) {
        iconStateSwitcherIndex.value = SearchToolbarIconStateSwitcherIndex.PROGRESSBAR
        searchManager.searchText(input, searchLocation)
    }

    private fun cancelSearch() {
        searchCoroutineJob?.cancel()
        iconStateSwitcherIndex.value = SearchToolbarIconStateSwitcherIndex.MAGNIFIER
    }

    fun retrySearch() {
        search(lastSearchedString)
    }

    fun onClearButtonClick() {
        cancelSearch()
        inputText.value = EMPTY_STRING
    }

    fun onEditorActionEvent(actionId: Int): Boolean {
        return when (actionId) {
            EditorInfo.IME_ACTION_SEARCH -> {
                onActionSearchClickObservable.asSingleEvent().call()
                true
            }
            else -> false
        }
    }

    override fun onCleared() {
        super.onCleared()

        cancelSearch()
        searchToolbarFocused.value = false
        searchManager.removeSearchResultsListener(searchResultsListener)
    }
}