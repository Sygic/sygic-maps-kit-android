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

package com.sygic.maps.uikit.views.common

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.sygic.maps.uikit.views.R
import com.sygic.maps.uikit.views.databinding.LayoutAdvancedInfoViewInternalBinding

/**
 * A [AdvancedInfoView] is an easily customizable component to display a variety of information states (info, error, empty etc.)
 *
 * Content and visibility of each individual parts can be changed with the _imageSource_, _titleText_, _suggestionText_
 * or _actionButtonText_ attribute.
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
class AdvancedInfoView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : LinearLayout(context, attrs, defStyleAttr, defStyleRes) {

    private val binding: LayoutAdvancedInfoViewInternalBinding =
        LayoutAdvancedInfoViewInternalBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        orientation = VERTICAL
        gravity = Gravity.CENTER

        attrs?.let { attributeSet ->
            context.obtainStyledAttributes(attributeSet, R.styleable.AdvancedInfoView, defStyleAttr, 0).apply {

                getResourceId(R.styleable.AdvancedInfoView_imageSource, NO_ID).let {
                    if (it != NO_ID) setImageResource(it)
                }

                getResourceId(R.styleable.AdvancedInfoView_imageDescription, NO_ID).let {
                    if (it != NO_ID) setImageDescription(it)
                }

                getResourceId(R.styleable.AdvancedInfoView_titleText, NO_ID).let {
                    if (it != NO_ID) setTitleText(it)
                }

                getResourceId(R.styleable.AdvancedInfoView_suggestionText, NO_ID).let {
                    if (it != NO_ID) setSuggestionText(it)
                }

                getResourceId(R.styleable.AdvancedInfoView_actionButtonText, NO_ID).let {
                    if (it != NO_ID) setActionButtonText(it)
                }

                recycle()
            }
        }
    }

    fun setImageResource(@DrawableRes drawableRes: Int) {
        binding.icon.setImageResource(drawableRes)
        binding.icon.visibility = VISIBLE
    }

    fun setImageDrawable(drawable: Drawable?) {
        if (drawable == null) {
            binding.icon.visibility = GONE
            return
        }

        binding.icon.setImageDrawable(drawable)
        binding.icon.visibility = VISIBLE
    }

    fun setImageDescription(@StringRes descriptionId: Int) {
        context?.let { setImageDescription(it.getString(descriptionId)) }
    }

    fun setImageDescription(description: String) {
        if (description.isNotEmpty()) {
            binding.icon.contentDescription = description
        }
    }

    fun setTitleText(@StringRes titleTextId: Int) {
        context?.let { setTitleText(it.getString(titleTextId)) }
    }

    fun setTitleText(titleText: String) {
        if (titleText.isEmpty()) {
            binding.titleText.visibility = GONE
            return
        }

        binding.titleText.text = titleText
        binding.titleText.visibility = VISIBLE
    }

    fun setSuggestionText(@StringRes suggestionTextId: Int) {
        context?.let { setSuggestionText(it.getString(suggestionTextId)) }
    }

    fun setSuggestionText(suggestionText: String) {
        if (suggestionText.isEmpty()) {
            binding.suggestionText.visibility = GONE
            return
        }

        binding.suggestionText.text = suggestionText
        binding.suggestionText.visibility = VISIBLE
    }

    fun setActionButtonText(@StringRes actionButtonTextId: Int) {
        context?.let { setActionButtonText(it.getString(actionButtonTextId)) }
    }

    fun setActionButtonText(actionButtonText: String) {
        if (actionButtonText.isEmpty()) {
            binding.actionButton.visibility = GONE
            return
        }

        binding.actionButton.text = actionButtonText
        binding.actionButton.visibility = VISIBLE
    }

    override fun setOnClickListener(listener: OnClickListener?) {
        binding.actionButton.setOnClickListener(listener)
    }
}