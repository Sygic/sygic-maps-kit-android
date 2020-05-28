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
import android.view.View
import android.widget.AdapterView
import android.widget.AutoCompleteTextView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import androidx.recyclerview.widget.RecyclerView
import com.sygic.maps.uikit.views.R
import com.sygic.maps.uikit.views.common.ArrayIndicesAdapter
import com.sygic.maps.uikit.views.common.extensions.disable
import com.sygic.maps.uikit.views.common.extensions.enable
import com.sygic.maps.uikit.views.common.extensions.selectedIndex
import com.sygic.maps.uikit.views.common.extensions.selectedPosition
import com.sygic.maps.uikit.views.common.utils.TextHolder

@BindingAdapter(value = ["text"])
fun setTextHolder(textView: TextView, textHolder: TextHolder?) {
    textHolder?.let {
        textView.text = it.getText(textView.context)
        return
    }

    textView.text = null
}

@BindingAdapter("adapter")
fun <T : RecyclerView.ViewHolder> setAdapter(view: RecyclerView, adapter: RecyclerView.Adapter<T>) {
    view.adapter = adapter
}

@BindingAdapter("adapter")
fun setAutoCompleteTextViewAdapter(view: AutoCompleteTextView, wantsAdapter: Boolean) {
    if (wantsAdapter) {
        val adapter = ArrayIndicesAdapter<String>(view.context, R.layout.dropdown_menu_popup_item)
        view.setAdapter(adapter)
    }
}

@Suppress("UNCHECKED_CAST")
@BindingAdapter("adapterSource")
fun setAdapterSource(view: AutoCompleteTextView, source: Array<String>) {
    (view.adapter as ArrayIndicesAdapter<String>).clear()
    (view.adapter as ArrayIndicesAdapter<String>).addAll(source.toList())
}

@BindingAdapter("values", "indices", requireAll = false)
fun setArrayAdapter(view: AutoCompleteTextView, array: Array<String>, indices: IntArray?) {
    val adapter = ArrayIndicesAdapter(
        view.context, R.layout.dropdown_menu_popup_item, array.toList(), indices
    )
    view.setAdapter(adapter)
    if (!adapter.isEmpty) {
        view.selectedPosition = 0
    }
}

@BindingAdapter("disableInput")
fun setDisableInput(view: AutoCompleteTextView, disable: Boolean) {
    if (disable) {
        view.inputType = InputType.TYPE_NULL
    }
}

@BindingAdapter("selected")
fun setSelected(view: AutoCompleteTextView, selected: Int) {
    if (view.selectedIndex != selected) {
        view.selectedIndex = selected
    }
}

@InverseBindingAdapter(attribute = "selected")
fun getSelected(view: AutoCompleteTextView) = view.selectedIndex

@BindingAdapter("app:selectedAttrChanged")
fun setListeners(view: AutoCompleteTextView, attrChange: InverseBindingListener) {
    view.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
        if (view.selectedPosition != position) {
            view.selectedPosition = position
            attrChange.onChange()
        }
    }
}

@BindingAdapter("enable")
fun setViewEnable(view: View, enabled: Boolean) {
    if (enabled) {
        view.enable()
    } else {
        view.disable()
    }
}