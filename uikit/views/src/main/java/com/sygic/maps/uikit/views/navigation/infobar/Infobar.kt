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

package com.sygic.maps.uikit.views.navigation.infobar

import android.animation.LayoutTransition
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.annotation.CallSuper
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.constraintlayout.widget.ConstraintLayout
import com.sygic.maps.uikit.views.R
import com.sygic.maps.uikit.views.common.extensions.EMPTY_STRING
import com.sygic.maps.uikit.views.common.extensions.backgroundTint
import com.sygic.maps.uikit.views.common.extensions.tint
import com.sygic.maps.uikit.views.databinding.LayoutInfobarInternalBinding
import com.sygic.maps.uikit.views.navigation.infobar.items.InfobarItemsHolder

/**
 * A [Infobar] view can be used as an visual presentation component for the navigation info data (eta, distanceToEnd,
 * altitude etc.) and as user interaction component. It contains two [ImageButton]'s (left/right) and pre-customized
 * primary/secondary [TextView] which can be controlled with [InfobarItemsHolder] class.
 *
 * The [Infobar] design can be completely changed with the custom _infoBarStyle_ or the standard android
 * attributes as _background_, _navigationTextColorPrimary_ or _navigationTextColorSecondary_ can be used. The [Infobar]
 * view has also own attributes such as _leftButtonIcon_, _leftButtonIconTint_, _leftButtonBackground_, _leftButtonBackgroundTint_,
 * _rightButtonIcon_, _rightButtonIconTint_, _rightButtonBackground_ and _rightButtonBackgroundTint_.
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
open class Infobar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.infoBarStyle,
    defStyleRes: Int = R.style.SygicInfobarStyle
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val binding: LayoutInfobarInternalBinding =
        LayoutInfobarInternalBinding.inflate(LayoutInflater.from(context), this, true)

    private var layoutMargin: Int = 0
    private var layoutMarginTop: Int = 0
    private var layoutMarginBottom: Int = 0
    private var layoutMarginStart: Int = 0
    private var layoutMarginEnd: Int = 0

    init {
        isClickable = true
        layoutTransition = LayoutTransition()

        attrs?.let { attributeSet ->
            @Suppress("Recycle")
            context.obtainStyledAttributes(
                attributeSet,
                R.styleable.Infobar,
                defStyleAttr,
                defStyleRes
            ).also {
                setBackgroundResource(it.getResourceId(R.styleable.Infobar_android_background, 0))

                setLeftButtonImageResource(
                    it.getResourceId(R.styleable.Infobar_leftButtonIcon, 0),
                    it.getResourceId(R.styleable.Infobar_leftButtonIconTint, 0)
                )
                setLeftButtonBackgroundResource(
                    it.getResourceId(R.styleable.Infobar_leftButtonBackground, 0),
                    it.getResourceId(R.styleable.Infobar_leftButtonBackgroundTint, 0)
                )
                setRightButtonImageResource(
                    it.getResourceId(R.styleable.Infobar_rightButtonIcon, 0),
                    it.getResourceId(R.styleable.Infobar_rightButtonIconTint, 0)
                )
                setRightButtonBackgroundResource(
                    it.getResourceId(R.styleable.Infobar_rightButtonBackground, 0),
                    it.getResourceId(R.styleable.Infobar_rightButtonBackgroundTint, 0)
                )

                elevation = it.getDimensionPixelSize(R.styleable.Infobar_android_elevation, 0).toFloat()
                layoutMargin = it.getDimensionPixelSize(R.styleable.Infobar_android_layout_margin, 0)
                layoutMarginTop = it.getDimensionPixelSize(R.styleable.Infobar_android_layout_marginTop, 0)
                layoutMarginBottom = it.getDimensionPixelSize(R.styleable.Infobar_android_layout_marginBottom, 0)
                layoutMarginStart = it.getDimensionPixelSize(R.styleable.Infobar_android_layout_marginStart, 0)
                layoutMarginEnd = it.getDimensionPixelSize(R.styleable.Infobar_android_layout_marginEnd, 0)

                it.recycle()
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
     * Sets the left button image resource and optionally an tint.
     *
     * @param imageResource [Int] to be used as left button image resource.
     * @param tintColor [Int] optional parameter to be used as left button tint.
     */
    @JvmOverloads
    fun setLeftButtonImageResource(@DrawableRes imageResource: Int, @ColorRes tintColor: Int = 0) {
        if (imageResource != 0) binding.infobarLeftButton.setImageResource(imageResource)
        if (tintColor != 0) setLeftButtonImageTint(tintColor)
    }

    /**
     * Sets the left button image tint.
     *
     * @param tintColor [Int] left button tint.
     */
    fun setLeftButtonImageTint(@ColorRes tintColor: Int) {
        if (tintColor != 0) binding.infobarLeftButton.tint(tintColor)
    }

    /**
     * Sets the left button background resource and optionally an tint.
     *
     * @param backgroundResource [Int] to be used as left button background resource.
     * @param tintColor [Int] optional parameter to be used as left button background tint.
     */
    @JvmOverloads
    fun setLeftButtonBackgroundResource(@DrawableRes backgroundResource: Int, @ColorRes tintColor: Int = 0) {
        if (backgroundResource != 0) binding.infobarLeftButton.setBackgroundResource(backgroundResource)
        if (tintColor != 0) setLeftButtonBackgroundTint(tintColor)
    }

    /**
     * Sets the left button background tint.
     *
     * @param tintColor [Int] left button background tint.
     */
    fun setLeftButtonBackgroundTint(@ColorRes tintColor: Int) {
        if (tintColor != 0) binding.infobarLeftButton.backgroundTint(tintColor)
    }

    /**
     * Register a callback to be invoked when [Infobar] left action button is clicked.
     *
     * @param listener [View.OnClickListener] callback to invoke on [Infobar] left action button click.
     */
    fun setOnLeftButtonClickListener(listener: OnClickListener) {
        binding.infobarLeftButton.setOnClickListener(listener)
    }

    /**
     * Sets the left button visibility.
     *
     * @param visible [Boolean] true to sets the left button visible, false otherwise
     */
    fun setLeftButtonVisible(visible: Boolean) {
        binding.infobarLeftButton.visibility = if (visible) VISIBLE else GONE
    }

    /**
     * Sets the right button image resource and optionally an tint.
     *
     * @param imageResource [Int] to be used as right button image resource.
     * @param tintColor [Int] optional parameter to be used as right button tint.
     */
    @JvmOverloads
    fun setRightButtonImageResource(@DrawableRes imageResource: Int, @ColorRes tintColor: Int = 0) {
        if (imageResource != 0) binding.infobarRightButton.setImageResource(imageResource)
        if (tintColor != 0) setRightButtonImageTint(tintColor)
    }

    /**
     * Sets the right button image tint.
     *
     * @param tintColor [Int] right button tint.
     */
    fun setRightButtonImageTint(@ColorRes tintColor: Int) {
        if (tintColor != 0) binding.infobarRightButton.tint(tintColor)
    }

    /**
     * Sets the right button background resource and optionally an tint.
     *
     * @param backgroundResource [Int] to be used as right button background resource.
     * @param tintColor [Int] optional parameter to be used as right button background tint.
     */
    @JvmOverloads
    fun setRightButtonBackgroundResource(@DrawableRes backgroundResource: Int, @ColorRes tintColor: Int = 0) {
        if (backgroundResource != 0) binding.infobarRightButton.setBackgroundResource(backgroundResource)
        if (tintColor != 0) setRightButtonBackgroundTint(tintColor)
    }

    /**
     * Sets the right button background tint.
     *
     * @param tintColor [Int] right button background tint.
     */
    fun setRightButtonBackgroundTint(@ColorRes tintColor: Int) {
        if (tintColor != 0) binding.infobarRightButton.backgroundTint(tintColor)
    }

    /**
     * Register a callback to be invoked when [Infobar] right action button is clicked.
     *
     * @param listener [View.OnClickListener] callback to invoke on [Infobar] right action button click.
     */
    fun setOnRightButtonClickListener(listener: OnClickListener) {
        binding.infobarRightButton.setOnClickListener(listener)
        binding.infobarRightButton.visibility = View.VISIBLE
    }

    /**
     * Sets the right button visibility.
     *
     * @param visible [Boolean] true to sets the right button visible, false otherwise
     */
    fun setRightButtonVisible(visible: Boolean) {
        binding.infobarRightButton.visibility = if (visible) VISIBLE else GONE
    }

    /**
     * Sets the [InfobarItemsHolder] which will be converted to the secondary infobar text.
     *
     * @param itemsHolder [InfobarItemsHolder] primary infobar ItemsHolder with valid data, empty list otherwise.
     */
    fun setPrimaryItemsHolder(itemsHolder: InfobarItemsHolder) {
        if (itemsHolder.isNotEmpty()) {
            binding.infobarPrimaryTextView.text = itemsHolder.items.joinToString(
                itemsHolder.divider,
                itemsHolder.prefix,
                itemsHolder.postfix,
                itemsHolder.limit
            )
            binding.infobarPrimaryTextView.visibility = View.VISIBLE
        } else {
            binding.infobarPrimaryTextView.text = EMPTY_STRING
            binding.infobarPrimaryTextView.visibility = View.GONE
        }
    }

    /**
     * Sets the [InfobarItemsHolder] which will be converted to the secondary infobar text.
     *
     * @param itemsHolder [InfobarItemsHolder] secondary infobar ItemsHolder with valid data, empty list otherwise.
     */
    fun setSecondaryItemsHolder(itemsHolder: InfobarItemsHolder) {
        if (itemsHolder.isNotEmpty()) {
            binding.infobarSecondaryTextView.text = itemsHolder.items.joinToString(
                itemsHolder.divider,
                itemsHolder.prefix,
                itemsHolder.postfix,
                itemsHolder.limit
            )
            binding.infobarSecondaryTextView.visibility = View.VISIBLE
        } else {
            binding.infobarSecondaryTextView.text = EMPTY_STRING
            binding.infobarSecondaryTextView.visibility = View.GONE
        }
    }
}