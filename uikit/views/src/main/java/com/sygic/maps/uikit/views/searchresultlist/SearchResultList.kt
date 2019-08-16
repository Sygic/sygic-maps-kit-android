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

package com.sygic.maps.uikit.views.searchresultlist

import android.content.Context
import android.os.Parcelable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sygic.maps.uikit.views.R
import com.sygic.maps.uikit.views.common.AdvancedInfoView
import com.sygic.maps.uikit.views.common.EmptyRecyclerView
import com.sygic.maps.uikit.views.databinding.LayoutSearchResultListInternalBinding
import com.sygic.maps.uikit.views.searchresultlist.adapter.ResultListAdapter
import com.sygic.maps.uikit.views.searchresultlist.adapter.SearchResultListAdapter

/**
 * A [SearchResultList] can be used as an visual presentation component for the search result items. It contains
 * [EmptyRecyclerView] and pre-customized [AdvancedInfoView] component.
 *
 * The [SearchResultList] item layout can be completely changed with the custom _searchResultListStyle_
 * (the _itemLayoutId_ attribute) or you can use the standard android attributes as _colorBackground_, _textColorPrimary_
 * or _textColorSecondary_ definition. Note, the custom item layout provided by _itemLayoutId_ need to have the
 * _searchItemIcon_ ([ImageView]), _searchItemIconRing_ ([ImageView]), _searchItemTitle_ ([TextView]) and _searchItemSubtitle_
 * ([TextView]) identifiers. Or you can extend the [SearchResultListAdapter] and provide your custom [ResultListAdapter] with your
 * custom ViewHolder logic (see [setAdapter] method and [SearchResultListAdapter] implementation).
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
open class SearchResultList @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.searchResultListStyle,
    defStyleRes: Int = R.style.SygicSearchResultListStyle
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes) {

    private val binding: LayoutSearchResultListInternalBinding =
        LayoutSearchResultListInternalBinding.inflate(LayoutInflater.from(context), this, true)

    @LayoutRes
    private var itemLayoutId: Int = R.layout.layout_search_item_result_internal

    init {
        binding.searchResultListRecyclerView.setHasFixedSize(true)
        binding.searchResultListRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.searchResultListRecyclerView.itemAnimator = DefaultItemAnimator()

        attrs?.let { attributeSet ->
            @Suppress("Recycle")
            context.obtainStyledAttributes(
                attributeSet,
                R.styleable.SearchResultList,
                defStyleAttr,
                defStyleRes
            ).also {
                itemLayoutId = it.getResourceId(
                    R.styleable.SearchResultList_itemLayoutId,
                    R.layout.layout_search_item_result_internal
                )

                it.recycle()
            }
        }
    }

    /**
     * Set a new adapter to provide [RecyclerView] child views on demand.
     *
     * When adapter is changed, all existing views are recycled back to the pool. If the pool has
     * only one adapter, it will be cleared.
     *
     * @param adapter The new adapter to set, or null to set no adapter.
     */
    fun <T : Parcelable> setAdapter(adapter: ResultListAdapter<T, ResultListAdapter.ItemViewHolder<T>>) {
        adapter.itemLayoutId = itemLayoutId
        binding.searchResultListRecyclerView.adapter = adapter
    }

    /**
     * Add a listener that will be notified of any changes in scroll state or position.
     *
     * Components that add a listener should take care to remove it when finished.
     * Other components that take ownership of a view may call [clearOnScrollListeners]
     * to remove all attached listeners.
     *
     * @param listener listener to set
     */
    fun addOnScrollListener(listener: RecyclerView.OnScrollListener) {
        binding.searchResultListRecyclerView.addOnScrollListener(listener)
    }

    /**
     * Remove a listener observing changes.
     *
     * Components that add a listener should take care to remove it when finished.
     * Other components that take ownership of a view may call [clearOnScrollListeners]
     * to remove all attached listeners.
     *
     * @param listener listener to remove
     */
    fun removeOnScrollListener(listener: RecyclerView.OnScrollListener) {
        binding.searchResultListRecyclerView.removeOnScrollListener(listener)
    }

    /**
     * Remove all listeners which are registered for scroll state or position changes.
     */
    fun clearOnScrollListeners() {
        binding.searchResultListRecyclerView.clearOnScrollListeners()
    }

    /**
     * Set an active error view of the ErrorViewSwitcher.
     *
     * @param index [SearchResultListErrorViewSwitcherIndex] No Results, Slow internet connection, No internet
     * connection or General error.
     */
    fun setErrorViewSwitcherIndex(@SearchResultListErrorViewSwitcherIndex index: Int) {
        binding.searchResultListErrorViewAnimator.displayedChild = index
    }

    /**
     * Register a callback to be invoked when [SearchResultList] error view with action button is clicked.
     *
     * @param listener [OnClickListener] callback to invoke on [SearchResultList] error view with action button click.
     */
    fun setOnErrorViewWithActionListener(listener: OnClickListener) {
        binding.searchResultListErrorViewWithAction.setOnClickListener(listener)
    }
}