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

package com.sygic.maps.uikit.views.searchfab

import android.content.Context
import android.content.res.ColorStateList
import com.google.android.material.floatingactionbutton.FloatingActionButton
import android.util.AttributeSet
import com.sygic.maps.uikit.views.R
import com.sygic.maps.uikit.views.common.extensions.getColorFromAttr

/**
 * A [SearchFab] can be simply used as an entry point into the Search screen.
 *
 * As this class descends from [FloatingActionButton], [SearchFab] come also in two sizes: the default and the mini.
 * You can control the size with the fabSize attribute or setSize method.
 *
 * Unlike the [FloatingActionButton], background and icon color can be changed with the custom _searchFabStyle_ or
 * with the standard _backgroundTint_ and _tint_ attribute.
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
open class SearchFab @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.searchFabStyle,
    defStyleRes: Int = R.style.SygicSearchFabStyle
) : FloatingActionButton(context, attrs, defStyleAttr) {

    init {
        isClickable = true
        setImageResource(R.drawable.ic_search)

        attrs?.let { attributeSet ->
            @Suppress("Recycle")
            context.obtainStyledAttributes(
                attributeSet,
                R.styleable.SearchFab,
                defStyleAttr,
                defStyleRes
            ).also {

                imageTintList = ColorStateList.valueOf(
                    it.getColor(
                        R.styleable.SearchFab_android_tint,
                        context.getColorFromAttr(android.R.attr.colorAccent)
                    )
                )

                backgroundTintList = ColorStateList.valueOf(
                    it.getColor(
                        R.styleable.SearchFab_android_backgroundTint,
                        context.getColorFromAttr(android.R.attr.colorBackground)
                    )
                )

                it.recycle()
            }
        }
    }
}