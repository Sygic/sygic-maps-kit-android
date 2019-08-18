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

package com.sygic.maps.uikit.views.searchtoolbar

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.sygic.maps.uikit.views.R
import com.sygic.maps.uikit.views.common.extensions.EMPTY_STRING
import com.sygic.maps.uikit.views.databinding.LayoutSearchToolbarInternalBinding

/**
 * A [SearchToolbar] can be used as an input component to the search screen. It contains [EditText] input field, state
 * switcher (MAGNIFIER or PROGRESSBAR) and clear [Button].
 *
 * The [SearchToolbar] search icon and the clear button icon can be changed with the custom _searchToolbarStyle_
 * (the _searchIcon_ or _clearButtonIcon_ attribute) or you can use the standard android attributes as _colorBackground_,
 * _textColorPrimary_ or _textColorSecondary_ definition. The [SearchToolbar] hint can be overridden by the _search_hint_ string.
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
open class SearchToolbar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.searchToolbarStyle,
    defStyleRes: Int = R.style.SygicSearchToolbarStyle
) : Toolbar(context, attrs, defStyleAttr) {

    private val binding: LayoutSearchToolbarInternalBinding =
        LayoutSearchToolbarInternalBinding.inflate(LayoutInflater.from(context), this, true)

    /**
     * Set the text to be displayed in the input [EditText].
     *
     * @param [CharSequence] text to be displayed.
     *
     * @return current text from the input [EditText].
     */
    var text: CharSequence
        get() = binding.searchToolbarInputEditText.text?.toString() ?: EMPTY_STRING
        set(value) {
            binding.searchToolbarInputEditText.text?.let { editable ->
                if (editable.toString() != value) setTextInternal(value)
            } ?: run {
                setTextInternal(value)
            }
        }

    init {
        descendantFocusability = FOCUS_AFTER_DESCENDANTS

        attrs?.let { attributeSet ->
            @Suppress("Recycle")
            context.obtainStyledAttributes(
                attributeSet,
                R.styleable.SearchToolbar,
                defStyleAttr,
                defStyleRes
            ).also {
                binding.searchToolbarSearchIcon.setImageResource(
                    it.getResourceId(R.styleable.SearchToolbar_searchIcon, R.drawable.ic_search_robust)
                )

                binding.searchToolbarClearButton.setImageResource(
                    it.getResourceId(R.styleable.SearchToolbar_clearButtonIcon, R.drawable.ic_close)
                )

            }.recycle()
        }
    }

    private fun setTextInternal(text: CharSequence) {
        binding.searchToolbarInputEditText.setText(text)
        binding.searchToolbarInputEditText.setSelection(text.length)
    }

    override fun onRequestFocusInDescendants(direction: Int, previouslyFocusedRect: Rect?): Boolean =
        binding.searchToolbarInputEditText.requestFocus()

    override fun setOnFocusChangeListener(listener: OnFocusChangeListener) {
        binding.searchToolbarInputEditText.onFocusChangeListener = listener
    }

    override fun getOnFocusChangeListener(): OnFocusChangeListener {
        return binding.searchToolbarInputEditText.onFocusChangeListener
    }

    /**
     * Set the focused state of the [SearchToolbar] view. Internally is called [requestFocus]
     * or [clearFocus] method and delegated to the input [EditText] view.
     *
     * @param focused [Boolean] true to make this view focused, false otherwise.
     */
    fun setFocused(focused: Boolean) {
        if (focused) {
            requestFocus()
        } else {
            clearFocus()
        }
    }

    /**
     * Set a special listener to be called when an action is performed
     * on the InputEditText view. This will be called when the enter key is pressed,
     * or when an action supplied to the IME is selected by the user. Setting
     * this means that the normal hard key event will not insert a newline
     * into the text view, even if it is multi-line; holding down the ALT
     * modifier will, however, allow the user to insert a newline character.
     */
    fun setOnEditorActionListener(listener: TextView.OnEditorActionListener) {
        binding.searchToolbarInputEditText.setOnEditorActionListener(listener)
    }

    /**
     * Set the visibility state of the IconStateSwitcher view.
     *
     * @param visibility One of VISIBLE, INVISIBLE, or GONE.
     */
    fun setIconStateSwitcherVisibility(visibility: Int) {
        binding.searchToolbarIconStateSwitcher.visibility = visibility
    }

    /**
     * Set an active view of the IconStateSwitcher.
     *
     * @param index [SearchToolbarIconStateSwitcherIndex] Magnifier or Progressbar.
     */
    fun setIconStateSwitcherIndex(@SearchToolbarIconStateSwitcherIndex index: Int) {
        binding.searchToolbarIconStateSwitcher.displayedChild = index
    }

    /**
     * Set the visibility state of the ClearButton view.
     *
     * @param visibility One of VISIBLE, INVISIBLE, or GONE.
     */
    fun setClearButtonVisibility(visibility: Int) {
        binding.searchToolbarClearButton.visibility = visibility
    }

    /**
     * Register a callback to be invoked when ClearButton view is clicked.
     *
     * @param listener [View.OnClickListener] callback to invoke on ClearButton view click.
     */
    fun setOnClearButtonClickListener(listener: OnClickListener) {
        binding.searchToolbarClearButton.setOnClickListener(listener)
    }
}
