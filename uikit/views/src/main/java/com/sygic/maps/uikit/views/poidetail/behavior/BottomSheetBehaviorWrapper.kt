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

package com.sygic.maps.uikit.views.poidetail.behavior

import android.view.View
import androidx.annotation.RestrictTo
import com.google.android.material.bottomsheet.BottomSheetBehavior
import java.lang.ref.WeakReference
import java.util.*

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
class BottomSheetBehaviorWrapper(private val behavior: BottomSheetBehavior<View>) {

    interface StateListener {
        fun onStateChanged(@BottomSheetBehavior.State newState: Int)
    }

    interface SlideListener {
        fun onSlide(slideOffset: Float)
    }

    @BottomSheetBehavior.State
    var state: Int
        get() = behavior.state
        set(value) {
            behavior.state = value
        }

    var isHideable: Boolean
        get() = behavior.isHideable
        set(value) {
            behavior.isHideable = value
        }

    var peekHeight: Int
        get() = behavior.peekHeight
        set(value) {
            behavior.peekHeight = value
        }

    private val stateListeners = LinkedHashSet<WeakReference<StateListener>>()
    private val slideListeners = LinkedHashSet<WeakReference<SlideListener>>()

    init {
        behavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                notifyStateChanged(newState)
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                notifySlideChanged(slideOffset)
            }
        })
    }

    fun notifyStateChanged(state: Int) {
        stateListeners.iterator().apply {
            while (hasNext()) {
                next().get()?.onStateChanged(state) ?: remove()
            }
        }
    }

    fun notifySlideChanged(slideOffset: Float) {
        slideListeners.iterator().apply {
            while (hasNext()) {
                next().get()?.onSlide(slideOffset) ?: remove()
            }
        }
    }

    fun addStateListener(listener: StateListener) {
        stateListeners.add(WeakReference(listener))
    }

    fun addSlideListener(listener: SlideListener) {
        slideListeners.add(WeakReference(listener))
    }
}
