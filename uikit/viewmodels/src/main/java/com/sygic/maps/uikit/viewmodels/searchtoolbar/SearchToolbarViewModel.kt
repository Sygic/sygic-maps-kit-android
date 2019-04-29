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

import android.util.Log
import android.view.inputmethod.EditorInfo
import androidx.lifecycle.*
import com.sygic.maps.tools.annotations.AutoFactory
import com.sygic.maps.uikit.viewmodels.common.search.SearchManager
import com.sygic.maps.uikit.viewmodels.common.utils.TextWatcherAdapter
import com.sygic.maps.uikit.views.common.extensions.EMPTY_STRING
import com.sygic.maps.uikit.views.common.extensions.asSingleEvent
import com.sygic.maps.uikit.views.common.livedata.SingleLiveEvent
import com.sygic.maps.uikit.views.positionlockfab.PositionLockFab
import com.sygic.maps.uikit.views.searchtoolbar.SearchToolbar
import com.sygic.maps.uikit.views.searchtoolbar.SearchToolbarIconStateSwitcherIndex
import com.sygic.sdk.map.Camera
import com.sygic.sdk.search.Search

/**
 * A [SearchToolbarViewModel] is a basic ViewModel implementation for the [SearchToolbar] class. TODO:... It listens to the Sygic SDK
 * [Camera.ModeChangedListener] and set appropriate state to the [PositionLockFab] view. It also sets the [LockState.UNLOCKED]
 * as default.
 */
@AutoFactory
@Suppress("unused", "MemberVisibilityCanBePrivate")
open class SearchToolbarViewModel internal constructor(
    private val searchManager: SearchManager
) : ViewModel(), DefaultLifecycleObserver {

    @SearchToolbarIconStateSwitcherIndex
    val iconStateSwitcherIndex: MutableLiveData<Int> = MutableLiveData()
    val inputText: MutableLiveData<String> = MutableLiveData()

    val keyboardVisibilityObservable: LiveData<Boolean> = SingleLiveEvent()

    val onTextChangedListener = TextWatcherAdapter { input -> if (input.isNotEmpty()) searchManager.searchText(input/*, GeoCoordinates(48.157648, 17.128288)*/) } // todo: single live event

    // TODO: For testing purposes only
    val searchResultsListener = Search.SearchResultsListener { searchedString, state, results ->
        Log.d("Tomas","onSearchResults() called with: searchedString = [$searchedString], state = [$state]")
        results.forEach { Log.d("Tomas", it.toString()) }
        //searchManager.removeSearchResultsListener(this)
    }

    init {
        iconStateSwitcherIndex.value = SearchToolbarIconStateSwitcherIndex.MAGNIFIER
        inputText.value = "" //todo: use the value from initialSearchInput attribute
    }

    override fun onStart(owner: LifecycleOwner) {
        keyboardVisibilityObservable.asSingleEvent().value = true

        // TODO: For testing purposes only
        searchManager.addSearchResultsListener(searchResultsListener)
        searchManager.searchText("billa"/*, GeoCoordinates(48.157648, 17.128288)*/) // todo: SDK will ignore this call, because the init is not finished yet
    }

    fun onClearButtonClick() {
        inputText.value = EMPTY_STRING
        //todo: cancel previous search request here
    }

    fun onEditorActionEvent(actionId: Int): Boolean {
        return if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            keyboardVisibilityObservable.asSingleEvent().value = false
            true
        } else false
    }

    override fun onStop(owner: LifecycleOwner) {
        // TODO: For testing purposes only
        searchManager.addSearchResultsListener(searchResultsListener)
    }
}