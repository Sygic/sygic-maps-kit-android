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

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.sygic.maps.uikit.views.R
import com.sygic.maps.uikit.views.common.utils.TextHolder
import com.sygic.maps.uikit.views.databinding.LayoutFullSignpostViewInternalBinding
import com.sygic.maps.uikit.views.navigation.lanes.SimpleLanesView
import com.sygic.maps.uikit.views.navigation.lanes.data.SimpleLanesData
import com.sygic.maps.uikit.views.navigation.roadsign.RoadSignsView
import com.sygic.maps.uikit.views.navigation.roadsign.data.RoadSignData

/**
 * A [FullSignpostView] can be used as an visual presentation component for the navigation Signpost or Direction. It contains
 * __distanceTextView__, __primaryDirectionImageView__, __instructionTextView__ , [RoadSignsView], __pictogramImageView__,
 * __secondaryDirectionTextView__ and __secondaryDirectionImageView__.
 */
@Suppress("unused", "MemberVisibilityCanBePrivate", "CustomViewStyleable")
open class FullSignpostView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.signpostViewStyle,
    defStyleRes: Int = R.style.SygicSignpostViewStyle
) : BaseSignpostView(context, attrs, defStyleAttr, defStyleRes) {

    /**
     * Toggles embedded [SimpleLanesView] _on_ or _off_
     */
    var lanesViewEnabled: Boolean = true
        set(value) {
            field = value
            if (!value) {
                binding.signpostSecondaryDirectionContainer.displayedChild = 0
            }
        }

    private val binding = LayoutFullSignpostViewInternalBinding.inflate(LayoutInflater.from(context), this, true)

    /**
     * Sets the text to be displayed as the distance.
     *
     * @param distanceTextHolder [TextHolder] text to be displayed
     */
    fun setDistance(distanceTextHolder: TextHolder) {
        setDistance(distanceTextHolder.getText(context))
    }

    /**
     * Sets the text to be displayed as the distance.
     *
     * @param distanceText [String] text to be displayed
     */
    fun setDistance(distanceText: String?) {
        binding.signpostDistanceTextView.text = distanceText
    }

    /**
     * Sets the drawable to be displayed as the primary direction.
     *
     * @param primaryDirectionDrawableRes [Int] primary direction drawable res to be displayed
     */
    fun setPrimaryDirection(@DrawableRes primaryDirectionDrawableRes: Int) {
        binding.signpostPrimaryDirectionImageView.setImageResource(primaryDirectionDrawableRes)
    }

    /**
     * Register a callback to be invoked when primary direction view is clicked.
     *
     * @param primaryDirectionListener [View.OnClickListener] callback to invoke on primary direction click
     */
    fun setOnPrimaryDirectionClickListener(primaryDirectionListener: OnClickListener?) {
        binding.signpostPrimaryDirectionImageView.setOnClickListener(primaryDirectionListener)
    }

    /**
     * Sets the drawable to be displayed as the secondary direction.
     *
     * @param secondaryDirectionDrawableRes [Int] secondary direction drawable res to be displayed
     */
    fun setSecondaryDirection(@DrawableRes secondaryDirectionDrawableRes: Int) {
        binding.signpostSecondaryDirectionImageView.setImageResource(secondaryDirectionDrawableRes)
    }

    /**
     * Sets the secondary direction visibility.
     *
     * @param visible [Boolean] true to sets the secondary direction visible, false otherwise
     */
    fun setSecondaryDirectionContainerVisible(visible: Boolean) {
        binding.signpostSecondaryDirectionContainer.visibility = if (visible) VISIBLE else GONE
    }

    /**
     * Sets the text to be displayed as the secondary direction text.
     *
     * @param secondaryDirectionText [Int] text to be displayed
     */
    fun setSecondaryDirectionText(@StringRes secondaryDirectionText: Int) {
        setSecondaryDirectionText(resources.getString(secondaryDirectionText))
    }

    /**
     * Sets the text to be displayed as the secondary direction text.
     *
     * @param secondaryDirectionTextHolder [TextHolder] text to be displayed
     */
    fun setSecondaryDirectionText(secondaryDirectionTextHolder: TextHolder) {
        setSecondaryDirectionText(secondaryDirectionTextHolder.getText(context))
    }

    /**
     * Sets the text to be displayed as the secondary direction text.
     *
     * @param secondaryDirectionText [String] text to be displayed
     */
    fun setSecondaryDirectionText(secondaryDirectionText: String?) {
        binding.signpostSecondaryDirectionTextView.text = secondaryDirectionText
    }

    /**
     * Sets the text to be displayed as the instruction text.
     *
     * @param instructionText [Int] text to be displayed
     */
    fun setInstructionText(@StringRes instructionText: Int) {
        setInstructionText(resources.getString(instructionText))
    }

    /**
     * Sets the text to be displayed as the instruction text.
     *
     * @param instructionTextHolder [TextHolder] text to be displayed
     */
    fun setInstructionText(instructionTextHolder: TextHolder) {
        setInstructionText(instructionTextHolder.getText(context))
    }

    /**
     * Sets the text to be displayed as the instruction text.
     *
     * @param instructionText [String] text to be displayed
     */
    fun setInstructionText(instructionText: String?) {
        binding.signpostInstructionTextView.text = instructionText
    }

    /**
     * Sets the [RoadSignData] list to be displayed as the roadSigns icon's.
     *
     * @param roadSigns [List] of [RoadSignData] to be displayed
     */
    fun setRoadSigns(roadSigns: List<RoadSignData>) {
        binding.signpostRoadSignsView.data = roadSigns
    }

    /**
     * Sets the drawable to be displayed as the pictogram icon.
     *
     * @param pictogramDrawableRes [Int] pictogram drawable res to be displayed
     */
    fun setPictogram(@DrawableRes pictogramDrawableRes: Int) {
        if (pictogramDrawableRes != 0) {
            with(binding.signpostPictogramImageView) {
                visibility = VISIBLE
                setImageResource(pictogramDrawableRes)
            }
        } else {
            with(binding.signpostPictogramImageView) {
                visibility = GONE
                setImageDrawable(null)
            }
        }
    }

    /**
     * Sets the data about actual lanes situation on the road
     *
     * @param lanesData [Array] od [SimpleLanesData] items representing lanes on the road
     */
    fun setLanesData(lanesData: Array<SimpleLanesData>) {
        if (!lanesViewEnabled || lanesData.isEmpty()) {
            binding.signpostSecondaryDirectionContainer.displayedChild = 0
        } else {
            binding.signpostLanesView.lanesData = lanesData
            binding.signpostSecondaryDirectionContainer.displayedChild = 1
        }
    }
}
