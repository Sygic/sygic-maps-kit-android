package com.sygic.ui.view.positionlockfab

import android.content.Context
import android.content.res.ColorStateList
import com.google.android.material.floatingactionbutton.FloatingActionButton
import android.util.AttributeSet
import com.sygic.ui.common.extensions.getDataFromAttr

@Suppress("unused", "MemberVisibilityCanBePrivate")
class PositionLockFab @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.positionLockFabStyle,
    defStyleRes: Int = R.style.SygicPositionLockFabStyle
) : FloatingActionButton(context, attrs, defStyleAttr) {

    init {
        isClickable = true

        attrs?.let { attributeSet ->
            val typedArray = context.obtainStyledAttributes(attributeSet,
                R.styleable.PositionLockFab,
                defStyleAttr,
                defStyleRes
            )

            imageTintList = ColorStateList.valueOf(
                typedArray.getColor(
                    R.styleable.PositionLockFab_android_tint,
                    getDataFromAttr(android.R.attr.colorAccent)
                )
            )

            backgroundTintList = ColorStateList.valueOf(
                typedArray.getColor(
                    R.styleable.PositionLockFab_android_backgroundTint,
                    getDataFromAttr(android.R.attr.colorBackground)
                )
            )

            typedArray.recycle()
        }

        setState(LockState.UNLOCKED)
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