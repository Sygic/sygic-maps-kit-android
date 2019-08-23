/*
 * Copyright (c) 2019 Sygic a.s. All rights reserved.
 * This project is licensed under the MIT License.
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.sygic.maps.uikit.views.navigation.lanes

import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.LayerDrawable
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.core.util.Pools
import com.sygic.maps.uikit.views.R
import com.sygic.maps.uikit.views.common.extensions.dpToPixels
import com.sygic.maps.uikit.views.common.extensions.getColorFromAttr
import com.sygic.maps.uikit.views.navigation.lanes.data.SimpleLanesData

private const val POOL_SIZE = 10

class SimpleLanesView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.lanesViewStyle,
    defStyleRes: Int = R.style.SygicLanesViewStyle
) : LinearLayout(context, attrs, defStyleAttr, defStyleRes) {

    var lanes: Array<SimpleLanesData> = emptyArray()
        set(value) {
            field = value
            setLanesInternal(value)
        }

    private var layoutMargin: Int = 0
    private var layoutMarginTop: Int = 0
    private var layoutMarginBottom: Int = 0
    private var layoutMarginStart: Int = 0
    private var layoutMarginEnd: Int = 0

    @ColorInt
    private val highlightedColor = context.getColorFromAttr(R.attr.navigationTextColorTertiary)
    private val laneViewSize = resources.dpToPixels(24f).toInt()

    private val viewPool: Pools.SimplePool<ImageView> = Pools.SimplePool(POOL_SIZE)

    init {
        orientation = HORIZONTAL
        gravity = Gravity.CENTER_HORIZONTAL
        val padding = resources.dpToPixels(8f).toInt()
        setPadding(padding, padding, padding, padding)

        attrs?.let { attributeSet ->
            @Suppress("Recycle")
            context.obtainStyledAttributes(
                attributeSet,
                R.styleable.SimpleLanesView,
                defStyleAttr,
                defStyleRes
            ).also {
                layoutMargin =
                    it.getDimensionPixelSize(R.styleable.SimpleLanesView_android_layout_margin, 0)
                layoutMarginTop = it.getDimensionPixelSize(
                    R.styleable.SimpleLanesView_android_layout_marginTop,
                    0
                )
                layoutMarginBottom = it.getDimensionPixelSize(
                    R.styleable.SimpleLanesView_android_layout_marginBottom,
                    0
                )
                layoutMarginStart = it.getDimensionPixelSize(
                    R.styleable.SimpleLanesView_android_layout_marginStart,
                    0
                )
                layoutMarginEnd = it.getDimensionPixelSize(
                    R.styleable.SimpleLanesView_android_layout_marginEnd,
                    0
                )
            }.recycle()
        }
    }

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

    private fun setLanesInternal(lanes: Array<SimpleLanesData>) {
        val lanesCount = lanes.run {
            reverse()
            size
        }

        if (lanesCount < childCount) {
            for (i in childCount - 1 downTo lanesCount) {
                viewPool.release(getChildAt(i))
                removeViewAt(i)
            }
        } else if (lanesCount > childCount) {
            for (i in childCount until lanesCount) {
                addView(viewPool.acquire() ?: createLaneView())
            }
        }

        lanes.forEachIndexed { i, lane ->
            val drawables = lane.directions.map {
                ContextCompat.getDrawable(context, it)
            }.toTypedArray()

            getChildAt(i).setImageDrawable(LayerDrawable(drawables).apply {
                if (lane.highlighted) {
                    setTintMode(PorterDuff.Mode.SRC_ATOP)
                    setTint(highlightedColor)
                }
            })
        }
    }

    private fun createLaneView() = ImageView(context).apply {
        setBackgroundColor(Color.TRANSPARENT)
        layoutParams = LayoutParams(laneViewSize, laneViewSize)
        scaleType = ImageView.ScaleType.FIT_CENTER
    }

    override fun getChildAt(index: Int): ImageView {
        return super.getChildAt(index) as ImageView
    }

    override fun addView(child: View, index: Int, params: ViewGroup.LayoutParams) {
        if (child is ImageView) {
            super.addView(child, index, params)
        } else {
            throw UnsupportedOperationException("You can't add views to ${this::class.java.name}")
        }
    }
}