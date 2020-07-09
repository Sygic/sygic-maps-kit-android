/*
 * Copyright (c) 2020 Sygic a.s. All rights reserved.
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

package com.sygic.maps.offlinemaps.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.sygic.maps.offlinemaps.R
import com.sygic.maps.offlinemaps.adapter.viewholder.CountryEntryViewHolder
import com.sygic.maps.offlinemaps.loader.CountryHolder

class CountryListAdapter : MapListAdapter<CountryHolder, CountryEntryViewHolder>(DiffCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CountryEntryViewHolder {
        val rootView = LayoutInflater.from(parent.context).inflate(R.layout.map_item_layout, parent, false)
        return CountryEntryViewHolder(rootView, loadButtonEnabled)
    }

    companion object DiffCallback : DiffUtil.ItemCallback<CountryHolder>() {
        override fun areItemsTheSame(oldItem: CountryHolder, newItem: CountryHolder) = oldItem.iso == newItem.iso
        override fun areContentsTheSame(oldItem: CountryHolder, newItem: CountryHolder) = oldItem == newItem
    }
}
