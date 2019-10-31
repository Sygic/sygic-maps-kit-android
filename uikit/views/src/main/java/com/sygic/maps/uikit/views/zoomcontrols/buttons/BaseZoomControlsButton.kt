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

package com.sygic.maps.uikit.views.zoomcontrols.buttons

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.sygic.maps.uikit.views.R
import com.sygic.maps.uikit.views.common.extensions.getDrawable
import com.sygic.maps.uikit.views.zoomcontrols.ZoomControlsMenu

@Suppress("unused", "MemberVisibilityCanBePrivate")
internal abstract class BaseZoomControlsButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.zoomControlsMenuStyle,
    defStyleRes: Int = R.style.SygicZoomControlsMenuStyle,
    @DrawableRes iconDrawableRes: Int = 0
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes) {

    internal var interactionListener: ZoomControlsMenu.InteractionListener? = null

    private val iconImageView: ImageView
    private val showAnimation = AnimationUtils.loadAnimation(context, R.anim.zoom_button_scale_up)
    private val hideAnimation = AnimationUtils.loadAnimation(context, R.anim.zoom_button_scale_down)

    open fun onActionDown() {}
    abstract fun onActionUpOrCancel()

    init {
        isClickable = true
        isFocusable = true
        clipToOutline = true

        attrs?.let { attributeSet ->
            @Suppress("Recycle")
            context.obtainStyledAttributes(
                attributeSet,
                R.styleable.ZoomControlsMenu,
                defStyleAttr,
                defStyleRes
            ).also {
                setBackgroundResource(it.getResourceId(R.styleable.ZoomControlsMenu_buttonBackground,0))

                foreground = getDrawable(it.getResourceId(R.styleable.ZoomControlsMenu_buttonForeground,0))
                elevation = it.getDimensionPixelSize(R.styleable.ZoomControlsMenu_buttonElevation, 0).toFloat()
            }.recycle()
        }

        addView(createButtonIcon(context, attrs, defStyleAttr, iconDrawableRes).also { iconImageView = it })
    }

    private fun createButtonIcon(context: Context, attrs: AttributeSet?, defStyleAttr: Int, @DrawableRes resourceId: Int) =
        ImageView(context, attrs, defStyleAttr).apply {
            id = View.NO_ID
            setImageResource(resourceId)
            layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply { gravity = Gravity.CENTER }
        }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> onActionDown()
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                onActionUpOrCancel()
            }
        }
        return super.onTouchEvent(event)
    }

    private fun playShowAnimation() {
        hideAnimation.cancel()
        startAnimation(showAnimation)
    }

    private fun playHideAnimation() {
        showAnimation.cancel()
        startAnimation(hideAnimation)
    }

    internal fun setImageResource(@DrawableRes iconDrawableRes: Int) {
        iconImageView.setImageResource(iconDrawableRes)
    }

    internal fun setImageDrawable(drawable: Drawable?) {
        iconImageView.setImageDrawable(drawable)
    }

    fun show(animate: Boolean) {
        if (visibility == View.GONE) {
            if (animate) {
                playShowAnimation()
            }
            super.setVisibility(View.VISIBLE)
        }
    }

    fun hide(animate: Boolean) {
        if (visibility == View.VISIBLE) {
            if (animate) {
                playHideAnimation()
            }
            super.setVisibility(View.GONE)
        }
    }
}
