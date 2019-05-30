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
import android.view.View
import android.view.ViewGroup

//ToDo: Use the Recents adapter instead when ready
class DefaultStateAdapter<P : Parcelable> : ResultListAdapter<P, ResultListAdapter.ItemViewHolder<P>>() {
    override fun getItemCount(): Int = 1
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder<P> = EmptyItemViewHolder(View(parent.context))
    override fun onBindViewHolder(holder: ItemViewHolder<P>, position: Int) {}
    inner class EmptyItemViewHolder(emptyView: View) : ItemViewHolder<P>(emptyView)
}