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
import androidx.recyclerview.widget.RecyclerView
import com.sygic.maps.offlinemaps.adapter.viewholder.MapItemViewHolder
import com.sygic.maps.module.common.maploader.MapItem

abstract class InPlaceListAdapter : RecyclerView.Adapter<MapItemViewHolder>() {
    protected var currentMap = LinkedHashMap<String, Int>()
    protected var currentList = emptyList<MapItem>()

    override fun getItemCount() = currentMap.size

    @Suppress("UNCHECKED_CAST")
    fun submitList(list: List<MapItem>) {
        val diffResult = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return areItemsTheSame(currentList[oldItemPosition], list[newItemPosition])
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return areContentsTheSame(currentList[oldItemPosition], list[newItemPosition])
            }

            override fun getOldListSize(): Int {
                return currentMap.size
            }

            override fun getNewListSize(): Int {
                return list.size
            }
        }, false)
        currentList = list.map { it.copy() }
        currentMap.clear()
        currentList.forEachIndexed { index, value ->
            currentMap[value.iso] = index
        }
        diffResult.dispatchUpdatesTo(this)
    }

    fun getItem(position: Int) = currentList[position]

    private companion object : DiffUtil.ItemCallback<MapItem>() {
        override fun areItemsTheSame(oldItem: MapItem, newItem: MapItem) = oldItem.iso == newItem.iso
        override fun areContentsTheSame(oldItem: MapItem, newItem: MapItem) = oldItem == newItem
    }
}
