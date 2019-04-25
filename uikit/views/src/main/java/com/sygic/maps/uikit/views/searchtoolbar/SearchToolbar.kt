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
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.sygic.maps.uikit.views.R
import com.sygic.maps.uikit.views.databinding.LayoutSearchToolbarInternalBinding

/**
 * A [SearchToolbar] TODO
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
open class SearchToolbar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.searchToolbarStyle,
    defStyleRes: Int = R.style.SygicSearchToolbarStyle //todo: defStyleRes
) : Toolbar(context, attrs, defStyleAttr) {

    private val binding: LayoutSearchToolbarInternalBinding =
        LayoutSearchToolbarInternalBinding.inflate(LayoutInflater.from(context), this, true)

    fun setText(text: String) {
        binding.inputEditText.setText(text)
        binding.inputEditText.setSelection(binding.inputEditText.text.length)
    }

    fun addTextChangedListener(textWatcher: TextWatcher) {
        binding.inputEditText.addTextChangedListener(textWatcher)
    }

    fun setIconStateSwitcherIndex(@SearchToolbarIconStateSwitcherIndex index: Int) {
        binding.searchToolbarIconStateSwitcher.displayedChild = index
    }

    fun setOnEditorActionListener(listener: TextView.OnEditorActionListener) {
        binding.inputEditText.setOnEditorActionListener(listener)
    }

    fun setOnClearButtonClickListener(listener: OnClickListener) {
        binding.clearButton.setOnClickListener(listener)
    }

    fun clearInputEditTextFocus() {
        binding.inputEditText.clearFocus()
    }
}
