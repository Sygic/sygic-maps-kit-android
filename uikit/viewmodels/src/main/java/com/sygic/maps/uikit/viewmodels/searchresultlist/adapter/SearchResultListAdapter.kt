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

package com.sygic.maps.uikit.viewmodels.searchresultlist.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sygic.maps.uikit.viewmodels.common.sdk.search.SearchResultItem
import com.sygic.maps.uikit.views.common.extensions.backgroundTint
import com.sygic.maps.uikit.views.common.extensions.tint
import com.sygic.maps.uikit.views.common.extensions.visible
import com.sygic.maps.uikit.views.databinding.LayoutSearchItemResultInternalBinding

class SearchResultListAdapter : RecyclerView.Adapter<SearchResultListAdapter.SearchResultItemViewHolder>() {

    var clickListener: ClickListener? = null
    var items: List<SearchResultItem<*>> = mutableListOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    interface ClickListener {
        fun onSearchResultItemClick(searchResultItem: SearchResultItem<*>)
    }

    override fun getItemCount(): Int = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchResultItemViewHolder {
        return SearchResultItemViewHolder(
            LayoutSearchItemResultInternalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: SearchResultItemViewHolder, position: Int) {
        holder.update(items[position])
    }

    inner class SearchResultItemViewHolder(val binding: LayoutSearchItemResultInternalBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener { clickListener?.onSearchResultItemClick(items[adapterPosition]) }
        }

        fun update(searchResultItem: SearchResultItem<*>) {
            binding.searchItemIcon.setImageResource(searchResultItem.icon)
            binding.searchItemIcon.tint(searchResultItem.iconColor)
            binding.searchItemIcon.backgroundTint(searchResultItem.iconBackgroundColor)
            binding.searchItemIconRing.tint(searchResultItem.iconBackgroundColor)
            binding.searchItemIconRing.visible(searchResultItem.isCategory)
            binding.searchItemTitle.text = searchResultItem.title
            binding.searchItemSubtitle.text = searchResultItem.subTitle
            binding.searchItemSubtitle.visible(searchResultItem.subTitle.isNotEmpty())
        }
    }
}