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
import androidx.recyclerview.widget.ListAdapter
import com.sygic.maps.offlinemaps.R
import com.sygic.maps.offlinemaps.adapter.viewholder.ContinentViewHolder
import com.sygic.maps.offlinemaps.loader.Continent

class ContinentsAdapter : ListAdapter<Continent, ContinentViewHolder>(DiffCallback) {
    private var onContinentClickedCallback: (Continent) -> Unit = {}

    fun setOnContinentClickListener(callback: (Continent) -> Unit) {
        onContinentClickedCallback = callback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContinentViewHolder {
        val rootView = LayoutInflater.from(parent.context).inflate(R.layout.continent_item_layout, parent, false)
        return ContinentViewHolder(rootView)
    }

    override fun onBindViewHolder(holder: ContinentViewHolder, position: Int) {
        val continent = getItem(position)
        holder.bind(continent)
        holder.itemView.setOnClickListener {
            onContinentClickedCallback(continent)
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Continent>() {
        override fun areItemsTheSame(oldItem: Continent, newItem: Continent) = oldItem.continentName == newItem.continentName
        override fun areContentsTheSame(oldItem: Continent, newItem: Continent) = oldItem == newItem
    }
}
