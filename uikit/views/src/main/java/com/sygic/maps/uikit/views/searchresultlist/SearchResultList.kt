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

package com.sygic.maps.uikit.views.searchresultlist

import android.content.Context
import android.os.Parcelable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sygic.maps.uikit.views.R
import com.sygic.maps.uikit.views.common.AdvanceInfoView
import com.sygic.maps.uikit.views.common.EmptyRecyclerView
import com.sygic.maps.uikit.views.databinding.LayoutSearchResultListInternalBinding
import com.sygic.maps.uikit.views.searchresultlist.adapter.ResultListAdapter

/**
 * A [SearchResultList] can be used as an visual presentation component for the search result items. It contains
 * [EmptyRecyclerView] and pre-customized [AdvanceInfoView] component.
 *
 * TODO: MS-5681
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
open class SearchResultList @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.searchResultListStyle,
    defStyleRes: Int = R.style.SygicSearchResultListStyle // TODO: MS-5681
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes) {

    private val binding: LayoutSearchResultListInternalBinding =
        LayoutSearchResultListInternalBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        binding.searchResultListRecyclerView.setHasFixedSize(true)
        binding.searchResultListRecyclerView.layoutManager = LinearLayoutManager(context)
    }

    /**
     * Set a new adapter to provide [RecyclerView] child views on demand.
     *
     * When adapter is changed, all existing views are recycled back to the pool. If the pool has
     * only one adapter, it will be cleared.
     *
     * @param adapter The new adapter to set, or null to set no adapter.
     */
    fun <T : Parcelable> setAdapter(adapter: ResultListAdapter<T, ResultListAdapter.ItemViewHolder<T>>) {
        binding.searchResultListRecyclerView.adapter = adapter
    }
}