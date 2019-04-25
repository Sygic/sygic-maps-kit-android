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

package com.sygic.maps.uikit.views.emptyrecyclerview

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.recyclerview.widget.RecyclerView
import com.sygic.maps.uikit.views.R

/**
 * TODO
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
open class EmptyRecyclerView @JvmOverloads constructor( // TODO
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.searchFabStyle, //todo
    defStyleRes: Int = R.style.SygicSearchFabStyle //todo
) : RecyclerView(context, attrs, defStyleAttr) {

    private var emptyViewId: Int = android.R.id.empty
    private var emptyView: View? = null
    private var emptyViewVisible = true
    private var adapterDataObserver: AdapterDataObserver? = null

    init {
        attrs?.let { attributeSet ->
            val typedArray = context.obtainStyledAttributes(attributeSet,
                R.styleable.EmptyRecyclerView,
                defStyleAttr,
                defStyleRes
            )

            emptyViewId = typedArray.getResourceId(R.styleable.EmptyRecyclerView_emptyView, android.R.id.empty)

            typedArray.recycle()
        }
    }

    override fun onAttachedToWindow() { //TODO
        super.onAttachedToWindow()

        if (emptyView == null) {
            setEmptyView(emptyViewId)
        }
    }

    fun setEmptyView(@IdRes emptyViewId: Int) { //TODO
        val parent = this.parent

        if (parent is ViewGroup) {
            emptyView = parent.findViewById(emptyViewId)
            emptyView?.visibility = if (emptyViewVisible) VISIBLE else GONE
        }
    }

    override fun setAdapter(adapter: Adapter<*>?) { //TODO
        val previousAdapter: Adapter<*>? = super.getAdapter()
        previousAdapter?.let {
            adapterDataObserver?.let {
                previousAdapter.unregisterAdapterDataObserver(it)
            }
        }

        adapterDataObserver = object : AdapterDataObserver() {
            override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
                setEmptyViewVisibility(adapter)
            }

            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                setEmptyViewVisibility(adapter)
            }

            override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
                setEmptyViewVisibility(adapter)
            }

            override fun onChanged() {
                setEmptyViewVisibility(adapter)
            }
        }
        adapterDataObserver?.let {
            adapter?.registerAdapterDataObserver(it)
        }
        setEmptyViewVisibility(adapter)
        super.setAdapter(adapter)
    }

    private fun setEmptyViewVisibility(adapter: Adapter<*>?) {
        adapter?.let {
            emptyViewVisible = it.itemCount <= 0
            if (emptyView != null) {    //we want to react only if we have some empty view //TODO
                if (emptyViewVisible) {
                    emptyView?.visibility = VISIBLE
                    visibility = GONE
                } else {
                    emptyView?.visibility = GONE
                    visibility = VISIBLE
                }
            }
        }
    }
}
