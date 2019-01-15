package com.sygic.ui.common.extensions

import android.util.TypedValue
import android.view.View
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes

fun View.applyStyle(@StyleRes resId: Int, force: Boolean = false) {
    context.theme.applyStyle(resId, force)
}

fun View.getDataFromAttr(@AttrRes resId: Int, typedValue: TypedValue = TypedValue(), resolveRefs: Boolean = true): Int {
    typedValue.let {
        context.theme.resolveAttribute(resId, it, resolveRefs)
        return it.data
    }
}