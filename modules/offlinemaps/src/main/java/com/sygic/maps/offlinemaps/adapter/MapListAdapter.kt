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
import com.sygic.maps.offlinemaps.R
import com.sygic.maps.offlinemaps.adapter.viewholder.MapItemViewHolder
import com.sygic.sdk.map.MapLoader

class MapListAdapter : InPlaceListAdapter() {
    private var onItemClickedCallback: (String) -> Unit = {}
    private var onPrimaryActionClicked: (String) -> Unit = {}
    private var onLoadButtonClicked: (String) -> Unit = {}
    private var onUpdateButtonClicked: (String) -> Unit = {}

    private var loadButtonEnabled = true

    fun setOnItemClicked(callback: (String) -> Unit) {
        onItemClickedCallback = callback
    }

    fun setOnPrimaryActionClicked(callback: (String) -> Unit) {
        onPrimaryActionClicked = callback
    }

    fun setOnUpdateButtonClicked(callback: (String) -> Unit) {
        onUpdateButtonClicked = callback
    }

    fun setOnLoadButtonClicked(callback: (String) -> Unit) {
        onLoadButtonClicked = callback
    }

    fun setEnableLoadUnloadButton(enabled: Boolean) {
        loadButtonEnabled = enabled
    }

    fun refreshList() {
        notifyDataSetChanged()
    }

    fun updateStatus(iso: String, status: MapLoader.MapStatus) {
        currentMap[iso]?.let {
            currentList[it].data.status = status
            notifyItemChanged(it, status)
        }
    }

    fun updateProgress(iso: String, progress: Int) {
        currentMap[iso]?.let {
            currentList[it].data.progress = progress
            notifyItemChanged(it, progress)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MapItemViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.map_item_layout, parent, false)
        return MapItemViewHolder(itemView)
    }

    @Suppress("UNCHECKED_CAST")
    override fun onBindViewHolder(holder: MapItemViewHolder, position: Int, payloads: MutableList<Any>) {
        super.onBindViewHolder(holder, position, payloads)
        val item = getItem(position)
        when {
            payloads.isEmpty() -> holder.bind(item)
            else -> {
                payloads.forEach { payload ->
                    when (payload) {
                        is Int -> holder.updateProgress(payload)
                        is MapLoader.MapStatus -> holder.updateStatus(payload, item.data.progress)
                    }
                }
            }
        }
    }

    override fun onBindViewHolder(holder: MapItemViewHolder, position: Int) {
        val item = getItem(position)
        holder.itemView.setOnClickListener { onItemClickedCallback(item.iso) }
        holder.setPrimaryActionClickListener { onPrimaryActionClicked(item.iso) }
        holder.setLoadButtonClickListener { onLoadButtonClicked(item.iso) }
        holder.setUpdateButtonClickListener { onUpdateButtonClicked(item.iso) }
    }
}
