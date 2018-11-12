package com.sygic.ui.view.zoomcontrols

import android.content.Context
import android.support.annotation.DrawableRes
import android.util.AttributeSet

import com.sygic.ui.common.*

internal class ZoomControlsMenuButton @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, private val callback: MenuCallback? = null)
    : ZoomControlsBaseButton(context, attrs, defStyleAttr) {

    companion object {
        private const val ANIMATION_DURATION = 300
    }

    @DrawableRes
    override fun iconDrawableRes(): Int = R.drawable.ic_plus_minus

    interface MenuCallback {
        fun toggleMenu()
    }

    override fun onActionUpOrCancel() {
        callback?.toggleMenu()
    }

    fun onMenuAction(open: Boolean) {
        val drawableRes = if (open) R.drawable.ic_cross else R.drawable.ic_plus_minus
        setImageDrawableWithFade(getVectorDrawableWithTint(drawableRes, iconColor), ANIMATION_DURATION)
    }
}
