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

package com.sygic.maps.uikit.views.navigation.signpost

import android.animation.LayoutTransition
import android.content.Context
import android.util.AttributeSet
import androidx.annotation.CallSuper
import androidx.constraintlayout.widget.ConstraintLayout
import com.sygic.maps.uikit.views.R

@Suppress("CustomViewStyleable")
abstract class BaseSignpostView(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int,
    defStyleRes: Int
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private var layoutMargin: Int = 0
    private var layoutMarginTop: Int = 0
    private var layoutMarginBottom: Int = 0
    private var layoutMarginStart: Int = 0
    private var layoutMarginEnd: Int = 0

    init {
        isClickable = true
        layoutTransition = LayoutTransition()

        attrs?.let { attributeSet ->
            context.obtainStyledAttributes(
                attributeSet,
                R.styleable.SignpostView,
                defStyleAttr,
                defStyleRes
            ).apply {
                setBackgroundResource(getResourceId(R.styleable.SignpostView_android_background,0))

                layoutMargin = getDimensionPixelSize(R.styleable.SignpostView_android_layout_margin, 0)
                layoutMarginTop = getDimensionPixelSize(R.styleable.SignpostView_android_layout_marginTop, 0)
                layoutMarginBottom = getDimensionPixelSize(R.styleable.SignpostView_android_layout_marginBottom, 0)
                layoutMarginStart = getDimensionPixelSize(R.styleable.SignpostView_android_layout_marginStart, 0)
                layoutMarginEnd = getDimensionPixelSize(R.styleable.SignpostView_android_layout_marginEnd, 0)

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
}
