package com.sygic.ui.common.behaviors

import android.view.View
import android.widget.FrameLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior

class BottomSheetBehaviorWrapper(private val behavior: BottomSheetBehavior<FrameLayout>) {

    interface StateListener {
        fun onStateChanged(bottomSheet: View, newState: Int)
    }

    interface SlideListener {
        fun onSlide(bottomSheet: View, slideOffset: Float)
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

    private val stateListeners = LinkedHashSet<StateListener>()
    private val slideListeners = LinkedHashSet<SlideListener>()

    init {
        behavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                stateListeners.forEach { it.onStateChanged(bottomSheet, newState) }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                slideListeners.forEach { it.onSlide(bottomSheet, slideOffset) }
            }
        })
    }

    fun addStateListener(listener: StateListener) {
        stateListeners.add(listener)
    }

    fun removeStateListener(listener: StateListener) {
        stateListeners.remove(listener)
    }

    fun addSlideListener(listener: SlideListener) {
        slideListeners.add(listener)
    }

    fun removeSlideListener(listener: SlideListener) {
        slideListeners.remove(listener)
    }
}
