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
import android.view.ViewGroup
import com.sygic.maps.uikit.views.common.extensions.backgroundTint
import com.sygic.maps.uikit.views.common.extensions.text
import com.sygic.maps.uikit.views.common.extensions.tint
import com.sygic.maps.uikit.views.common.extensions.visible
import com.sygic.maps.uikit.views.databinding.LayoutSearchItemResultInternalBinding
import com.sygic.maps.uikit.views.searchresultlist.data.SearchResultItem

open class SearchResultListAdapter<P : Parcelable> : ResultListAdapter<P, ResultListAdapter.ItemViewHolder<P>>() {

    var items: List<SearchResultItem<out P>> = mutableListOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ItemViewHolder<P>, position: Int) = holder.update(items[position])

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder<P> = ListItemViewHolder(
        LayoutSearchItemResultInternalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    inner class ListItemViewHolder(val binding: LayoutSearchItemResultInternalBinding) : ItemViewHolder<P>(binding.root) {

        init {
            binding.root.setOnClickListener { clickListener?.onSearchResultItemClick(items[adapterPosition]) }
        }

        override fun update(searchResultItem: SearchResultItem<out P>) {
            binding.searchItemIcon.setImageResource(searchResultItem.icon)
            binding.searchItemIcon.tint(searchResultItem.iconColor)
            binding.searchItemIcon.backgroundTint(searchResultItem.iconBackgroundColor)
            binding.searchItemIconRing.tint(searchResultItem.iconBackgroundColor)
            binding.searchItemIconRing.visible(searchResultItem.iconRingVisible)
            binding.searchItemTitle.text(searchResultItem.title)
            binding.searchItemSubtitle.text(searchResultItem.subTitle)
            binding.searchItemSubtitle.visible(searchResultItem.subTitle.isNotEmpty())
        }
    }
}