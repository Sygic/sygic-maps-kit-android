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
import androidx.annotation.DrawableRes
import com.sygic.maps.uikit.views.R
import com.sygic.maps.uikit.views.databinding.LayoutFullSignpostViewInternalBinding
import com.sygic.maps.uikit.views.navigation.roadsign.data.RoadSignData

/**
 * A [FullSignpostView] TODO
 */
@Suppress("unused", "MemberVisibilityCanBePrivate", "CustomViewStyleable")
open class FullSignpostView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.signpostViewStyle,
    defStyleRes: Int = R.style.SygicSignpostViewStyle
) : BaseSignpostView(context, attrs, defStyleAttr, defStyleRes) {

    private val binding = LayoutFullSignpostViewInternalBinding.inflate(LayoutInflater.from(context), this, true)

    fun setDistance(distanceText: String?) {
        binding.signpostDistanceTextView.text = distanceText
    }

    fun setPrimaryDirection(@DrawableRes primaryDirectionDrawableRes: Int) {
        binding.signpostPrimaryDirectionImageView.setImageResource(primaryDirectionDrawableRes)
    }

    fun setOnPrimaryDirectionClickListener(primaryDirectionListener: OnClickListener?) {
        binding.signpostPrimaryDirectionImageView.setOnClickListener(primaryDirectionListener)
    }

    fun setSecondaryDirection(@DrawableRes secondaryDirectionDrawableRes: Int) {
        binding.signpostSecondaryDirectionImageView.setImageResource(secondaryDirectionDrawableRes)
    }

    fun setSecondaryDirectionContainerVisible(visible: Boolean) {
        binding.signpostSecondaryDirectionContainer.visibility = if (visible) VISIBLE else GONE
    }

    fun setSecondaryDirectionText(secondaryDirectionText: String?) {
        binding.signpostSecondaryDirectionTextView.text = secondaryDirectionText
    }

    fun setInstructionText(instructionText: String?) {
        binding.signpostInstructionTextView.text = instructionText
    }

    fun setRoadSigns(roadSigns: List<RoadSignData>) {
        binding.signpostRoadSignsView.data = roadSigns
    }

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
}
