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
) : BaseSignpostView(context, attrs, defStyleAttr, defStyleRes) { //TODO

    private val binding: LayoutFullSignpostViewInternalBinding =
        LayoutFullSignpostViewInternalBinding.inflate(LayoutInflater.from(context), this, true)

    fun setDistance(distance: String) {
        binding.signpostDistanceTextView.text = distance
    }

    fun setPrimaryDirection(@DrawableRes drawableRes: Int) {
        binding.signpostPrimaryDirectionImageView.setImageResource(drawableRes)
    }

    fun setOnPrimaryDirectionClickListener(listener: OnClickListener) {
        binding.signpostPrimaryDirectionImageView.setOnClickListener(listener)
    }

    fun setInstructionText(instructionText: String) {
        binding.signpostInstructionTextView.text = instructionText
    }

    fun setRoadSigns(roadSigns: List<RoadSignData>) {
        binding.signpostRoadSignsView.data = roadSigns
    }

    /*
    fun setSecondaryDirection(@DrawableRes drawableRes: Int) {
        if (drawableRes != 0) { //todo
            binding.signpostSecondaryDirectionWrapper.visibility = View.VISIBLE
            binding.signpostSecondaryDirectionImageView.setImageResource(drawableRes)
        } else {
            binding.signpostSecondaryDirectionWrapper.visibility = View.GONE
        }
    }

    fun setInstructionText(text: FormattedString) { //todo
        setInstructionText(if (TextUtils.isEmpty(text)) null else text.getFormattedString(context))
    }

    fun setInstructionTextColor(@ColorInt color: Int) { //todo
        binding.signpostInstructionTextView.setTextColor(color)
        binding.signpostDistanceTextView.setTextColor(color)
        binding.signpostSecondaryDirectionTextView.setTextColor(color)

        setImageViewTint(binding.signpostPrimaryDirectionImageView, color)
        setImageViewTint(binding.signpostSecondaryDirectionImageView, color)
        setImageViewTint(binding.signpostPictogramImageView, color)
    }

    fun setRoadSigns(info: List<RoadSignInfo.Data>) { //todo
        binding.signpostRoadSignsView.setData(info)
    }

    fun setPictogram(@DrawableRes pictogramDrawableRes: Int) { //todo
        if (pictogramDrawableRes != 0) {
            binding.signpostPictogramImageView.visibility = View.VISIBLE
            binding.signpostPictogramImageView.setImageResource(pictogramDrawableRes)
        } else {
            binding.signpostPictogramImageView.visibility = View.GONE
            binding.signpostPictogramImageView.setImageDrawable(null)
        }
    }

    companion object { //todo

        @BindingAdapter("primaryDirection", "secondaryDirection", "instructionTextColor")
        fun setDirectionsWithColor(
            view: SignpostView, @DrawableRes primary: Int,
            @DrawableRes secondary: Int, @ColorInt color: Int
        ) {
            view.setPrimaryDirection(primary)
            view.setSecondaryDirection(secondary)
            view.setInstructionTextColor(color)
        }
    }*/
}