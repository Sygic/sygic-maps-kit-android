package com.sygic.ui.view.zoomcontrols

import android.content.Context
import androidx.annotation.DrawableRes
import android.util.AttributeSet

internal class ZoomControlsZoomInButton @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : ZoomControlsBaseButton(context, attrs, defStyleAttr) {

    @DrawableRes
    override fun iconDrawableRes(): Int = R.drawable.ic_plus

    override fun onActionDown() {
        super.onActionDown()
        interactionListener?.onZoomInStart()
    }

    override fun onActionUpOrCancel() {
        interactionListener?.onZoomInStop()
    }
}
