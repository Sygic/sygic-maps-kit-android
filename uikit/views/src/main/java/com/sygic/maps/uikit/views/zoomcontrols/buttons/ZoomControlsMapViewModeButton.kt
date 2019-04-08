package com.sygic.maps.uikit.views.zoomcontrols.buttons

import android.content.Context
import android.util.AttributeSet
import com.sygic.maps.uikit.views.R
import com.sygic.maps.uikit.views.zoomcontrols.TiltType

internal class ZoomControlsMapViewModeButton @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : BaseZoomControlsButton(context, attrs, defStyleAttr, iconDrawableRes = R.drawable.ic_3d) {

    override fun onActionUpOrCancel() {
        interactionListener?.onCameraProjectionChanged()
    }

    fun cameraProjectionChanged(@TiltType tiltType: Int) {
        when (tiltType) {
            TiltType.TILT_2D -> setImageResource(R.drawable.ic_3d)
            TiltType.TILT_3D -> setImageResource(R.drawable.ic_2d)
        }
    }
}
