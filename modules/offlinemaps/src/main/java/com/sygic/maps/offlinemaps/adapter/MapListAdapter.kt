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

import androidx.recyclerview.widget.DiffUtil
import com.sygic.maps.offlinemaps.adapter.viewholder.MapEntryViewHolder
import com.sygic.maps.offlinemaps.loader.MapHolder
import com.sygic.sdk.map.MapLoader

abstract class MapListAdapter<T : MapHolder, VH : MapEntryViewHolder<T>>(diffCallback: DiffUtil.ItemCallback<T>) : InPlaceListAdapter<T, VH>(diffCallback) {
    private var onItemClickedCallback: (T) -> Unit = {}
    private var onPrimaryActionClicked: (T) -> Unit = {}
    private var onLoadButtonClicked: (T) -> Unit = {}
    private var onUpdateButtonClicked: (T) -> Unit = {}

    protected var loadButtonEnabled = true

    fun setOnItemClicked(callback: (T) -> Unit) {
        onItemClickedCallback = callback
    }

    fun setOnPrimaryActionClicked(callback: (T) -> Unit) {
        onPrimaryActionClicked = callback
    }

    fun setOnUpdateButtonClicked(callback: (T) -> Unit) {
        onUpdateButtonClicked = callback
    }

    fun setOnLoadButtonClicked(callback: (T) -> Unit) {
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
            currentList[it].status = status
            notifyItemChanged(it, status)
        }
    }

    fun updateProgress(iso: String, progress: Int) {
        currentMap[iso]?.let {
            currentList[it].progress = progress
            notifyItemChanged(it, progress)
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun onBindViewHolder(holder: VH, position: Int, payloads: MutableList<Any>) {
        super.onBindViewHolder(holder, position, payloads)
        val item = getItem(position)
        when {
            payloads.isEmpty() -> holder.bind(item)
            else -> {
                payloads.forEach { payload ->
                    when (payload) {
                        is Int -> holder.updateProgress(payload)
                        is MapLoader.MapStatus -> {
                            holder.updateFromStatus(payload, item.progress)
                        }
                    }
                }
            }
        }
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = getItem(position)
        holder.itemView.setOnClickListener {
            onItemClickedCallback(item)
        }
        holder.setPrimaryActionClickListener {
            onPrimaryActionClicked(item)
        }
        holder.setLoadButtonClickListener {
            onLoadButtonClicked(item)
        }
        holder.setUpdateButtonClickListener {
            onUpdateButtonClicked(item)
        }
    }
}
