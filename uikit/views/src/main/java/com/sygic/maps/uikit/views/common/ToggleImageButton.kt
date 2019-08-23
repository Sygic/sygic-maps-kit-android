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
import android.os.Parcelable
import android.util.AttributeSet
import android.view.SoundEffectConstants
import android.widget.CheckBox
import android.widget.Checkable
import androidx.appcompat.widget.AppCompatImageButton
import kotlinx.android.parcel.Parcelize

private val CHECKED_STATE_SET = intArrayOf(android.R.attr.state_checked)

/**
 * A [ToggleImageButton] is an extended variant of [AppCompatImageButton] and can be used as an replacement of the
 * [CheckBox] button. Unlike the [CheckBox] this class perfectly fit for vector icons transitions.
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
open class ToggleImageButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageButton(context, attrs, defStyleAttr), Checkable {

    private var checked: Boolean = false
    private var broadcasting: Boolean = false

    var onCheckedChangedListener: OnCheckedChangedListener? = null

    interface OnCheckedChangedListener {
        fun onCheckedChanged(buttonView: ToggleImageButton, isChecked: Boolean)
    }

    override fun onSaveInstanceState(): Parcelable? {
        return SavedState(super.onSaveInstanceState(), checked)
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        if (state !is SavedState) {
            super.onRestoreInstanceState(state)
            return
        }

        super.onRestoreInstanceState(state.superState)
        checked = state.checked
    }

    override fun performClick(): Boolean {
        toggle()

        val handled = super.performClick()
        if (!handled) {
            // View class only makes a sound effect if the onClickListener was
            // called, so we'll need to make one here instead.
            playSoundEffect(SoundEffectConstants.CLICK)
        }

        return handled
    }

    override fun onCreateDrawableState(extraSpace: Int): IntArray {
        val drawableState = super.onCreateDrawableState(extraSpace + 1)
        if (isChecked) {
            mergeDrawableStates(drawableState, CHECKED_STATE_SET)
        }
        return drawableState
    }

    override fun setChecked(checked: Boolean) {
        if (this.checked != checked) {
            this.checked = checked
            refreshDrawableState()

            // Avoid infinite recursions if setChecked() is called from a listener
            if (broadcasting) {
                return
            }

            broadcasting = true
            if (onCheckedChangedListener != null) {
                onCheckedChangedListener!!.onCheckedChanged(this, this.checked)
            }

            broadcasting = false
        }
    }

    override fun isChecked(): Boolean = checked

    override fun toggle() {
        isChecked = !checked
    }

    @Parcelize
    @Suppress("CanBeParameter")
    private class SavedState(val state: Parcelable?, var checked: Boolean) : BaseSavedState(state)
}