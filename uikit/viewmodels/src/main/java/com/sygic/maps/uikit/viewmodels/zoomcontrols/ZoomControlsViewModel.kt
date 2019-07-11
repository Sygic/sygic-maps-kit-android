/*
 * Copyright (c) 2019 Sygic a.s. All rights reserved.
 *
 * This project is licensed under the MIT License.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.sygic.maps.uikit.viewmodels.zoomcontrols

import android.graphics.PointF
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sygic.maps.tools.annotations.AutoFactory
import com.sygic.sdk.map.Camera
import com.sygic.sdk.map.MapAnimation
import com.sygic.sdk.position.GeoCoordinates
import com.sygic.maps.uikit.viewmodels.common.sdk.DEFAULT_ANIMATION
import com.sygic.maps.uikit.viewmodels.common.sdk.ZOOM_ANIMATION
import com.sygic.maps.uikit.viewmodels.common.sdk.model.ExtendedCameraModel
import com.sygic.maps.uikit.views.zoomcontrols.TiltType
import com.sygic.maps.uikit.views.zoomcontrols.ZoomControlsMenu
import com.sygic.maps.uikit.views.zoomcontrols.ZoomType
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.floor

private const val DEFAULT_ZOOM_BASE_LINE = 1f
private const val DEFAULT_ZOOM_FLOW_SCALE = 0.05f
private const val DEFAULT_ZOOM_FLOW_TIME_STEP = 20L

private const val DEFAULT_TILT_2D = 0f
private const val DEFAULT_TILT_3D = 70f

/**
 * A [ZoomControlsViewModel] is a basic ViewModel implementation for the [ZoomControlsMenu] class. It listens to the Sygic SDK
 * [Camera.PositionChangedListener] and set appropriate state to the [ZoomControlsMenu]. It also sets the actual [tiltType]
 * from Sygic SDK [Camera.CameraModel] to the [ZoomControlsMenu] during initialization.
 *
 * It listens also to the [ZoomControlsMenu.InteractionListener] and sets the appropriate states defined by the internal
 * business logic to the Sygic SDK [Camera.CameraModel]. For example, when the [onZoomInStart] method is triggered, then
 * "auto zoom" flow is started (which repeatedly call [Camera.zoomBy] method, every 20ms by default) until the [onZoomInStop]
 * is called (the [onZoomInStop] method invoke [Camera.CameraModel.setZoomLevel] method with one level higher than current).
 */
@AutoFactory
@Suppress("unused", "MemberVisibilityCanBePrivate")
open class ZoomControlsViewModel internal constructor(
    private val cameraModel: ExtendedCameraModel
) : ViewModel(), ZoomControlsMenu.InteractionListener, Camera.PositionChangedListener, DefaultLifecycleObserver {

    val tiltType: MutableLiveData<Int> = MutableLiveData(getTiltType(cameraModel.tilt))

    /**
     * A *[zoomBaseLine]* defines the zoom base line value. The default value is [DEFAULT_ZOOM_BASE_LINE].
     *
     * @param [Float] zoom base line value.
     *
     * @return the zoom base line value.
     */
    var zoomBaseLine: Float = DEFAULT_ZOOM_BASE_LINE
        protected set

    /**
     * A *[zoomFlowScale]* defines the zoom flow scale value. The default value is [DEFAULT_ZOOM_FLOW_SCALE].
     *
     * @param [Float] zoom flow scale value.
     *
     * @return the zoom flow scale value.
     */
    var zoomFlowScale: Float = DEFAULT_ZOOM_FLOW_SCALE
        protected set

    /**
     * A *[zoomFlowTimeStep]* defines the zoom flow time step value used in the [zoomFlowJob] as delay. The default
     * value is [DEFAULT_ZOOM_FLOW_TIME_STEP].
     *
     * @param [Long] zoom flow time step value.
     *
     * @return zoom flow time step value.
     */
    var zoomFlowTimeStep: Long = DEFAULT_ZOOM_FLOW_TIME_STEP
        protected set

    /**
     * A *[tilt2d]* defines value, that is considered to be the tilt 2D boundary. The default value is [DEFAULT_TILT_2D].
     *
     * @param [Float] tilt 2D value.
     *
     * @return tilt 2D value.
     */
    var tilt2d: Float = DEFAULT_TILT_2D
        protected set

    /**
     * A *[tilt3d]* defines value, that is considered to be the tilt 3D boundary. The default value is [DEFAULT_TILT_3D].
     *
     * @param [Float] tilt 3D value.
     *
     * @return tilt 3D value.
     */
    var tilt3d: Float = DEFAULT_TILT_3D
        protected set

    private var zoomFlowJob: Job? = null

    override fun onStart(owner: LifecycleOwner) {
        cameraModel.addPositionChangedListener(this)
    }

    override fun onStop(owner: LifecycleOwner) {
        cameraModel.removePositionChangedListener(this)
    }

    override fun onMenuOpened(opened: Boolean) {
        // Do nothing
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
        cameraModel.setTilt(if (tiltType.value == TiltType.TILT_2D) tilt3d else tilt2d, DEFAULT_ANIMATION)
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
            ZoomType.IN -> zoomBaseLine + zoomFlowScale
            ZoomType.OUT -> zoomBaseLine - zoomFlowScale
            else -> throw IllegalArgumentException("Unknown zoom type: $zoomType") // should not happen
        }

        zoomFlowJob = GlobalScope.launch {
            while (true) {
                cameraModel.zoomBy(zoomScale, PointF(-1f, -1f), MapAnimation.NONE)
                delay(zoomFlowTimeStep)
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

    @TiltType
    private fun getTiltType(tilt: Float): Int =
        if (floor(tilt) == floor(tilt2d)) TiltType.TILT_2D else TiltType.TILT_3D

    override fun onCleared() {
        super.onCleared()
        zoomFlowJob?.cancel()
    }
}