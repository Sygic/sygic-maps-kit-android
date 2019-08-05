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
import androidx.core.content.ContextCompat
import com.sygic.maps.uikit.views.R
import kotlin.math.min

private const val angleMax = 270
private const val angleStartPoint = 136 //todo: fixed
private const val segmentStep = 10 // todo: currently is the value with width together
private const val segmentWidth = 8f
private const val isRoundEdge = false

/*TODO: make configurable from outside only segment width, height, step (space between) */
class SpeedProgressView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = /*R.attr.currentSpeedViewStyle*/ 0, //todo
    defStyleRes: Int = /*R.style.SygicCurrentSpeedViewStyle*/ 0 //todo
) : View(context, attrs, defStyleAttr, defStyleRes) {

    private val oval = RectF()
    private val progressPath: Path = Path()
    private val backgroundPath: Path = Path()

    private var left: Float = 0f
    private var top: Float = 0f
    private var right: Float = 0f
    private var bottom: Float = 0f
    private var isGradientColor: Boolean = false //todo

    private var maximumProgress = 100f
    var progress = 0f
        set(value) {
            field = if (value <= maximumProgress) value else maximumProgress
            invalidate()
        }

    var progressColor: Int = ContextCompat.getColor(context, R.color.progress_color)
        set(value) {
            field = value
            this.foregroundPaint.color = value
            invalidate()
            requestLayout()
        }

    private var strokeWidth = resources.getDimension(R.dimen.default_stroke_width)
    private var backgroundStrokeWidth = resources.getDimension(R.dimen.default_background_stroke_width)
    var strokeBackgroundColor = ContextCompat.getColor(context, R.color.background_color)
        set(value) {
            field = value
            this.backgroundPaint.color = value
            invalidate()
            requestLayout()
        }

    private var backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = this@SpeedProgressView.strokeBackgroundColor
        style = Paint.Style.STROKE
        strokeWidth = this@SpeedProgressView.backgroundStrokeWidth
    }
    private var foregroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = this@SpeedProgressView.progressColor
        style = Paint.Style.STROKE
        strokeWidth = this@SpeedProgressView.strokeWidth
        if (isRoundEdge) {
            strokeCap = Paint.Cap.ROUND
        }
    }

    var gradientColors: IntArray = intArrayOf()
        set(value) {
            field = value
            this.isGradientColor = value.isNotEmpty()
        }

    init {
        attrs?.let { attributeSet ->
            context.obtainStyledAttributes(
                attributeSet,
                R.styleable.SpeedProgressView,
                defStyleAttr,
                defStyleRes
            ).apply {
                strokeWidth = getDimension(R.styleable.SpeedProgressView_progressWidth, strokeWidth)
                backgroundStrokeWidth = getDimension(R.styleable.SpeedProgressView_barWidth, backgroundStrokeWidth)
                progressColor = getInt(R.styleable.SpeedProgressView_progressColor, progressColor)
                strokeBackgroundColor = getInt(R.styleable.SpeedProgressView_barColor, strokeBackgroundColor)

                recycle()
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val min = setDimensions(widthMeasureSpec, heightMeasureSpec)

        left = 0 + strokeWidth / 2
        top = 0 + strokeWidth / 2
        right = min - strokeWidth / 2
        bottom = min - strokeWidth / 2
        oval.set(left, top, right, bottom)
    }

    private fun setDimensions(widthMeasureSpec: Int, heightMeasureSpec: Int): Int {
        val height = getDefaultSize(suggestedMinimumHeight, heightMeasureSpec)
        val width = getDefaultSize(suggestedMinimumWidth, widthMeasureSpec)

        val smallerDimens = min(width, height)
        setMeasuredDimension(smallerDimens, smallerDimens)
        return smallerDimens
    }

    public override fun onDraw(canvas: Canvas) {
        if (isGradientColor) {
            setGradientPaint(foregroundPaint, left, top, right, bottom, gradientColors)
        }

        with(backgroundPath) {
            reset()
            for (i in angleStartPoint until angleMax + angleStartPoint step segmentStep) {
                addArc(oval, i.toFloat(), segmentWidth)
            }
            canvas.drawPath(this, backgroundPaint)
        }

        //todo
        val angle = angleMax * progress.toInt() / maximumProgress + angleStartPoint
        var i = angleStartPoint
        while (i < angle) {
            progressPath.addArc(oval, i.toFloat(), segmentWidth)
            i += segmentStep
        }
        canvas.drawPath(progressPath, foregroundPaint)

        /*with(progressPath) {
            reset()

            //todo
            val angle = (angleMax * progress.toInt() / maximumProgress + angleStartPoint).toInt()
            for (i in angleStartPoint..angle step segmentStep) {
                addArc(oval, i.toFloat(), segmentWidth)
            }
            canvas.drawPath(this, foregroundPaint)
        }*/
    }

    fun setGradientPaint(paint: Paint, left: Float, top: Float, right: Float, bottom: Float, colors: IntArray) {
        val linearGradient = LinearGradient(left, top, right, bottom, colors, null, Shader.TileMode.CLAMP)
        paint.shader = linearGradient
        paint.isAntiAlias = true
    }

    //todo
    fun setSweepGradientGradientPaint(paint: Paint, width: Float, height: Float, colorStart: Int, colorEnd: Int) {
        paint.shader = SweepGradient(width, height, colorStart, colorEnd)
        paint.isAntiAlias = true
    }
}
