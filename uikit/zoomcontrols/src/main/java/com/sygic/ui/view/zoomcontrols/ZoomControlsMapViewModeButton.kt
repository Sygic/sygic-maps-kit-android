package com.sygic.ui.view.zoomcontrols

import android.content.Context
import android.support.annotation.DrawableRes
import android.util.AttributeSet

internal class ZoomControlsMapViewModeButton @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : ZoomControlsBaseButton(context, attrs, defStyleAttr) {

    @DrawableRes
    override fun iconDrawableRes(): Int = R.drawable.ic_3d

    override fun onActionUpOrCancel() {
        interactionListener?.onCameraProjectionChanged()
    }

    fun cameraProjectionChanged(@TiltType tiltType: Int) {
        when (tiltType) {
            TiltType.TILT_2D -> setIconImage(R.drawable.ic_3d)
            TiltType.TILT_3D -> setIconImage(R.drawable.ic_2d)
        }
    }
}
