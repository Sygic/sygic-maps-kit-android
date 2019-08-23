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

package com.sygic.maps.uikit.views.navigation.speed

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.CallSuper
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import com.sygic.maps.uikit.views.R
import com.sygic.maps.uikit.views.common.extensions.getColorFromAttr
import com.sygic.maps.uikit.views.databinding.LayoutCurrentSpeedInternalBinding

/**
 * A [CurrentSpeedView] view is designed to be used as an visual presentation component for the actual speed value. It
 * contains two [TextView]'s for speed value and unit.
 *
 * The [CurrentSpeedView] design can be completely changed with the custom _currentSpeedViewStyle_ or the standard android
 * attributes as _background_, _navigationTextColorPrimary_ or _navigationTextColorSecondary_ can be used.
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
open class CurrentSpeedView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.currentSpeedViewStyle,
    defStyleRes: Int = R.style.SygicCurrentSpeedViewStyle
) : LinearLayout(context, attrs, defStyleAttr, defStyleRes) {

    private val binding: LayoutCurrentSpeedInternalBinding =
        LayoutCurrentSpeedInternalBinding.inflate(LayoutInflater.from(context), this, true)

    private var layoutMargin: Int = 0
    private var layoutMarginTop: Int = 0
    private var layoutMarginBottom: Int = 0
    private var layoutMarginStart: Int = 0
    private var layoutMarginEnd: Int = 0

    @ColorInt
    private val whiteColor = ContextCompat.getColor(context, R.color.white)
    @ColorInt
    private val redColor = ContextCompat.getColor(context, R.color.brick_red)

    init {
        isClickable = true
        orientation = VERTICAL
        gravity = Gravity.CENTER

        attrs?.let { attributeSet ->
            @Suppress("Recycle")
            context.obtainStyledAttributes(
                attributeSet,
                R.styleable.CurrentSpeedView,
                defStyleAttr,
                defStyleRes
            ).also {
                layoutMargin = it.getDimensionPixelSize(R.styleable.CurrentSpeedView_android_layout_margin, 0)
                layoutMarginTop = it.getDimensionPixelSize(R.styleable.CurrentSpeedView_android_layout_marginTop, 0)
                layoutMarginBottom = it.getDimensionPixelSize(R.styleable.CurrentSpeedView_android_layout_marginBottom, 0)
                layoutMarginStart = it.getDimensionPixelSize(R.styleable.CurrentSpeedView_android_layout_marginStart, 0)
                layoutMarginEnd = it.getDimensionPixelSize(R.styleable.CurrentSpeedView_android_layout_marginEnd, 0)
            }.recycle()
        }
    }

    @CallSuper
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        with((layoutParams as MarginLayoutParams)) {
            if (layoutMargin != 0) {
                setMargins(layoutMargin, layoutMargin, layoutMargin, layoutMargin)
            } else {
                setMargins(layoutMarginStart, layoutMarginTop, layoutMarginEnd, layoutMarginBottom)
            }
        }
    }

    /**
     * Sets the current speed value.
     *
     * @param currentSpeedValue [Int] the current speed value.
     */
    fun setSpeedValue(currentSpeedValue: Int) {
        binding.currentSpeedValueTextView.text = (if (currentSpeedValue > 0) currentSpeedValue else 0).toString()
    }

    /**
     * Sets the current speed unit.
     *
     * @param currentSpeedUnit [String] the current speed unit.
     */
    fun setSpeedUnit(currentSpeedUnit: String) {
        binding.currentSpeedUnitTextView.text = currentSpeedUnit
    }

    /**
     * Sets the actual speeding state (if the current speed is higher than the current speed limit value or not).
     *
     * @param isSpeeding [Boolean] true to sets if currently speeding, false otherwise.
     */
    fun setIsSpeeding(isSpeeding: Boolean) {
        binding.currentSpeedValueTextView.setTextColor(if (isSpeeding) whiteColor else getColorFromAttr(R.attr.navigationTextColorPrimary))
        binding.currentSpeedUnitTextView.setTextColor(if (isSpeeding) whiteColor else getColorFromAttr(R.attr.navigationTextColorPrimary))
        backgroundTintList = ColorStateList.valueOf(if (isSpeeding) redColor else getColorFromAttr(R.attr.navigationBackgroundColor))
    }
}