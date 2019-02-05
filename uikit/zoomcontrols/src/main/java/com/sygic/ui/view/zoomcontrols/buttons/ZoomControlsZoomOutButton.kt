package com.sygic.ui.view.zoomcontrols.buttons

import android.content.Context
import android.util.AttributeSet
import com.sygic.ui.view.zoomcontrols.R

internal class ZoomControlsZoomOutButton @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : BaseZoomControlsButton(context, attrs, defStyleAttr, iconDrawableRes = R.drawable.ic_minus) {

    override fun onActionDown() {
        interactionListener?.onZoomOutStart()
    }

    override fun onActionUpOrCancel() {
        interactionListener?.onZoomOutStop()
    }
}
