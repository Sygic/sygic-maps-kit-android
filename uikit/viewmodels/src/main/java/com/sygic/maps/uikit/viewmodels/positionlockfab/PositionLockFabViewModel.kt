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

package com.sygic.maps.uikit.viewmodels.positionlockfab

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sygic.maps.tools.annotations.AutoFactory
import com.sygic.sdk.map.Camera
import com.sygic.maps.uikit.viewmodels.common.sdk.DEFAULT_ANIMATION
import com.sygic.maps.uikit.viewmodels.common.location.LocationManager
import com.sygic.maps.uikit.viewmodels.common.sdk.model.ExtendedCameraModel
import com.sygic.maps.uikit.viewmodels.common.permission.PermissionsManager
import com.sygic.maps.uikit.viewmodels.common.utils.requestLocationAccess
import com.sygic.maps.uikit.views.positionlockfab.LockState
import com.sygic.maps.uikit.views.positionlockfab.PositionLockFab

private const val NORTH_UP = 0f

private const val ZOOM_LEVEL_PEDESTRIAN_ROTATE_MAP = 17f
private const val ZOOM_LEVEL_PEDESTRIAN_ROTATE_INDICATOR = 16f

/**
 * A [PositionLockFabViewModel] is a basic ViewModel implementation for the [PositionLockFab] class. It listens to the Sygic SDK
 * [Camera.ModeChangedListener] and set appropriate state to the [PositionLockFab] view. It also sets the [LockState.UNLOCKED]
 * as default.
 */
@AutoFactory
@Suppress("unused", "MemberVisibilityCanBePrivate")
open class PositionLockFabViewModel internal constructor(
    private val cameraModel: ExtendedCameraModel,
    private val locationManager: LocationManager,
    private val permissionsManager: PermissionsManager
) :
    ViewModel(),
    Camera.ModeChangedListener,
    DefaultLifecycleObserver {

    val currentState: MutableLiveData<Int> = MutableLiveData()

    init {
        setState(LockState.UNLOCKED)
    }

    override fun onStart(owner: LifecycleOwner) {
        cameraModel.addModeChangedListener(this)
        if (currentState.value == LockState.LOCKED) {
            locationManager.setSdkPositionUpdatingEnabled(true)
        }
    }

    override fun onRotationModeChanged(@Camera.RotationMode mode: Int) {
        modeChanged()
    }

    override fun onMovementModeChanged(@Camera.MovementMode mode: Int) {
        modeChanged()
    }

    private fun modeChanged() {
        when {
            cameraModel.movementMode == Camera.MovementMode.Free -> setState(LockState.UNLOCKED)
            cameraModel.rotationMode == Camera.RotationMode.Attitude -> setState(LockState.LOCKED_AUTOROTATE)
            else -> setState(LockState.LOCKED)
        }
    }

    open fun onClick() {
        requestLocationAccess(permissionsManager, locationManager) {
            when (currentState.value) {
                LockState.UNLOCKED -> {
                    setState(LockState.LOCKED)
                    setLockedMode()
                    setZoom(ZOOM_LEVEL_PEDESTRIAN_ROTATE_INDICATOR)
                }
                LockState.LOCKED_AUTOROTATE -> {
                    setState(LockState.LOCKED)
                    setLockedMode()
                    setZoom(ZOOM_LEVEL_PEDESTRIAN_ROTATE_INDICATOR)
                    cameraModel.setRotation(NORTH_UP, DEFAULT_ANIMATION)
                }
                LockState.LOCKED -> {
                    setState(LockState.LOCKED_AUTOROTATE)
                    setAutoRotateMode()
                    setZoom(ZOOM_LEVEL_PEDESTRIAN_ROTATE_MAP)
                }
            }
        }
    }

    private fun setState(@LockState state: Int) {
        if (currentState.value != state) {
            currentState.value = state
        }
    }

    protected fun setLockedMode() {
        locationManager.positionOnMapEnabled = true
        cameraModel.movementMode = Camera.MovementMode.FollowGpsPosition
        cameraModel.rotationMode = Camera.RotationMode.Free
    }

    protected fun setAutoRotateMode() {
        cameraModel.movementMode = Camera.MovementMode.FollowGpsPosition
        cameraModel.rotationMode = Camera.RotationMode.Attitude
    }

    protected fun setZoom(zoomLevel: Float) {
        cameraModel.zoomLevel = zoomLevel
    }

    override fun onStop(owner: LifecycleOwner) {
        cameraModel.removeModeChangedListener(this)
        locationManager.setSdkPositionUpdatingEnabled(false)
    }
}