package com.sygic.modules.common.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.sygic.modules.common.utils.DEFAULT_ANIMATION
import com.sygic.sdk.map.Camera
import com.sygic.sdk.position.GeoCoordinates

private const val NORTH_UP = 0f

class CompassViewModel(
    application: Application,
    private val cameraModel: Camera.CameraModel
) : AndroidViewModel(application), Camera.PositionChangedListener, DefaultLifecycleObserver {

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

    class ViewModelFactory(private val application: Application, private val cameraModel: Camera.CameraModel) :
        ViewModelProvider.NewInstanceFactory() {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return CompassViewModel(application, cameraModel) as T
        }
    }
}