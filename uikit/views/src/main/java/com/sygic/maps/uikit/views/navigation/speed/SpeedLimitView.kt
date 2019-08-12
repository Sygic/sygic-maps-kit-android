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
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ViewSwitcher
import androidx.annotation.CallSuper
import com.sygic.maps.uikit.views.R
import com.sygic.maps.uikit.views.databinding.LayoutSpeedLimitInternalBinding
import com.sygic.maps.uikit.views.navigation.speed.limit.SpeedLimitType

/**
 * A [SpeedLimitView] view is designed to be used as an visual presentation component for the speed limit value. It
 * contains two predefined speed limit styles [SpeedLimitType.EU] and [SpeedLimitType.US]'.
 *
 * The [SpeedLimitView] design can be only little bit changed with the custom _speedLimitViewStyle_ or the
 * _navigationTextColorPrimary_ attribute.
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
open class SpeedLimitView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.speedLimitViewStyle,
    defStyleRes: Int = R.style.SygicSpeedLimitViewStyle
) : ViewSwitcher(context, attrs) {

    private val binding: LayoutSpeedLimitInternalBinding =
        LayoutSpeedLimitInternalBinding.inflate(LayoutInflater.from(context), this, true)

    private var layoutMargin: Int = 0
    private var layoutMarginTop: Int = 0
    private var layoutMarginBottom: Int = 0
    private var layoutMarginStart: Int = 0
    private var layoutMarginEnd: Int = 0

    init {
        isClickable = true

        attrs?.let { attributeSet ->
            context.obtainStyledAttributes(
                attributeSet,
                R.styleable.SpeedLimitView,
                defStyleAttr,
                defStyleRes
            ).apply {
                elevation = getDimensionPixelSize(R.styleable.SpeedLimitView_android_elevation, 0).toFloat()
                layoutMargin = getDimensionPixelSize(R.styleable.SpeedLimitView_android_layout_margin, 0)
                layoutMarginTop = getDimensionPixelSize(R.styleable.SpeedLimitView_android_layout_marginTop, 0)
                layoutMarginBottom = getDimensionPixelSize(R.styleable.SpeedLimitView_android_layout_marginBottom, 0)
                layoutMarginStart = getDimensionPixelSize(R.styleable.SpeedLimitView_android_layout_marginStart, 0)
                layoutMarginEnd = getDimensionPixelSize(R.styleable.SpeedLimitView_android_layout_marginEnd, 0)

                recycle()
            }
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
     * Sets the speed limit type.
     *
     * @param speedLimitType [SpeedLimitType] the speed limit type.
     */
    fun setLimitType(speedLimitType: SpeedLimitType) {
        displayedChild = speedLimitType.ordinal
    }

    /**
     * Sets the speed limit value.
     *
     * @param speedLimitValue [Int] the speed limit value.
     */
    fun setLimitValue(speedLimitValue: Int) {
        with(speedLimitValue.toString()) {
            binding.euSpeedLimitTextView.text = this
            binding.usSpeedLimitTextView.text = this
        }
    }
}