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

package com.sygic.maps.uikit.views.placedetail.binding

import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.databinding.BindingAdapter
import com.sygic.maps.uikit.views.R
import com.sygic.maps.uikit.views.common.extensions.getColorFromAttr

@BindingAdapter(value = ["placeDetailItemState"])
fun setPlaceDetailItemState(textView: TextView, enabled: Boolean) {
    with(textView) {
        setBackgroundResource(if (enabled) R.drawable.bg_place_detail_item_horizontal_rounded else R.drawable.bg_place_detail_item_horizontal_disabled_rounded)
        getColorFromAttr(if (enabled) android.R.attr.colorAccent else R.attr.placeDetailItemDisabledTintColor).let {
            setTextColor(it)
            setDrawableColorTint(it)
        }
    }
}

private fun TextView.setDrawableColorTint(@ColorInt color: Int) {
    for (drawable in compoundDrawables) drawable?.setTint(color)
}