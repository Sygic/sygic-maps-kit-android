package com.sygic.maps.uikit.views.zoomcontrols.buttons

import android.content.Context
import android.util.AttributeSet
import com.sygic.maps.uikit.views.R

internal class ZoomControlsZoomInButton @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : BaseZoomControlsButton(context, attrs, defStyleAttr, iconDrawableRes = R.drawable.ic_plus) {

    override fun onActionDown() {
        interactionListener?.onZoomInStart()
    }

    override fun onActionUpOrCancel() {
        interactionListener?.onZoomInStop()
    }
}
