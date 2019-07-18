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

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sygic.maps.tools.annotations.Assisted
import com.sygic.maps.tools.annotations.AutoFactory
import com.sygic.maps.uikit.viewmodels.common.search.MAX_RESULTS_COUNT_DEFAULT_VALUE
import com.sygic.maps.uikit.viewmodels.common.search.SearchManager
import com.sygic.maps.uikit.viewmodels.searchtoolbar.component.KEY_SEARCH_INPUT
import com.sygic.maps.uikit.viewmodels.searchtoolbar.component.KEY_SEARCH_LOCATION
import com.sygic.maps.uikit.viewmodels.searchtoolbar.component.KEY_SEARCH_MAX_RESULTS_COUNT
import com.sygic.maps.uikit.views.common.extensions.*
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
    @Assisted arguments: Bundle?,
    private val searchManager: SearchManager
) : ViewModel(), DefaultLifecycleObserver {

    val searchToolbarFocused: MutableLiveData<Boolean> = MutableLiveData(true)
    val onActionSearchClickObservable: LiveData<TextView> = SingleLiveEvent()

    val iconStateSwitcherIndex: MutableLiveData<Int> = MutableLiveData(SearchToolbarIconStateSwitcherIndex.MAGNIFIER)
    val inputText: MutableLiveData<CharSequence> = object : MutableLiveData<CharSequence>() {
        override fun setValue(value: CharSequence) {
            if (value != this.value) super.setValue(value)
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

    private val scope = CoroutineScope(Job() + Dispatchers.Main)
    private var searchCoroutineJob: Job? = null
    private var lastSearchedString: CharSequence = EMPTY_STRING

    init {
        with(arguments) {
            inputText.value = getString(KEY_SEARCH_INPUT, EMPTY_STRING)
            searchLocation = getParcelableValue(KEY_SEARCH_LOCATION)
            maxResultsCount = getInt(KEY_SEARCH_MAX_RESULTS_COUNT, MAX_RESULTS_COUNT_DEFAULT_VALUE)
        }

        inputText.observeForever(::search)
        searchManager.addSearchResultsListener(searchResultsListener)
    }

    private fun search(input: CharSequence) {
        lastSearchedString = input
        searchCoroutineJob?.cancel()
        searchCoroutineJob = scope.launch {
            iconStateSwitcherIndex.value = SearchToolbarIconStateSwitcherIndex.PROGRESSBAR
            if (input.isNotEmpty()) delay(searchDelay)
            searchManager.searchText(input.toString(), searchLocation)
        }
    }

    open fun retrySearch() {
        search(lastSearchedString)
    }

    open fun onClearButtonClick() {
        inputText.value = EMPTY_STRING
    }

    open fun onEditorActionEvent(view: TextView, actionId: Int): Boolean {
        return when (actionId) {
            EditorInfo.IME_ACTION_SEARCH -> {
                onActionSearchClickObservable.asSingleEvent().value = view
                true
            }
            else -> false
        }
    }

    open fun onFocusChanged(view: View, hasFocus: Boolean) {
        searchToolbarFocused.value = hasFocus
        if (hasFocus) view.showKeyboard()
    }

    override fun onCleared() {
        super.onCleared()

        searchCoroutineJob?.cancel()
        scope.cancel()
        searchToolbarFocused.value = false
        searchManager.removeSearchResultsListener(searchResultsListener)
    }
}