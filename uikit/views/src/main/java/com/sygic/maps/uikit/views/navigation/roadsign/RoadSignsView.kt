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

package com.sygic.maps.uikit.views.navigation.roadsign

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatTextView
import com.sygic.maps.uikit.views.R
import com.sygic.maps.uikit.views.common.extensions.getColor
import com.sygic.maps.uikit.views.common.extensions.getDrawable
import com.sygic.maps.uikit.views.navigation.roadsign.data.RoadSignData

/**
 * A [RoadSignsView] is an container layout for the list of [RoadSignData].
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
open class RoadSignsView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : LinearLayout(context, attrs, defStyleAttr, defStyleRes) {

    private val textSize: Float
    private val textPadding: Int

    var data: List<RoadSignData> = emptyList()
        set(value) {
            if (field != value) {
                field = value
                removeAllViews()
                value.map { createRoadSignChildView(it) }.forEach { addView(it) }
            }
        }

    init {
        orientation = HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL
        showDividers = SHOW_DIVIDER_MIDDLE
        dividerDrawable = getDrawable(R.drawable.shape_empty_roadsigns_divider)

        textSize = resources.getDimension(R.dimen.roadSignTextSize)
        textPadding = resources.getDimensionPixelOffset(R.dimen.roadSignTextPadding)

        if (isInEditMode) {
            data = listOf(
                RoadSignData(R.drawable.ic_roadsign_rect_green, "E58"),
                RoadSignData(R.drawable.ic_roadsign_rect_red, "D1")
            )
        }
    }

    private fun createRoadSignChildView(data: RoadSignData) = AppCompatTextView(context).apply {
        text = data.text
        gravity = Gravity.CENTER
        setBackgroundResource(data.background)
        setPadding(textPadding, textPadding, textPadding, textPadding)
        setTextColor(getColor(data.textColor))
        setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)
        setTypeface(typeface, Typeface.BOLD)
    }
}
