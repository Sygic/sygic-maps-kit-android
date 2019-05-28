/*
 * Copyright (c) 2019 Sygic a.s. All rights reserved.
 * This project is licensed under the MIT License.
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.sygic.maps.uikit.views.searchtoolbar

import android.text.Editable
import android.text.TextWatcher
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import androidx.databinding.adapters.ListenerUtil
import androidx.databinding.adapters.TextViewBindingAdapter
import kotlinx.android.synthetic.main.layout_search_toolbar_internal.view.*


@BindingAdapter(
    value = ["android:beforeTextChanged", "android:onTextChanged",
        "android:afterTextChanged", "android:textAttrChanged"], requireAll = false
)
fun setTextWatcher(
    toolbar: SearchToolbar, before: TextViewBindingAdapter.BeforeTextChanged?,
    on: TextViewBindingAdapter.OnTextChanged?, after: TextViewBindingAdapter.AfterTextChanged?,
    textAttrChanged: InverseBindingListener?
) {
    val newValue = if (before == null && after == null && on == null && textAttrChanged == null) {
        null
    } else {
        object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                after?.afterTextChanged(s)
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                before?.beforeTextChanged(s, start, count, after)
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                on?.onTextChanged(s, start, before, count)
                textAttrChanged?.onChange()
            }
        }
    }

    val editText = toolbar.inputEditText
    ListenerUtil.trackListener(editText, newValue, androidx.databinding.library.baseAdapters.R.id.textWatcher)?.let {
        editText.removeTextChangedListener(it)
    }

    newValue?.let {
        editText.addTextChangedListener(it)
    }
}

@BindingAdapter("android:text")
fun setSearchText(toolbar: SearchToolbar, text: CharSequence?) {
    with(toolbar.inputEditText) {
        getText().let {
            if (it.toString() == text.toString() || (text == null && it.isEmpty())) {
                return
            }
        }

        setText(text)
    }
}

@InverseBindingAdapter(attribute = "android:text", event = "android:textAttrChanged")
fun getSearchText(toolbar: SearchToolbar): CharSequence = toolbar.getText()
