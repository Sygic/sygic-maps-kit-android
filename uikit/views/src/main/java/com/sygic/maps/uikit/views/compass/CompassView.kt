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

package com.sygic.maps.uikit.views.compass

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.annotation.CallSuper
import androidx.annotation.DrawableRes
import com.sygic.maps.uikit.views.R

private const val DEFAULT_ANIMATION_DELAY = 500L
private const val DEFAULT_ANIMATION_DURATION = 300L

/**
 * A [CompassView] is a simple view using virtual needle to point to the geographic north relatively to the map rotation.
 * The view can be simply controlled with the standard [setRotation] method.
 *
 * The size, background drawable or color can be changed with the custom _compassStyle_ style or standard android
 * attributes as _background_ or _accentColor_ definition.
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
open class CompassView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.compassStyle,
    defStyleRes: Int = R.style.SygicCompassStyle
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes) {

    /**
     * A *[hideAnimationDelay]* modifies the [CompassView] auto hide delay. The default value is [DEFAULT_ANIMATION_DELAY].
     *
     * @param [Long] delay value in milliseconds.
     *
     * @return the animation delay value in milliseconds.
     */
    var hideAnimationDelay: Long = DEFAULT_ANIMATION_DELAY
        protected set

    /**
     * A *[hideAnimationDuration]* modifies the [CompassView] auto hide animation duration. The default value is [DEFAULT_ANIMATION_DURATION].
     *
     * @param [Long] animation duration value in milliseconds.
     *
     * @return the animation duration value in milliseconds.
     */
    var hideAnimationDuration: Long = DEFAULT_ANIMATION_DURATION
        protected set

    /**
     * A *[hideCompassIfNorthUpAllowed]* modifies the [CompassView] auto hide behaviour.
     *
     * @param [Boolean] true to hide the [CompassView] automatically when it points northwards, false otherwise.
     *
     * @return whether the [CompassView] auto hide behaviour is on or off.
     */
    var hideCompassIfNorthUpAllowed: Boolean = false

    protected val arrowImageView: ImageView?
    protected val alphaSetter = AlphaSetter()

    private var needleRotation: Float

    init {
        needleRotation = rotation

        addView(createImageView(context, attrs, defStyleAttr, R.drawable.ic_compass_nicks))
        addView(createImageView(context, resourceId = R.drawable.ic_compass_arrow).also { arrowImageView = it })
    }

    private fun createImageView(context: Context, attrs: AttributeSet? = null,
                                defStyleAttr: Int = 0, @DrawableRes resourceId: Int): ImageView {
        val imageView = ImageView(context, attrs, defStyleAttr)
        imageView.id = View.NO_ID
        imageView.setImageResource(resourceId)
        imageView.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        return imageView
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        hideCompassIfNorthUp(0)
    }

    /**
     * Allows you to change rotation of the [CompassView] needle. The default value is 0 (north).
     *
     * @param rotation [Float] to be applied to the [CompassView] needle.
     */
    @CallSuper
    override fun setRotation(rotation: Float) {
        val isNorth = isNorthUp(needleRotation)
        val northWanted = isNorthUp(rotation)
        needleRotation = rotation

        if (isNorth != northWanted) {
            // the visibility is going to change
            removeCallbacks(alphaSetter)
            postDelayed(alphaSetter, hideAnimationDelay)
        }

        // setRotation is sometimes called from View constructor
        arrowImageView?.let { it.rotation = rotation }
    }

    private fun hideCompassIfNorthUp(animationDuration: Long) {
        if (!hideCompassIfNorthUpAllowed) {
            return
        }

        if (!isNorthUp(needleRotation)) {
            animate().alpha(1f).duration = animationDuration
        } else {
            animate().alpha(0f).duration = animationDuration
        }
    }

    private fun isNorthUp(rotation: Float): Boolean = rotation >= -1 && rotation <= 1

    protected inner class AlphaSetter : Runnable {
        override fun run() {
            hideCompassIfNorthUp(hideAnimationDuration)
        }
    }
}
