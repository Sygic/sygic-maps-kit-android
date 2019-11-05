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
import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import com.sygic.maps.uikit.views.R
import com.sygic.maps.uikit.views.common.extensions.getDrawable

internal class ZoomControlsMenuButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.zoomControlsMenuStyle,
    defStyleRes: Int = R.style.SygicZoomControlsMenuStyle,
    private val callback: MenuCallback? = null
) : BaseZoomControlsButton(context, attrs, defStyleAttr, defStyleRes, R.drawable.ic_plus_minus) {

    private val openAnimation = getDrawable(R.drawable.vector_morph_plus_minus)
    private val closeAnimation = getDrawable(R.drawable.vector_morph_cross)

    interface MenuCallback {
        fun toggleMenu()
    }

    override fun onActionUpOrCancel() {
        callback?.toggleMenu()
    }

    fun onMenuAction(open: Boolean) {
        if (open) {
            runAnimation(openAnimation, closeAnimation)
        } else {
            runAnimation(closeAnimation, openAnimation)
        }
    }

    private fun runAnimation(animationToStart: Drawable?, animationToStop: Drawable?) {
        if (animationToStop is AnimatedVectorDrawable) animationToStop.stop()
        setImageDrawable(animationToStart)
        if (animationToStart is AnimatedVectorDrawable) animationToStart.start()
    }
}
