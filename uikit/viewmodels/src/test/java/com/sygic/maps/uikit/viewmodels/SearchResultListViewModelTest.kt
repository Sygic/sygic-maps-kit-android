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

import android.widget.TextView
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.recyclerview.widget.RecyclerView
import com.jraska.livedata.test
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.sygic.maps.uikit.viewmodels.common.search.SearchManagerClient
import com.sygic.maps.uikit.viewmodels.searchresultlist.SearchResultListViewModel
import com.sygic.maps.uikit.views.common.extensions.hideKeyboard
import com.sygic.maps.uikit.views.searchresultlist.SearchResultListErrorViewSwitcherIndex
import com.sygic.maps.uikit.views.searchresultlist.adapter.DefaultStateAdapter
import com.sygic.maps.uikit.views.searchresultlist.data.SearchResultItem
import com.sygic.sdk.search.SearchResult
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class SearchResultListViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var searchManagerClient: SearchManagerClient

    private lateinit var searchResultListViewModel: SearchResultListViewModel

    @Before
    fun setup() {
        searchResultListViewModel = SearchResultListViewModel(searchManagerClient, mock())
    }

    @Test
    fun initTest() {
        assertEquals(true, searchResultListViewModel.activeAdapter.value is DefaultStateAdapter<SearchResult>)
        assertEquals(SearchResultListErrorViewSwitcherIndex.NO_RESULTS_FOUND, searchResultListViewModel.errorViewSwitcherIndex.value)
        verify(searchManagerClient).addSearchResultsListener(any())
    }

    @Test
    fun onResultListScrollStateChangedTest() {
        val recyclerViewMock = mock<RecyclerView>()
        searchResultListViewModel.onResultListScrollStateChanged(recyclerViewMock, RecyclerView.SCROLL_STATE_DRAGGING)
        verify(recyclerViewMock).hideKeyboard()
        verify(recyclerViewMock).requestFocus()
    }

    @Test
    fun onSearchResultItemClickTest() {
        val textViewMock = mock<TextView>()
        val searchResultItemMock = mock<SearchResultItem<SearchResult>>()
        searchResultListViewModel.onSearchResultItemClick(textViewMock, searchResultItemMock)
        verify(textViewMock).hideKeyboard()
        searchResultListViewModel.onSearchResultItemClickObservable.test().assertValue(searchResultItemMock)
    }
}