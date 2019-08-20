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

package com.sygic.maps.uikit.views.navigation.actionmenu.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sygic.maps.uikit.views.common.extensions.text
import com.sygic.maps.uikit.views.common.extensions.tint
import com.sygic.maps.uikit.views.databinding.LayoutActionMenuItemInternalBinding
import com.sygic.maps.uikit.views.navigation.actionmenu.data.ActionMenuItem
import com.sygic.maps.uikit.views.navigation.actionmenu.listener.ActionMenuItemClickListener

internal class ActionMenuAdapter : RecyclerView.Adapter<ActionMenuAdapter.ItemViewHolder>() {

    var clickListener: ActionMenuItemClickListener? = null
    var items: List<ActionMenuItem> = mutableListOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount(): Int = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ItemViewHolder(LayoutActionMenuItemInternalBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) = holder.update(items[position])

    inner class ItemViewHolder(private val binding: LayoutActionMenuItemInternalBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener { clickListener?.onActionMenuItemClick(items[adapterPosition]) }
        }

        fun update(actionMenuItem: ActionMenuItem) {
            binding.actionMenuItemImageView.setImageResource(actionMenuItem.icon)
            binding.actionMenuItemImageView.tint(actionMenuItem.iconColor)
            binding.actionMenuItemTextView.text(actionMenuItem.title)
        }
    }
}