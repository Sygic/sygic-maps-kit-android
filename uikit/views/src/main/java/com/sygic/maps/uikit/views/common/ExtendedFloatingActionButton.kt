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

package com.sygic.maps.uikit.views.common

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.annotation.CallSuper
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.sygic.maps.uikit.views.R
import com.sygic.maps.uikit.views.common.extensions.getColor
import com.sygic.maps.uikit.views.common.extensions.tint
import com.sygic.maps.uikit.views.databinding.LayoutExtendedFabInternalBinding

@Suppress("unused", "MemberVisibilityCanBePrivate")
open class ExtendedFloatingActionButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.extendedFloatingActionButtonStyle,
    defStyleRes: Int = R.style.SygicExtendedFloatingActionButtonStyle
) : LinearLayout(context, attrs, defStyleAttr, defStyleRes) {

    private val binding = LayoutExtendedFabInternalBinding.inflate(LayoutInflater.from(context), this, true)

    private var layoutMargin: Int = 0
    private var layoutMarginTop: Int = 0
    private var layoutMarginBottom: Int = 0
    private var layoutMarginStart: Int = 0
    private var layoutMarginEnd: Int = 0

    init {
        orientation = HORIZONTAL
        gravity = Gravity.CENTER

        attrs?.let { attributeSet ->
            @Suppress("Recycle")
            context.obtainStyledAttributes(
                attributeSet,
                R.styleable.ExtendedFloatingActionButton,
                defStyleAttr,
                defStyleRes
            ).also {
                setBackgroundResource(it.getResourceId(R.styleable.ExtendedFloatingActionButton_android_background,0))

                layoutMargin = it.getDimensionPixelSize(R.styleable.ExtendedFloatingActionButton_android_layout_margin, -1)
                layoutMarginTop = it.getDimensionPixelSize(R.styleable.ExtendedFloatingActionButton_android_layout_marginTop, 0)
                layoutMarginBottom = it.getDimensionPixelSize(R.styleable.ExtendedFloatingActionButton_android_layout_marginBottom, 0)
                layoutMarginStart = it.getDimensionPixelSize(R.styleable.ExtendedFloatingActionButton_android_layout_marginStart, 0)
                layoutMarginEnd = it.getDimensionPixelSize(R.styleable.ExtendedFloatingActionButton_android_layout_marginEnd, 0)
                setText(it.getResourceId(R.styleable.ExtendedFloatingActionButton_android_text, 0))
                setTextColor(it.getResourceId(R.styleable.ExtendedFloatingActionButton_android_textColor, 0))
                setTextSize(it.getDimensionPixelSize(R.styleable.ExtendedFloatingActionButton_android_textSize, 0))
                setIcon(it.getResourceId(R.styleable.ExtendedFloatingActionButton_android_icon, 0))
                setIconTint(it.getResourceId(R.styleable.ExtendedFloatingActionButton_android_iconTint, 0))
            }.recycle()
        }
    }

    @CallSuper
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        with((layoutParams as MarginLayoutParams)) {
            if (layoutMargin >= 0) {
                setMargins(layoutMargin, layoutMargin, layoutMargin, layoutMargin)
            } else {
                setMargins(layoutMarginStart, layoutMarginTop, layoutMarginEnd, layoutMarginBottom)
            }
        }
    }

    fun setText(@StringRes textId: Int) {
        if (textId != 0) {
            binding.extendedFabTitle.setText(textId)
        }
    }

    fun setTextColor(@ColorRes colorId: Int) {
        if (colorId != 0) {
            binding.extendedFabTitle.setTextColor(getColor(colorId))
        }
    }

    fun setTextSize(sizeValue: Int) = setTextSize(sizeValue.toFloat())

    fun setTextSize(sizeValue: Float) {
        if (sizeValue != 0F) {
            binding.extendedFabTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, sizeValue)
        }
    }

    fun setIcon(@DrawableRes iconId: Int) {
        if (iconId != 0) {
            binding.extendedFabIcon.setImageResource(iconId)
        }
    }

    fun setIconTint(@ColorRes colorId: Int) {
        if (colorId != 0) {
            binding.extendedFabIcon.tint(colorId)
        }
    }
}