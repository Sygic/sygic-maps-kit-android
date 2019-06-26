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

package com.sygic.maps.uikit.views.common

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.recyclerview.widget.RecyclerView
import com.sygic.maps.uikit.views.R

/**
 * A [EmptyRecyclerView] is an extended version of the standard [RecyclerView] class. When emptyViewId is set, then this
 * view automatically observes [RecyclerView.Adapter] data changes and sets an appropriate empty view visibility state.
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
open class EmptyRecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr) {

    private var emptyViewId: Int = View.NO_ID
    private var emptyView: View? = null
    private var emptyViewVisible = true

    private var adapterDataObserver: AdapterDataObserver? = null

    init {
        attrs?.let { attributeSet ->
            context.obtainStyledAttributes(attributeSet, R.styleable.EmptyRecyclerView).apply {
                emptyViewId = getResourceId(R.styleable.EmptyRecyclerView_emptyView, View.NO_ID)
                recycle()
            }
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        if (emptyView == null) setEmptyView(emptyViewId)
    }

    fun setEmptyView(@IdRes emptyViewId: Int) {
        if (emptyViewId == View.NO_ID) {
            return
        }

        parent.let {
            if (it is ViewGroup) {
                emptyView = it.findViewById<View>(emptyViewId).apply {
                    visibility = if (emptyViewVisible) VISIBLE else GONE
                }
            }
        }
    }

    override fun setAdapter(adapter: Adapter<*>?) {
        adapter?.let {
            super.getAdapter()?.let { currentAdapter ->
                adapterDataObserver?.let { observer -> currentAdapter.unregisterAdapterDataObserver(observer) }
            }

            adapterDataObserver = object : AdapterDataObserver() {
                override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) = setEmptyViewVisibility(it)
                override fun onItemRangeInserted(positionStart: Int, itemCount: Int) = setEmptyViewVisibility(it)
                override fun onItemRangeChanged(positionStart: Int, itemCount: Int) = setEmptyViewVisibility(it)
                override fun onChanged() = setEmptyViewVisibility(it)
            }.apply {
                it.registerAdapterDataObserver(this)
                setEmptyViewVisibility(it)
            }
        }

        super.setAdapter(adapter)
    }

    private fun setEmptyViewVisibility(adapter: Adapter<*>) {
        emptyViewVisible = adapter.itemCount <= 0
        emptyView?.let {
            if (emptyViewVisible) {
                it.visibility = VISIBLE
                this.visibility = GONE
            } else {
                it.visibility = GONE
                this.visibility = VISIBLE
            }
        }
    }
}
