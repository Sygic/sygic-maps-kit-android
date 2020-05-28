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

package com.sygic.maps.uikit.views.common.extensions

import android.text.Editable
import android.text.TextWatcher
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.TextView
import com.sygic.maps.uikit.views.common.ArrayIndicesAdapter
import com.sygic.maps.uikit.views.common.utils.TextHolder

fun TextView.text(textHolder: TextHolder) {
    text = textHolder.getText(context)
}

var AutoCompleteTextView.selectedPosition: Int
    get() = (adapter as ArrayIndicesAdapter<*>).selected
    set(value) {
        (adapter as ArrayIndicesAdapter<*>).selected = value
        setText(selectedValue, false)
    }

val AutoCompleteTextView.selectedValue: String?
    get() = adapter.getItem(selectedPosition).toString()

var AutoCompleteTextView.selectedIndex: Int
    get() = (adapter as ArrayIndicesAdapter<*>).getIndexForPosition(selectedPosition)
    set(value) {
        selectedPosition = (adapter as ArrayIndicesAdapter<*>).findPositionFromIndex(value)
    }

inline fun EditText.doAfterTextChanged(crossinline action: (Editable?) -> Unit) {
    addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            action(s)
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    })
}