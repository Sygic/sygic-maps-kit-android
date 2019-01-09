package com.sygic.ui.common.extensions

import android.content.res.TypedArray
import android.graphics.drawable.Drawable
import android.graphics.drawable.TransitionDrawable
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.StyleRes

fun View.applyStyle(@StyleRes resId: Int, force: Boolean = false) {
    context.theme.applyStyle(resId, force)
}

@ColorInt
fun View.getColorFromAttr(@AttrRes resId: Int, typedValue: TypedValue = TypedValue(), resolveRefs: Boolean = true): Int {
    typedValue.let {
        context.theme.resolveAttribute(resId, it, resolveRefs)
        return it.data
    }
}

//todo
@ColorInt
fun View.getColorFromAttr(typedArray: TypedArray, styleable: Int, @AttrRes resId: Int, colorValue: TypedValue = TypedValue()): Int {
    typedArray.getValue(styleable, colorValue)

    return colorValue.data
    /*return if (colorValue.type == TypedValue.TYPE_REFERENCE) {
        ContextCompat.getDrawable(context, resId)
    } else {
        // It must be a single color
        ColorDrawable(colorValue.data)
    }

    typedValue.let {
        context.theme.resolveAttribute(resId, it, resolveRefs)
        return it.data
    }*/
}

fun ImageView.setImageDrawableWithFade(targetDrawable: Drawable, duration: Int) {
    val currentDrawable = this.drawable
    if (currentDrawable == null) {
        this.setImageDrawable(targetDrawable)
        return
    }

    val layers = arrayOf(currentDrawable, targetDrawable)
    val transition = TransitionDrawable(layers)
    transition.isCrossFadeEnabled = true
    this.setImageDrawable(transition)
    transition.startTransition(duration)
}