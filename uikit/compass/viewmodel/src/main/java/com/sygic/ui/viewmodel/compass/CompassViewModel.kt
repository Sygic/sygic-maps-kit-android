package com.sygic.ui.viewmodel.compass

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sygic.sdk.map.Camera
import com.sygic.sdk.position.GeoCoordinates
import com.sygic.tools.annotations.AutoFactory
import com.sygic.ui.common.sdk.DEFAULT_ANIMATION
import com.sygic.ui.common.sdk.model.ExtendedCameraModel

private const val NORTH_UP = 0f

@AutoFactory
class CompassViewModel internal constructor(
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