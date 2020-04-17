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

package com.sygic.maps.uikit.views.common.binding.adapters

import android.text.InputType
import android.widget.AdapterView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.google.android.material.textfield.TextInputLayout
import com.sygic.maps.uikit.views.common.ArrayIndicesAdapter
import com.sygic.maps.uikit.views.R
import com.sygic.maps.uikit.views.common.extensions.dropDownTextView
import com.sygic.maps.uikit.views.common.utils.TextHolder

@BindingAdapter(value = ["text"])
fun setTextHolder(textView: TextView, textHolder: TextHolder?) {
    textHolder?.let {
        textView.text = it.getText(textView.context)
        return
    }

    textView.text = null
}

@BindingAdapter("values", "indices", requireAll = false)
fun setArrayAdapter(view: TextInputLayout, array: Array<String>, indices: IntArray?) {
    val adapter = ArrayIndicesAdapter(
        view.context, R.layout.dropdown_menu_popup_item, array, indices
    )
    view.dropDownTextView.setAdapter(adapter)
    if (!adapter.isEmpty) {
        view.dropDownTextView.tag = 0
        view.dropDownTextView.setText(adapter.getItem(0), false)
    }
}

@BindingAdapter("disableInput")
fun setDisableInput(view: TextInputLayout, disable: Boolean) {
    if (disable) {
        view.dropDownTextView.inputType = InputType.TYPE_NULL
    }
}

interface OnPositionSelectedListener {
    fun onPositionSelected(position: Int)
}

@BindingAdapter("onSelected")
fun setDropDownItemSelectedListener(view: TextInputLayout, listener: OnPositionSelectedListener?) {
    view.dropDownTextView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
        view.dropDownTextView.tag = position
        listener?.onPositionSelected(position)
    }
}