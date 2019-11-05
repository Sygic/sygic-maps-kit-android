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

package com.sygic.maps.uikit.viewmodels

import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.jraska.livedata.test
import com.nhaarman.mockitokotlin2.*
import com.sygic.maps.uikit.viewmodels.common.search.MAX_RESULTS_COUNT_DEFAULT_VALUE
import com.sygic.maps.uikit.viewmodels.common.search.SearchManagerClient
import com.sygic.maps.uikit.viewmodels.common.search.state.ResultState
import com.sygic.maps.uikit.viewmodels.searchtoolbar.SearchToolbarViewModel
import com.sygic.maps.uikit.viewmodels.searchtoolbar.component.KEY_SEARCH_INPUT
import com.sygic.maps.uikit.viewmodels.searchtoolbar.component.KEY_SEARCH_LOCATION
import com.sygic.maps.uikit.viewmodels.searchtoolbar.component.KEY_SEARCH_MAX_RESULTS_COUNT
import com.sygic.maps.uikit.viewmodels.utils.LiveDataResumedLifecycleOwner
import com.sygic.maps.uikit.views.common.extensions.EMPTY_STRING
import com.sygic.maps.uikit.views.common.extensions.showKeyboard
import com.sygic.maps.uikit.views.searchtoolbar.SearchToolbarIconStateSwitcherIndex
import com.sygic.sdk.position.GeoCoordinates
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

const val CUSTOM_SEARCH_DELAY = 500L

@kotlinx.coroutines.ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class SearchToolbarViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var searchManagerClient: SearchManagerClient

    private lateinit var searchToolbarViewModel: SearchToolbarViewModel

    private val testDispatcher = TestCoroutineDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        val arguments = mock<Bundle>()
        whenever(arguments.getString(eq(KEY_SEARCH_INPUT))).thenReturn(EMPTY_STRING)
        whenever(arguments.getParcelable<GeoCoordinates>(eq(KEY_SEARCH_LOCATION))).thenReturn(null)
        whenever(arguments.getInt(eq(KEY_SEARCH_MAX_RESULTS_COUNT), any())).thenReturn(MAX_RESULTS_COUNT_DEFAULT_VALUE)

        whenever(searchManagerClient.searchText).thenReturn(mock())
        whenever(searchManagerClient.autocompleteResultState).thenReturn(mock())
        val searchLocationMock = mock<MutableLiveData<GeoCoordinates>>()
        whenever(searchLocationMock.value).thenReturn(GeoCoordinates.Invalid)
        whenever(searchManagerClient.searchLocation).thenReturn(searchLocationMock)
        whenever(searchManagerClient.maxResultsCount).thenReturn(mock())

        searchToolbarViewModel = SearchToolbarViewModel(arguments, searchManagerClient)
        searchToolbarViewModel.searchDelay = CUSTOM_SEARCH_DELAY

        val searchText = argumentCaptor<String>()
        val searchResultsObserverCaptor = argumentCaptor<Observer<ResultState>>()
        whenever(searchManagerClient.autocompleteResultState.observe(any(), searchResultsObserverCaptor.capture())).then { /* do nothing */ }
        whenever(searchManagerClient.searchText.setValue(searchText.capture())).then {
            searchResultsObserverCaptor.firstValue.onChanged(ResultState.SUCCESS)
        }
    }

    @Test
    fun initTest() {
        assertEquals(true, searchToolbarViewModel.searchToolbarFocused.value)
        assertEquals(SearchToolbarIconStateSwitcherIndex.PROGRESSBAR, searchToolbarViewModel.iconStateSwitcherIndex.value)
        assertEquals(GeoCoordinates.Invalid, searchToolbarViewModel.searchLocation)
        assertEquals(EMPTY_STRING, searchToolbarViewModel.inputText.value)
    }

    @Test
    fun onCreateTest() {
        val resumedLifecycleOwner = LiveDataResumedLifecycleOwner()
        searchToolbarViewModel.onCreate(resumedLifecycleOwner)
        verify(searchManagerClient.autocompleteResultState).observe(eq(resumedLifecycleOwner), any())
    }

    @Test
    fun searchTest() {
        runBlockingTest(testDispatcher) {
            searchToolbarViewModel.onCreate(LiveDataResumedLifecycleOwner())

            searchToolbarViewModel.inputText.value = "London Eye"
            assertEquals(
                SearchToolbarIconStateSwitcherIndex.PROGRESSBAR,
                searchToolbarViewModel.iconStateSwitcherIndex.value
            )
            advanceTimeBy(CUSTOM_SEARCH_DELAY)
            verify(searchManagerClient.searchText).value = "London Eye"

            assertEquals(
                SearchToolbarIconStateSwitcherIndex.MAGNIFIER,
                searchToolbarViewModel.iconStateSwitcherIndex.value
            )
        }
    }

    @Test
    fun searchWithLocationTest() {
        runBlockingTest(testDispatcher) {
            searchToolbarViewModel.onCreate(LiveDataResumedLifecycleOwner())

            val testLocation = GeoCoordinates(51.507320, -0.127786)
            searchToolbarViewModel.searchLocation = testLocation
            searchToolbarViewModel.inputText.value = "London Eye"
            advanceTimeBy(CUSTOM_SEARCH_DELAY)
            verify(searchManagerClient.searchText).value = "London Eye"
            verify(searchManagerClient.searchLocation).value = testLocation
        }
    }

    @Test
    fun retrySearchTest() {
        runBlockingTest(testDispatcher) {
            searchToolbarViewModel.onCreate(LiveDataResumedLifecycleOwner())

            searchToolbarViewModel.inputText.value = "London Eye"
            advanceTimeBy(CUSTOM_SEARCH_DELAY)
            verify(searchManagerClient.searchText).value = "London Eye"

            searchToolbarViewModel.retrySearch()
            advanceTimeBy(CUSTOM_SEARCH_DELAY)
            verify(searchManagerClient.searchText, times(2)).value = "London Eye"
        }
    }

    @Test
    fun onClearButtonClickTest() {
        runBlockingTest(testDispatcher) {
            searchToolbarViewModel.onCreate(LiveDataResumedLifecycleOwner())

            searchToolbarViewModel.inputText.value = "London Eye"
            advanceTimeBy(CUSTOM_SEARCH_DELAY)
            verify(searchManagerClient.searchText).value = "London Eye"

            searchToolbarViewModel.onClearButtonClick()
            assertEquals(EMPTY_STRING, searchToolbarViewModel.inputText.value)
            verify(searchManagerClient.searchText, times(2)).value = EMPTY_STRING
        }
    }

    @Test
    fun onEditorActionEventSearchTest() {
        val textViewMock = mock<TextView>()
        searchToolbarViewModel.onEditorActionEvent(textViewMock, EditorInfo.IME_ACTION_SEARCH)
        searchToolbarViewModel.onActionSearchClickObservable.test().assertValue(textViewMock)
    }

    @Test
    fun onFocusChangedTest() {
        val recyclerViewMock = mock<RecyclerView>()
        searchToolbarViewModel.onFocusChanged(recyclerViewMock, true)
        verify(recyclerViewMock).showKeyboard()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
}