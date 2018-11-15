package com.sygic.ui.viewmodel.zoomcontrols

import android.graphics.PointF
import androidx.lifecycle.*
import com.sygic.sdk.map.Camera
import com.sygic.sdk.map.MapAnimation
import com.sygic.sdk.position.GeoCoordinates
import com.sygic.ui.common.sdk.DEFAULT_ANIMATION
import com.sygic.ui.common.sdk.ZOOM_ANIMATION
import com.sygic.ui.view.zoomcontrols.TiltType
import com.sygic.ui.view.zoomcontrols.ZoomControlsMenu
import com.sygic.ui.view.zoomcontrols.ZoomType
import kotlinx.coroutines.*
import kotlin.math.floor

private const val ZOOM_BASE_LINE = 1f
private const val ZOOM_FLOW_SCALE = 0.05f
private const val ZOOM_FLOW_TIME_STEP = 20L // ms

private const val TILT_2D = 0f
private const val TILT_3D = 70f

class ZoomControlsViewModel(
    private val cameraModel: Camera.CameraModel
) : ViewModel(), ZoomControlsMenu.InteractionListener, Camera.PositionChangedListener, DefaultLifecycleObserver {

    val tiltType: MutableLiveData<Int> = MutableLiveData()

    private var zoomFlowJob: Job? = null

    init {
        tiltType.value = getTiltType(cameraModel.tilt)
    }

    override fun onStart(owner: LifecycleOwner) {
        cameraModel.addPositionChangedListener(this)
    }

    override fun onStop(owner: LifecycleOwner) {
        cameraModel.removePositionChangedListener(this)
    }

    override fun onZoomInStart() {
        startZoom(ZoomType.IN)
    }

    override fun onZoomInStop() {
        stopZoom(ZoomType.IN)
    }

    override fun onZoomOutStart() {
        startZoom(ZoomType.OUT)
    }

    override fun onZoomOutStop() {
        stopZoom(ZoomType.OUT)
    }

    override fun onCameraProjectionChanged() {
        cameraModel.setTilt(if (tiltType.value == TiltType.TILT_2D) TILT_3D else TILT_2D, DEFAULT_ANIMATION)
    }

    override fun onPositionChangeCompleted() {
        // Do nothing
    }

    override fun onPositionChanged(geoCenter: GeoCoordinates, zoom: Float, rotation: Float, tilt: Float) {
        @TiltType val tiltType = getTiltType(tilt)
        if (this.tiltType.value != tiltType) {
            this.tiltType.value = tiltType
        }
    }

    private fun startZoom(@ZoomType zoomType: Int) {
        val zoomScale = when (zoomType) {
            ZoomType.IN -> ZOOM_BASE_LINE + ZOOM_FLOW_SCALE
            ZoomType.OUT -> ZOOM_BASE_LINE - ZOOM_FLOW_SCALE
            else -> throw IllegalArgumentException("Unknown zoom type: $zoomType") // should not happen
        }

        zoomFlowJob = GlobalScope.launch {
            while (true) {
                cameraModel.zoomBy(zoomScale, PointF(-1f, -1f), MapAnimation.NONE)
                delay(ZOOM_FLOW_TIME_STEP)
            }
        }
    }

    private fun stopZoom(@ZoomType zoomType: Int) {
        zoomFlowJob?.cancel()
        when (zoomType) {
            ZoomType.IN -> {
                cameraModel.setZoomLevel(cameraModel.zoomLevel + 1f, ZOOM_ANIMATION)
            }
            ZoomType.OUT -> {
                cameraModel.setZoomLevel(cameraModel.zoomLevel - 1f, ZOOM_ANIMATION)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        zoomFlowJob?.cancel()
    }

    @TiltType
    private fun getTiltType(tilt: Float): Int =
        if (floor(tilt) == floor(TILT_2D)) TiltType.TILT_2D else TiltType.TILT_3D

    class ViewModelFactory(private val cameraModel: Camera.CameraModel) :
        ViewModelProvider.NewInstanceFactory() {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return ZoomControlsViewModel(cameraModel) as T
        }
    }
}