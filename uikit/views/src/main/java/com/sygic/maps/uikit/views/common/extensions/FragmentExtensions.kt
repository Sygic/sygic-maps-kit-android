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
import androidx.annotation.RestrictTo
import androidx.fragment.app.Fragment
import com.sygic.maps.uikit.views.R
import com.sygic.maps.uikit.views.common.components.FragmentComponent
import com.sygic.maps.uikit.views.common.toast.InfoToastComponent
import com.sygic.maps.uikit.views.common.utils.logWarning

fun Fragment.showKeyboard(view: View) = context?.showKeyboard(view)
fun Fragment.toggleKeyboard() = context?.toggleKeyboard()
fun Fragment.hideKeyboard() = view?.let { context?.hideKeyboard(it) }
fun Fragment.showInfoToast(infoToastComponent: InfoToastComponent, isLong: Boolean = false) = context?.showInfoToast(infoToastComponent, isLong)

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
fun Fragment.openFragment(component: FragmentComponent) {
    if (isInLayout) {
        internalContainerId?.let {
            fragmentManager?.beginTransaction()?.add(it, component.fragment, component.fragmentTag)?.addToBackStack(null)?.commit()
        }
        logWarning("Fragment that is statically placed in an XML layout file cannot be replaced, the addition is used instead (performance or other issues may occur).")
    } else {
        containerId?.let { fragmentManager?.beginTransaction()?.replace(it, component.fragment, component.fragmentTag)?.addToBackStack(null)?.commit() }
    }
}

fun Fragment.finish() = requireActivity().finish()

private inline val Fragment.containerId: Int?
    @IdRes
    get() = view?.let { it.parent.let { parent -> if (parent is ViewGroup) parent.id else null } }

private inline val Fragment.internalContainerId: Int?
    @IdRes
    get() = view?.let { view ->
        view.parent?.let { parent ->
            if (parent is ViewGroup) {
                val internalFragmentContainerId = R.id.internalFragmentContainer
                if (parent.childCount == 1) {
                    return if (parent.id != View.NO_ID) {
                        parent.id
                    } else {
                        internalFragmentContainerId.also { parent.id = it }
                    }
                }

                return parent.findViewById<FrameLayout>(internalFragmentContainerId)?.id ?: run {
                    val sygicFragmentContainer = FrameLayout(requireContext()).apply {
                        id = internalFragmentContainerId
                        layoutParams = FrameLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                    }
                    parent.addView(sygicFragmentContainer, parent.indexOfChild(view) + 1)
                    sygicFragmentContainer.id
                }
            }

            return null
        }
    }
