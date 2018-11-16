package com.sygic.ui.view.positionlockfab

import android.content.Context
import com.google.android.material.floatingactionbutton.FloatingActionButton
import android.util.AttributeSet

@Suppress("unused", "MemberVisibilityCanBePrivate")
class LockActionFloatingButton : FloatingActionButton {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        setState(LockState.UNLOCKED)
        isClickable = true
    }

    /**
     * Set the visual state of the button
     *
     * Supported values are [LockState.UNLOCKED], [LockState.LOCKED], [LockState.LOCKED_AUTOROTATE]
     *
     * @param state one of the supported states
     */
    fun setState(@LockState state: Int) {
        when (state) {
            LockState.UNLOCKED -> setImageResource(R.drawable.ic_map_lock_empty)
            LockState.LOCKED -> setImageResource(R.drawable.ic_map_lock_full)
            LockState.LOCKED_AUTOROTATE -> setImageResource(R.drawable.ic_map_lock_rotate)
        }
    }
}