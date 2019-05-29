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
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.sygic.maps.uikit.views.R
import com.sygic.maps.uikit.views.common.extensions.showKeyboard
import com.sygic.maps.uikit.views.databinding.LayoutSearchToolbarInternalBinding

/**
 * A [SearchToolbar] can be used as input component to the search screen. It contains [EditText] input field, state
 * switcher (MAGNIFIER or PROGRESSBAR) and clear [Button].
 *
 * TODO MS-5681
 *
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
open class SearchToolbar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.searchToolbarStyle,
    defStyleRes: Int = R.style.SygicSearchToolbarStyle // TODO: MS-5681
) : Toolbar(context, attrs, defStyleAttr) {

    private val binding: LayoutSearchToolbarInternalBinding =
        LayoutSearchToolbarInternalBinding.inflate(LayoutInflater.from(context), this, true)

    private val inputEditTextOnFocusChangedListener = OnFocusChangeListener { view, hasFocus ->
        if (hasFocus) (view as EditText).showKeyboard()
    }

    init {
        descendantFocusability = FOCUS_AFTER_DESCENDANTS
        binding.inputEditText.onFocusChangeListener = inputEditTextOnFocusChangedListener
    }

    override fun onRequestFocusInDescendants(direction: Int, previouslyFocusedRect: Rect?): Boolean =
        binding.inputEditText.requestFocus()

    /**
     * Set the text to be displayed in the input [EditText].
     *
     * @param text text to be displayed.
     */
    fun setText(text: String) {
        binding.inputEditText.text?.let { editable ->
            if (editable.toString() != text) setTextInternal(text)
        } ?: run {
            setTextInternal(text)
        }
    }

    private fun setTextInternal(text: String) {
        binding.inputEditText.setText(text)
        binding.inputEditText.setSelection(text.length)
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
     * Adds a TextWatcher to the list of those whose methods are called
     * whenever the InputEditText's text changes.
     *
     * @param textWatcher watcher to be used.
     */
    fun addTextChangedListener(textWatcher: TextWatcher) {
        binding.inputEditText.addTextChangedListener(textWatcher)
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
        binding.inputEditText.setOnEditorActionListener(listener)
    }

    /**
     * Called when this view wants to give up focus. If focus is cleared
     * [View.OnFocusChangeListener] is called.
     *
     * Note: When not in touch-mode, the framework will try to give focus
     * to the first focusable View from the top after focus is cleared. Hence, if this
     * View is the first from the top that can take focus, then all callbacks
     * related to clearing focus will be invoked after which the framework will
     * give focus to this view.
     */
    fun clearInputEditTextFocus() {
        binding.inputEditText.clearFocus()
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
        binding.clearButton.visibility = visibility
    }

    /**
     * Register a callback to be invoked when ClearButton view is clicked.
     *
     * @param listener [View.OnClickListener] callback to invoke on ClearButton view click.
     */
    fun setOnClearButtonClickListener(listener: OnClickListener) {
        binding.clearButton.setOnClickListener(listener)
    }
}
