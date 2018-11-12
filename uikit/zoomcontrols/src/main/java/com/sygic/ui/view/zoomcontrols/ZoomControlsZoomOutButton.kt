package com.sygic.ui.view.zoomcontrols

import android.content.Context
import android.support.annotation.DrawableRes
import android.util.AttributeSet

internal class ZoomControlsZoomOutButton @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : ZoomControlsBaseButton(context, attrs, defStyleAttr) {

    @DrawableRes
    override fun iconDrawableRes(): Int = R.drawable.ic_minus

    override fun onActionDown() {
        super.onActionDown()
        interactionListener?.onZoomOutStart()
    }

    override fun onActionUpOrCancel() {
        interactionListener?.onZoomOutStop()
    }
}
