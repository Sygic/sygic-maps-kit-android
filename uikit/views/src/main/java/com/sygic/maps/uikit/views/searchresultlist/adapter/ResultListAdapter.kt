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

package com.sygic.maps.uikit.views.searchresultlist.adapter

import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.sygic.maps.uikit.views.R
import com.sygic.maps.uikit.views.searchresultlist.data.SearchResultItem

abstract class ResultListAdapter<P : Parcelable, T : ResultListAdapter.ItemViewHolder<P>> : RecyclerView.Adapter<T>() {

    var clickListener: ClickListener<P>? = null
    internal var itemLayoutId = R.layout.layout_search_item_result_internal

    interface ClickListener<P : Parcelable> {
        fun onSearchResultItemClick(searchResultItem: SearchResultItem<out P>)
    }

    abstract fun onCreateViewHolder(
        parent: ViewGroup,
        inflater: LayoutInflater, @LayoutRes layoutId: Int,
        viewType: Int
    ): T

    final override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): T =
        onCreateViewHolder(parent, LayoutInflater.from(parent.context), itemLayoutId, viewType)

    abstract class ItemViewHolder<P : Parcelable>(view: View) : RecyclerView.ViewHolder(view) {
        open fun update(searchResultItem: SearchResultItem<out P>) {}
    }
}