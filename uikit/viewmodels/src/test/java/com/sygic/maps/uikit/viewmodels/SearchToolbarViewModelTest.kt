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
import androidx.recyclerview.widget.RecyclerView
import com.jraska.livedata.test
import com.nhaarman.mockitokotlin2.*
import com.sygic.maps.uikit.viewmodels.common.search.MAX_RESULTS_COUNT_DEFAULT_VALUE
import com.sygic.maps.uikit.viewmodels.common.search.SearchManager
import com.sygic.maps.uikit.viewmodels.searchtoolbar.SearchToolbarViewModel
import com.sygic.maps.uikit.viewmodels.searchtoolbar.component.KEY_SEARCH_INPUT
import com.sygic.maps.uikit.viewmodels.searchtoolbar.component.KEY_SEARCH_LOCATION
import com.sygic.maps.uikit.viewmodels.searchtoolbar.component.KEY_SEARCH_MAX_RESULTS_COUNT
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
    private lateinit var searchManager: SearchManager

    private lateinit var searchToolbarViewModel: SearchToolbarViewModel

    private val testDispatcher = TestCoroutineDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        val arguments = mock<Bundle>()
        whenever(arguments.getString(eq(KEY_SEARCH_INPUT))).thenReturn(EMPTY_STRING)
        whenever(arguments.getParcelable<GeoCoordinates>(eq(KEY_SEARCH_LOCATION))).thenReturn(null)
        whenever(arguments.getInt(eq(KEY_SEARCH_MAX_RESULTS_COUNT), any())).thenReturn(MAX_RESULTS_COUNT_DEFAULT_VALUE)

        searchToolbarViewModel = SearchToolbarViewModel(arguments, searchManager)
        searchToolbarViewModel.searchDelay = CUSTOM_SEARCH_DELAY
    }

    @Test
    fun initTest() {
        assertEquals(true, searchToolbarViewModel.searchToolbarFocused.value)
        assertEquals(SearchToolbarIconStateSwitcherIndex.MAGNIFIER, searchToolbarViewModel.iconStateSwitcherIndex.value)
        assertEquals(null, searchToolbarViewModel.searchLocation)
        assertEquals(EMPTY_STRING, searchToolbarViewModel.inputText.value)
        verify(searchManager).addSearchResultsListener(any())
    }

    @Test
    fun searchTest() {
        runBlockingTest(testDispatcher) {
            assertEquals(
                SearchToolbarIconStateSwitcherIndex.MAGNIFIER,
                searchToolbarViewModel.iconStateSwitcherIndex.value
            )
            searchToolbarViewModel.inputText.value = "London Eye"
            assertEquals(
                SearchToolbarIconStateSwitcherIndex.PROGRESSBAR,
                searchToolbarViewModel.iconStateSwitcherIndex.value
            )
            advanceTimeBy(CUSTOM_SEARCH_DELAY)
            verify(searchManager).searchText(eq("London Eye"), anyOrNull())
        }
    }

    @Test
    fun searchWithLocationTest() {
        runBlockingTest(testDispatcher) {
            val testLocation = GeoCoordinates(51.507320, -0.127786)
            searchToolbarViewModel.searchLocation = testLocation
            searchToolbarViewModel.inputText.value = "London Eye"
            advanceTimeBy(CUSTOM_SEARCH_DELAY)
            assertEquals(
                SearchToolbarIconStateSwitcherIndex.PROGRESSBAR,
                searchToolbarViewModel.iconStateSwitcherIndex.value
            )
            verify(searchManager).searchText(eq("London Eye"), eq(testLocation))
        }
    }

    @Test
    fun retrySearchTest() {
        runBlockingTest(testDispatcher) {
            searchToolbarViewModel.inputText.value = "London Eye"
            advanceTimeBy(CUSTOM_SEARCH_DELAY)
            assertEquals(
                SearchToolbarIconStateSwitcherIndex.PROGRESSBAR,
                searchToolbarViewModel.iconStateSwitcherIndex.value
            )
            verify(searchManager).searchText(eq("London Eye"), anyOrNull())

            searchToolbarViewModel.retrySearch()
            advanceTimeBy(CUSTOM_SEARCH_DELAY)
            verify(searchManager, times(2)).searchText(eq("London Eye"), anyOrNull())
        }
    }

    @Test
    fun onClearButtonClickTest() {
        runBlockingTest(testDispatcher) {
            searchToolbarViewModel.inputText.value = "London Eye"
            advanceTimeBy(CUSTOM_SEARCH_DELAY)
            assertEquals(
                SearchToolbarIconStateSwitcherIndex.PROGRESSBAR,
                searchToolbarViewModel.iconStateSwitcherIndex.value
            )
            verify(searchManager).searchText(eq("London Eye"), anyOrNull())

            searchToolbarViewModel.onClearButtonClick()
            assertEquals(EMPTY_STRING, searchToolbarViewModel.inputText.value)
            verify(searchManager, times(2)).searchText(eq(EMPTY_STRING), anyOrNull())
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