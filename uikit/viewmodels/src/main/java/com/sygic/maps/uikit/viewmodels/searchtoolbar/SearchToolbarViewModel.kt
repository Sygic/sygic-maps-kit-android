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
import androidx.lifecycle.*
import com.sygic.maps.tools.annotations.Assisted
import com.sygic.maps.tools.annotations.AutoFactory
import com.sygic.maps.uikit.viewmodels.common.search.MAX_RESULTS_COUNT_DEFAULT_VALUE
import com.sygic.maps.uikit.viewmodels.common.search.SearchManagerClient
import com.sygic.maps.uikit.viewmodels.searchtoolbar.component.KEY_SEARCH_INPUT
import com.sygic.maps.uikit.viewmodels.searchtoolbar.component.KEY_SEARCH_LOCATION
import com.sygic.maps.uikit.viewmodels.searchtoolbar.component.KEY_SEARCH_MAX_RESULTS_COUNT
import com.sygic.maps.uikit.views.common.extensions.*
import com.sygic.maps.uikit.views.common.livedata.SingleLiveEvent
import com.sygic.maps.uikit.views.common.utils.UniqueMutableLiveData
import com.sygic.maps.uikit.views.searchtoolbar.SearchToolbar
import com.sygic.maps.uikit.views.searchtoolbar.SearchToolbarIconStateSwitcherIndex
import com.sygic.sdk.position.GeoCoordinates
import kotlinx.coroutines.*

private const val DEFAULT_SEARCH_DELAY = 300L

/**
 * A [SearchToolbarViewModel] is a basic ViewModel implementation for the [SearchToolbar] class. It listens to the
 * [SearchToolbar] input [EditText] changes and use the [SearchManagerClient] to process search query request to the Sygic SDK
 * [SearchManagerClient] after the specified [searchDelay]. It also listens to the Sygic SDK [SearchManagerClient.autocompleteResults] and set
 * appropriate state to the [SearchToolbar] state view.
 */
@AutoFactory
@Suppress("unused", "MemberVisibilityCanBePrivate")
open class SearchToolbarViewModel internal constructor(
    @Assisted arguments: Bundle?,
    private val searchManagerClient: SearchManagerClient
) : ViewModel(), DefaultLifecycleObserver {

    val searchToolbarFocused = MutableLiveData<Boolean>(true)
    val onActionSearchClickObservable: LiveData<TextView> = SingleLiveEvent()

    val iconStateSwitcherIndex = MutableLiveData<Int>(SearchToolbarIconStateSwitcherIndex.MAGNIFIER)
    val inputText: MutableLiveData<CharSequence> = UniqueMutableLiveData()

    var searchDelay: Long = DEFAULT_SEARCH_DELAY
    var searchLocation: GeoCoordinates?
        get() = searchManagerClient.searchLocation.value
        set(value) {
            searchManagerClient.searchLocation.value = value
        }
    var maxResultsCount: Int
        get() = searchManagerClient.maxResultsCount.value!!
        set(value) {
            searchManagerClient.maxResultsCount.value = value
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

    }

    override fun onCreate(owner: LifecycleOwner) {
        searchManagerClient.autocompleteResultState.observe(owner, Observer {
            iconStateSwitcherIndex.value = SearchToolbarIconStateSwitcherIndex.MAGNIFIER
        })
    }

    private fun search(input: CharSequence) {
        lastSearchedString = input
        searchCoroutineJob?.cancel()
        searchCoroutineJob = scope.launch {
            iconStateSwitcherIndex.value = SearchToolbarIconStateSwitcherIndex.PROGRESSBAR
            if (input.isNotEmpty()) delay(searchDelay)
            searchManagerClient.searchText.value = input.toString()
        }
    }

    open fun retrySearch() = search(lastSearchedString)

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
    }
}