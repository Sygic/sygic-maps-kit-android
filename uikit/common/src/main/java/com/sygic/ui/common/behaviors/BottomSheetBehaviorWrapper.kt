package com.sygic.ui.common.behaviors

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
