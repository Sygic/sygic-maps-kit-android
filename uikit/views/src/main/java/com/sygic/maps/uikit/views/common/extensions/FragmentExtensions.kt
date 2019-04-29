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

package com.sygic.maps.uikit.views.common.extensions

import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import com.sygic.maps.uikit.views.R

fun Fragment.showKeyboard() = context?.showKeyboard()
fun Fragment.hideKeyboard() = view?.let { context?.hideKeyboard(it) }

@IdRes
fun Fragment.getSygicFragmentContainerId(): Int {
    view?.let { view ->
        view.parent?.let { parent ->
            if (parent is ViewGroup) {
                @IdRes
                val sygicFragmentContainerId = R.id.sygicFragmentContainer

                if (parent.childCount == 1) {
                    if (parent.id != View.NO_ID) {
                        return parent.id
                    }

                    parent.id = sygicFragmentContainerId
                    return parent.id
                }

                parent.findViewById<FrameLayout>(sygicFragmentContainerId)?.let {
                    return it.id
                } ?: run {
                    val sygicFragmentContainer = FrameLayout(requireContext()).apply {
                        id = sygicFragmentContainerId
                        layoutParams = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                    }
                    parent.addView(sygicFragmentContainer, parent.indexOfChild(view) + 1)
                    return sygicFragmentContainer.id
                }
            }
        }
    }

    return View.NO_ID
}