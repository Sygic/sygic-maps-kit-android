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

package com.sygic.maps.uikit.views.zoomcontrols

import android.content.Context
import android.os.Handler
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import com.sygic.maps.uikit.views.R
import com.sygic.maps.uikit.views.common.extensions.isRtl
import com.sygic.maps.uikit.views.zoomcontrols.buttons.*

private const val ANIMATION_DELAY_PER_ITEM = 50

/**
 * A [ZoomControlsMenu] can be used to control camera zoom level or camera projection. The view state can be simply
 * controlled with the [toggleMenu] or alternatively with [open]/[close] method. The default state is closed (collapsed).
 *
 * You can register an [InteractionListener] using [setInteractionListener] method. Then you will be notified when something
 * interesting happens to the [ZoomControlsMenu] or it internal buttons. For example, you will be notified when the menu state
 * has been changed ([InteractionListener.onMenuOpened]) or when a ZoomInButton/ZoomOutButton/MapViewModeButton has been clicked.
 *
 * The size, background drawable or color can be changed with the custom _zoomControlsMenuStyle_ style or appropriate attributes.
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
open class ZoomControlsMenu @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.zoomControlsMenuStyle
) : LinearLayout(context, attrs, 0), ZoomControlsMenuButton.MenuCallback {

    private val uiHandler = Handler()
    private val menuButton: ZoomControlsMenuButton
    private val internalButtonMargin: Int = resources.getDimensionPixelSize(R.dimen.zoomControlsButtonMargin)

    private var interactionListener: InteractionListener? = null

    /**
     * A *[isMenuOpened]* reflect the state of [ZoomControlsMenu]. The default value is false (closed/collapsed).
     *
     * @return the actual [ZoomControlsMenu] state.
     */
    var isMenuOpened: Boolean = false
        private set(value) {
            field = value
            interactionListener?.onMenuOpened(value)
        }

    /**
     * Interface definition for a callback to be invoked when a menu interaction is triggered.
     */
    interface InteractionListener {
        /**
         * Called when a state of [ZoomControlsMenu] has been changed.
         *
         * @param opened True if menu is opened, false otherwise.
         */
        fun onMenuOpened(opened: Boolean)

        /**
         * Called when a [ZoomControlsZoomInButton] event [MotionEvent.ACTION_DOWN] has been called.
         */
        fun onZoomInStart()

        /**
         * Called when a [ZoomControlsZoomInButton] event [MotionEvent.ACTION_UP] or [MotionEvent.ACTION_CANCEL] has been called.
         */
        fun onZoomInStop()

        /**
         * Called when a [ZoomControlsZoomOutButton] event [MotionEvent.ACTION_DOWN] has been called.
         */
        fun onZoomOutStart()

        /**
         * Called when a [ZoomControlsZoomOutButton] event [MotionEvent.ACTION_UP] or [MotionEvent.ACTION_CANCEL] has been called.
         */
        fun onZoomOutStop()

        /**
         * Called when a [ZoomControlsMapViewModeButton] event [MotionEvent.ACTION_UP] or [MotionEvent.ACTION_CANCEL] has been called.
         */
        fun onCameraProjectionChanged()
    }

    init {
        orientation = HORIZONTAL
        clipChildren = false

        addView(
            ZoomControlsMenuButton(context, attrs, defStyleAttr, this).also { menuButton = it },
            LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        )

        val layoutParams = getChildLayoutParams()
        addView(ZoomControlsZoomOutButton(context, attrs, defStyleAttr).apply { hide(false) }, layoutParams)
        addView(ZoomControlsZoomInButton(context, attrs, defStyleAttr).apply { hide(false) }, layoutParams)
        addView(ZoomControlsMapViewModeButton(context, attrs, defStyleAttr).apply { hide(false) }, layoutParams)
    }

    private fun getChildLayoutParams(): LayoutParams {
        return LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
            context.isRtl().let {
                setMargins(if (!it) internalButtonMargin else 0, 0, if (it) internalButtonMargin else 0, 0)
            }
        }
    }

    /**
     * Allows you to change the visual state of the [ZoomControlsMapViewModeButton]. If the tiltType is [TiltType.TILT_2D],
     * then 3D icon will be used and vice versa.
     *
     * @param tiltType [TiltType] value to be applied to the [ZoomControlsMapViewModeButton] the visual state.
     */
    fun setTiltType(@TiltType tiltType: Int) {
        for (i in 0 until childCount) {
            getChildAt(i).let { if (it is ZoomControlsMapViewModeButton) it.cameraProjectionChanged(tiltType) }
        }
    }

    /**
     * Register a callback to be invoked when the menu interaction has been made. See [InteractionListener] for more information.
     *
     * @param interactionListener [InteractionListener] callback to invoke the menu interaction.
     */
    fun setInteractionListener(interactionListener: InteractionListener?) {
        this.interactionListener = interactionListener
        for (i in 0 until childCount) {
            (getChildAt(i) as BaseZoomControlsButton).interactionListener = interactionListener
        }
    }

    /**
     * Allows you to change/toggle the state of the [ZoomControlsMenu] depending on the [isMenuOpened] state.
     * See [open] or [close] method for more information.
     */
    override fun toggleMenu() {
        if (isMenuOpened) {
            close(true)
        } else {
            open(true)
        }
    }

    /**
     * Allows you to change the [ZoomControlsMenu] state to opened/expanded. All [ZoomControlsMenu] buttons will be visible.
     * If the menu is opening/expanding or is already opened, then the method call will be ignored.
     *
     * @param animate [Boolean] true to expand the [ZoomControlsMenu] with predefined animation, false otherwise.
     */
    fun open(animate: Boolean) {
        if (isMenuOpened) {
            return
        }

        menuButton.onMenuAction(true)

        var delay = 0L
        var counter = 0L
        for (i in 0 until childCount) {
            getChildAt(i).let {
                if (it is BaseZoomControlsButton && it.visibility != View.GONE) {
                    counter++

                    uiHandler.postDelayed(Runnable {
                        if (isMenuOpened) return@Runnable
                        if (it !is ZoomControlsMenuButton) it.show(animate)
                    }, delay)
                    delay += ANIMATION_DELAY_PER_ITEM
                }
            }
        }

        uiHandler.postDelayed({ isMenuOpened = true }, (++counter * ANIMATION_DELAY_PER_ITEM))
    }

    /**
     * Allows you to change the [ZoomControlsMenu] state to closed/collapsed. All [ZoomControlsMenu] buttons will be hidden.
     * If the menu is closing/collapsing or is already closed, then the method call will be ignored.
     *
     * @param animate [Boolean] true to collapse the [ZoomControlsMenu] with predefined animation, false otherwise.
     */
    fun close(animate: Boolean) {
        if (!isMenuOpened) {
            return
        }

        menuButton.onMenuAction(false)

        var delay = 0L
        var counter = 0L
        for (i in childCount - 1 downTo 0) {
            getChildAt(i).let {
                if (it is BaseZoomControlsButton && it.visibility != View.GONE) {
                    counter++

                    uiHandler.postDelayed(Runnable {
                        if (!isMenuOpened) return@Runnable
                        if (it !is ZoomControlsMenuButton) it.hide(animate)
                    }, delay)
                    delay += ANIMATION_DELAY_PER_ITEM
                }
            }
        }

        uiHandler.postDelayed({ isMenuOpened = false }, (++counter * ANIMATION_DELAY_PER_ITEM))
    }
}
