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

package com.sygic.maps.module.search

import android.app.Application
import android.widget.TextView
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import com.jraska.livedata.test
import com.nhaarman.mockitokotlin2.*
import com.sygic.maps.module.search.callback.SearchResultCallback
import com.sygic.maps.module.search.callback.SearchResultCallbackWrapper
import com.sygic.maps.module.search.viewmodel.SearchFragmentViewModel
import com.sygic.maps.uikit.views.common.extensions.hideKeyboard
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class SearchFragmentViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var app: Application

    private lateinit var searchFragmentViewModel: SearchFragmentViewModel

    @Before
    fun setup() {
        searchFragmentViewModel = SearchFragmentViewModel(app, mock())
    }

    @Test
    fun onCreateTest() {
        val searchResultCallbackProvider = mock<LiveData<SearchResultCallback>>()
        val lifecycleOwnerMock = mock<LifecycleOwner>(extraInterfaces = arrayOf(SearchResultCallbackWrapper::class))
        whenever((lifecycleOwnerMock as SearchResultCallbackWrapper).searchResultCallbackProvider).thenReturn(
            searchResultCallbackProvider
        )
        searchFragmentViewModel.onCreate(lifecycleOwnerMock)
        verify(searchResultCallbackProvider).observe(eq(lifecycleOwnerMock), any())
    }

    @Test
    fun onSearchResultItemClickTest() {
        searchFragmentViewModel.onSearchResultItemClick(mock())
        searchFragmentViewModel.onFinishObservable.test().assertHasValue()
    }

    @Test
    fun onActionSearchClickTest() {
        val textViewMock = mock<TextView>()
        searchFragmentViewModel.onActionSearchClick(textViewMock)
        verify(textViewMock).hideKeyboard()
        searchFragmentViewModel.onFinishObservable.test().assertHasValue()
    }
}