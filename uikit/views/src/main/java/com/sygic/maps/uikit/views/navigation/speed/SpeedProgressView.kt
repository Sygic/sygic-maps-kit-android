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
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import com.sygic.maps.uikit.views.R
import kotlin.math.min

private const val angleShift = 90f
private const val angleMax = 270f
private const val angleStartPoint = 45f
private const val angleEndPoint = angleStartPoint + angleMax
private const val maxProgress = 100f

@Suppress("unused", "MemberVisibilityCanBePrivate")
class SpeedProgressView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.speedProgressViewStyle,
    defStyleRes: Int = R.style.SygicSpeedProgressViewStyle
) : View(context, attrs, defStyleAttr, defStyleRes) {

    private val oval = RectF()
    private val foregroundPath = Path()
    private val backgroundPath = Path()
    private val foregroundPaint = SegmentPaint()
    private val backgroundPaint = SegmentPaint()

    var progress = 0f
        set(value) {
            field = if (value < 0f) 0f else if (value > maxProgress) maxProgress else value
            invalidate()
        }

    var roundSegmentEdges = false
        set(value) {
            field = value
            foregroundPaint.strokeCap = if (value) Paint.Cap.ROUND else Paint.Cap.BUTT
            backgroundPaint.strokeCap = if (value) Paint.Cap.ROUND else Paint.Cap.BUTT
            invalidate()
        }

    private var segmentForegroundGradient: Shader? = null

    @ColorInt
    var segmentForegroundColors = intArrayOf()
        set(value) {
            field = value
            segmentForegroundGradient = if (value.size >= 2) createForegroundGradient(value) else null
            invalidate()
        }

    @ColorInt
    var segmentBackgroundColor = ContextCompat.getColor(context, R.color.speedProgressBackgroundColor)
        set(value) {
            field = value
            backgroundPaint.color = value
            invalidate()
        }

    var segmentAngle = resources.getInteger(R.integer.speedProgressViewSegmentAngle).toFloat()
        set(value) {
            field = value
            invalidate()
        }

    var segmentThickness = resources.getDimensionPixelSize(R.dimen.speedProgressViewSegmentThickness)
        set(value) {
            field = value
            foregroundPaint.strokeWidth = value.toFloat()
            backgroundPaint.strokeWidth = value.toFloat()
            invalidate()
        }

    var segmentSpacing = resources.getFraction(R.fraction.speedProgressViewSegmentSpacing, 1, 1)
        set(value) {
            field = value
            invalidate()
        }

    init {
        attrs?.let { attributeSet ->
            @Suppress("Recycle")
            context.obtainStyledAttributes(
                attributeSet,
                R.styleable.SpeedProgressView,
                defStyleAttr,
                defStyleRes
            ).also {
                roundSegmentEdges = it.getBoolean(
                    R.styleable.SpeedProgressView_roundSegmentEdges, roundSegmentEdges
                )
                segmentBackgroundColor = it.getInt(
                    R.styleable.SpeedProgressView_segmentBackgroundColor, segmentBackgroundColor
                )
                segmentForegroundColors = resources.getIntArray(
                    it.getResourceId(
                        R.styleable.SpeedProgressView_segmentForegroundColors, R.array.speedProgressForegroundColors
                    )
                )
                segmentAngle = it.getInt(
                    R.styleable.SpeedProgressView_segmentAngle, segmentAngle.toInt()
                ).toFloat()
                segmentThickness = it.getDimensionPixelSize(
                    R.styleable.SpeedProgressView_segmentThickness, segmentThickness
                )
                segmentSpacing = it.getFraction(
                    R.styleable.SpeedProgressView_segmentSpacing, 1, 1, segmentSpacing
                )

                it.recycle()
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val min = min(
            getDefaultSize(suggestedMinimumWidth, widthMeasureSpec),
            getDefaultSize(suggestedMinimumHeight, heightMeasureSpec)
        )
        setMeasuredDimension(min, min)

        segmentForegroundGradient = createForegroundGradient(segmentForegroundColors)

        val left = segmentThickness.toFloat() / 2
        val top = segmentThickness.toFloat() / 2
        val right = min - segmentThickness.toFloat() / 2
        val bottom = min - segmentThickness.toFloat() / 2
        oval.set(left, top, right, bottom)
    }

    override fun onDraw(canvas: Canvas) {
        canvas.rotate(angleShift, width.toFloat() / 2, height.toFloat() / 2)

        val stepAngle = segmentAngle * segmentSpacing
        val stepWithSpacing = segmentAngle + stepAngle
        val rest = angleMax.rem(stepWithSpacing)
        val startEndGap = rest / 2
        val drawStartPoint = angleStartPoint + startEndGap
        val drawTolerance = 0.1f

        canvas.drawPath(backgroundPath.apply {
            reset()
            val drawEndPoint = angleEndPoint - startEndGap - stepWithSpacing
            addArcs(oval, drawStartPoint, drawEndPoint, drawTolerance, stepAngle, stepWithSpacing, segmentAngle)
        }, backgroundPaint)

        if (segmentForegroundColors.isNotEmpty()) {
            if (segmentForegroundColors.size >= 2) {
                foregroundPaint.apply {
                    shader = segmentForegroundGradient
                    isAntiAlias = true
                }
            } else {
                foregroundPaint.color = segmentForegroundColors.first()
            }

            canvas.drawPath(foregroundPath.apply {
                reset()
                val angle = angleMax * progress / maxProgress + angleStartPoint
                var drawEndPoint = angle - startEndGap
                if (progress == maxProgress) drawEndPoint -= stepWithSpacing
                addArcs(oval, drawStartPoint, drawEndPoint, drawTolerance, stepAngle, stepWithSpacing, segmentAngle)
            }, foregroundPaint)
        }
    }

    private fun createForegroundGradient(colors: IntArray) = SweepGradient(
        measuredWidth.toFloat() / 2,
        measuredHeight.toFloat() / 2,
        colors,
        null
    )

    inner class SegmentPaint : Paint(ANTI_ALIAS_FLAG) {
        init {
            style = Style.STROKE
        }
    }
}

private fun Path.addArcs(oval: RectF, drawStartPoint: Float, drawEndPoint: Float, drawTolerance: Float,
                         stepAngle: Float, stepWithSpacing: Float, segmentAngle: Float) {
    var i = drawStartPoint
    while (i < drawEndPoint || i in (drawEndPoint - drawTolerance)..(drawEndPoint + drawTolerance)) {
        addArc(oval, i + (stepAngle / 2), segmentAngle)
        i += stepWithSpacing
    }
}