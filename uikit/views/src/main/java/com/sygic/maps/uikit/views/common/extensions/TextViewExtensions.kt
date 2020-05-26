package com.sygic.maps.uikit.views.common.extensions

import android.text.Editable
import android.text.TextWatcher
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.TextView
import com.google.android.material.textfield.TextInputLayout
import com.sygic.maps.uikit.views.common.ArrayIndicesAdapter
import com.sygic.maps.uikit.views.common.utils.TextHolder

fun TextView.text(textHolder: TextHolder) {
    text = textHolder.getText(context)
}

val TextInputLayout.dropDownTextView: AutoCompleteTextView
    get() = (getChildAt(0) as FrameLayout).getChildAt(0) as AutoCompleteTextView

var AutoCompleteTextView.selectedPosition: Int
    get() = tag as Int
    set(value) {
        tag = value
        setText(selectedValue, false)
    }

val AutoCompleteTextView.selectedValue: String?
    get() = adapter.getItem(selectedPosition).toString()

var AutoCompleteTextView.selectedIndex: Int
    get() = (adapter as ArrayIndicesAdapter<*>).getIndexForPosition(selectedPosition)
    set(value) {
        selectedPosition = (adapter as ArrayIndicesAdapter<*>).findPositionFromIndex(value)
    }

fun AutoCompleteTextView.findIndexForName(name: String): Int {
    for (i in 0 until adapter.count) {
        val item = adapter.getItem(i)
        if (item == name) {
            return i
        }
    }
    return -1
}

fun AutoCompleteTextView.getItem(position: Int) = adapter.getItem(position).toString()

inline fun EditText.doAfterTextChanged(crossinline action: (Editable?) -> Unit) {
    addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            action(s)
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    })
}