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

package com.sygic.maps.uikit.views.common.extensions

import android.graphics.drawable.Drawable
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources

fun View.showKeyboard() = context?.showKeyboard(this)
fun View.hideKeyboard() = context?.hideKeyboard(this)

fun View.visible(visible: Boolean, invisibleValue: Int = View.GONE) {
    visibility = if (visible) View.VISIBLE else invisibleValue
}

@ColorInt
fun View.getColor(@ColorRes colorResId: Int): Int = context.getColorInt(colorResId)

fun View.getColorFromAttr(@AttrRes resId: Int, typedValue: TypedValue = TypedValue(), resolveRefs: Boolean = true) =
    context.getColorFromAttr(resId, typedValue, resolveRefs)

fun View.getDrawable(@DrawableRes drawableResId: Int): Drawable? = context.getDrawable(drawableResId)

fun ImageView.tint(@ColorRes color: Int) {
    if (color != 0) imageTintList = AppCompatResources.getColorStateList(context, color)
}

fun ImageView.backgroundTint(@ColorRes color: Int) {
    if (color != 0) backgroundTintList = AppCompatResources.getColorStateList(context, color)
}

fun View.enable() {
    alpha = 1f
    isClickable = true
}

fun View.disable() {
    alpha = .5f
    isClickable = false
}