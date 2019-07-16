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

package com.sygic.maps.uikit.views.navigation.preview

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.annotation.CallSuper
import com.sygic.maps.uikit.views.R
import com.sygic.maps.uikit.views.common.ToggleImageButton
import com.sygic.maps.uikit.views.databinding.LayoutRoutePreviewControlsBinding
import com.sygic.maps.uikit.views.navigation.preview.state.PlayPauseButtonState

/**
 * A [RoutePreviewControls] TODO
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
open class RoutePreviewControls @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.routePreviewControlsStyle,
    defStyleRes: Int = R.style.SygicRoutePreviewControlsStyle
) : LinearLayout(context, attrs, defStyleAttr, defStyleRes) {

    private val binding: LayoutRoutePreviewControlsBinding =
        LayoutRoutePreviewControlsBinding.inflate(LayoutInflater.from(context), this, true)

    private var layoutMargin: Int = 0
    private var layoutMarginTop: Int = 0
    private var layoutMarginBottom: Int = 0
    private var layoutMarginStart: Int = 0
    private var layoutMarginEnd: Int = 0

    /**
     * TODO
     */
    interface OnPlayPauseStateChangedListener {
        fun onPlayPauseButtonStateChanged(state: PlayPauseButtonState)
    }

    init {
        isClickable = true
        gravity = Gravity.CENTER

        attrs?.let { attributeSet ->
            context.obtainStyledAttributes(
                attributeSet,
                R.styleable.RoutePreviewControls,
                defStyleAttr,
                defStyleRes
            ).apply {
                layoutMargin = getDimensionPixelSize(
                    R.styleable.RoutePreviewControls_android_layout_margin,
                    0
                )

                layoutMarginTop = getDimensionPixelSize(
                    R.styleable.RoutePreviewControls_android_layout_marginTop,
                    resources.getDimensionPixelSize(R.dimen.defaultRoutePreviewControlsLayoutMargin)
                )

                layoutMarginBottom = getDimensionPixelSize(
                    R.styleable.RoutePreviewControls_android_layout_marginBottom,
                    resources.getDimensionPixelSize(R.dimen.defaultRoutePreviewControlsLayoutMargin)
                )

                layoutMarginStart = getDimensionPixelSize(
                    R.styleable.RoutePreviewControls_android_layout_marginStart,
                    resources.getDimensionPixelSize(R.dimen.defaultRoutePreviewControlsLayoutMargin)
                )

                layoutMarginEnd = getDimensionPixelSize(
                    R.styleable.RoutePreviewControls_android_layout_marginEnd,
                    resources.getDimensionPixelSize(R.dimen.defaultRoutePreviewControlsLayoutMargin)
                )

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
     * Allows you to change state of the play/pause [ToggleImageButton]. The default value is [PlayPauseButtonState.PLAY].
     *
     * @param state [PlayPauseButtonState] to be applied to the play/pause [ToggleImageButton].
     */
    fun setPlayPauseButtonState(state: PlayPauseButtonState) {
        binding.routePreviewControlsPlayPauseButton.isChecked = (state == PlayPauseButtonState.PAUSE)
    }

    /**
     * Register a callback to be invoked when [RoutePreviewControls] play/pause action button state has changed.
     *
     * @param listener [OnPlayPauseStateChangedListener] callback to invoke on [RoutePreviewControls]
     * play/pause state change.
     */
    fun setOnPlayPauseButtonStateChangedListener(listener: OnPlayPauseStateChangedListener) {
        binding.routePreviewControlsPlayPauseButton.onCheckedChangedListener =
            object : ToggleImageButton.OnCheckedChangedListener {
                override fun onCheckedChanged(buttonView: ToggleImageButton, isChecked: Boolean) {
                    listener.onPlayPauseButtonStateChanged(if (isChecked) PlayPauseButtonState.PAUSE else PlayPauseButtonState.PLAY)
                }
            }
    }

    /**
     * Register a callback to be invoked when [RoutePreviewControls] speed action button is clicked.
     *
     * @param listener [OnClickListener] callback to invoke on [RoutePreviewControls] speed button click.
     */
    fun setOnSpeedButtonClickListener(listener: OnClickListener) {
        binding.routePreviewControlsSpeedButton.setOnClickListener(listener)
    }

    /**
     * Register a callback to be invoked when [RoutePreviewControls] stop action button is clicked.
     *
     * @param listener [OnClickListener] callback to invoke on [RoutePreviewControls] stop button click.
     */
    fun setOnStopButtonClickListener(listener: OnClickListener) {
        binding.routePreviewControlsStopButton.setOnClickListener(listener)
    }
}