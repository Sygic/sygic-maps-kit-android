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

package com.sygic.maps.uikit.views.recyclerview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sygic.maps.uikit.views.R
import com.sygic.maps.uikit.views.common.extensions.doAfterTextChanged

data class ItemView(val name: String, var value: Int)

class SimpleEditAdapter : ListAdapter<ItemView, SimpleEditAdapter.ItemViewHolder>(DiffCallback) {
    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val itemName = itemView.findViewById<TextView>(R.id.item_name)
        private val itemValue = itemView.findViewById<EditText>(R.id.item_value)
        private val removeButton = itemView.findViewById<ImageButton>(R.id.remove_button)

        fun bind(item: ItemView, onRemoveItem: (String) -> Unit) {
            itemName.text = item.name
            itemValue.setText(item.value.toString())
            itemValue.doAfterTextChanged { text ->
                item.value = text?.toString()?.toIntOrNull() ?: 0
            }
            removeButton.setOnClickListener { onRemoveItem(item.name) }
        }
    }

    private var onItemRemoved: (ItemView) -> Unit = {}

    var items: List<ItemView>
        get() = currentList
        set(value) = submitList(value)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ItemViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_simple_list_edit_item, parent, false)
        )

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) =
        holder.bind(getItem(position), ::onRemoveItem)

    fun setOnItemRemoved(listener: (ItemView) -> Unit) {
        onItemRemoved = listener
    }

    fun addItem(item: ItemView) = submitList(currentList + item)

    private fun onRemoveItem(itemName: String) {
        val itemToRemove = currentList.find { it.name == itemName }!!
        submitList(currentList.filter { it.name != itemName })
        onItemRemoved(itemToRemove)
    }

    companion object DiffCallback : DiffUtil.ItemCallback<ItemView>() {
        override fun areItemsTheSame(oldItem: ItemView, newItem: ItemView) =
            oldItem.name == newItem.name

        override fun areContentsTheSame(oldItem: ItemView, newItem: ItemView): Boolean =
            oldItem == newItem
    }
}