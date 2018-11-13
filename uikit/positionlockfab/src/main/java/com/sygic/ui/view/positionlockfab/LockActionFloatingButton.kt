package com.sygic.ui.view.positionlockfab

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.design.widget.FloatingActionButton
import android.util.AttributeSet
import com.sygic.ui.common.getVectorDrawable

@Suppress("unused", "MemberVisibilityCanBePrivate")
class LockActionFloatingButton : FloatingActionButton {

    private val lockedDrawable: Drawable = getVectorDrawable(R.drawable.ic_map_lock_full)
    private val unlockedDrawable: Drawable = getVectorDrawable(R.drawable.ic_map_lock_empty)
    private val lockedCompassDrawable: Drawable = getVectorDrawable(R.drawable.ic_map_lock_rotate)

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
            LockState.UNLOCKED -> setImageDrawable(unlockedDrawable)
            LockState.LOCKED -> setImageDrawable(lockedDrawable)
            LockState.LOCKED_AUTOROTATE -> setImageDrawable(lockedCompassDrawable)
        }
    }
}