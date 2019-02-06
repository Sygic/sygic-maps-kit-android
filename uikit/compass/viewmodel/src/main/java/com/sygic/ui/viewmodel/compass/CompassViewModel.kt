package com.sygic.ui.viewmodel.compass

import android.view.View
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sygic.sdk.map.Camera
import com.sygic.sdk.position.GeoCoordinates
import com.sygic.tools.annotations.AutoFactory
import com.sygic.ui.common.sdk.DEFAULT_ANIMATION
import com.sygic.ui.common.sdk.model.ExtendedCameraModel
import com.sygic.ui.view.compass.CompassView

private const val NORTH_UP = 0f

/**
 * A [CompassViewModel] is a basic ViewModel implementation for the [CompassView] class. It listens to the Sygic SDK
 * [Camera.PositionChangedListener] and set appropriate rotation to the [CompassView] needle. It also sets the
 * north (default) rotation when [View.OnClickListener] click method is called.
 */
@AutoFactory
open class CompassViewModel internal constructor(
    private val cameraModel: ExtendedCameraModel
) : ViewModel(), Camera.PositionChangedListener, DefaultLifecycleObserver {

    val rotation: MutableLiveData<Float> = MutableLiveData()

    init {
        rotation.value = cameraModel.rotation
    }

    override fun onStart(owner: LifecycleOwner) {
        cameraModel.addPositionChangedListener(this)
    }

    override fun onStop(owner: LifecycleOwner) {
        cameraModel.removePositionChangedListener(this)
    }

    fun onClick() {
        cameraModel.setRotation(NORTH_UP, DEFAULT_ANIMATION)
    }

    override fun onPositionChanged(geoCenter: GeoCoordinates, zoom: Float, rotation: Float, tilt: Float) {
        this.rotation.value = rotation
    }

    override fun onPositionChangeCompleted() {
        // Do nothing
    }
}