package com.sygic.ui.viewmodel.positionlockfab

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sygic.sdk.map.Camera
import com.sygic.tools.annotations.AutoFactory
import com.sygic.ui.common.sdk.DEFAULT_ANIMATION
import com.sygic.ui.common.sdk.location.LocationManager
import com.sygic.ui.common.sdk.model.ExtendedCameraModel
import com.sygic.ui.common.sdk.permission.PermissionsManager
import com.sygic.ui.common.sdk.utils.requestLocationAccess

private const val NORTH_UP = 0f

private const val ZOOM_LEVEL_PEDESTRIAN_ROTATE_MAP = 17f
private const val ZOOM_LEVEL_PEDESTRIAN_ROTATE_INDICATOR = 16f

@AutoFactory
class PositionLockFabViewModel internal constructor(
    private val cameraModel: ExtendedCameraModel,
    private val locationManager: LocationManager,
    private val permissionsManager: PermissionsManager
) :
    ViewModel(),
    Camera.ModeChangedListener,
    DefaultLifecycleObserver {

    @LockState
    val currentState: MutableLiveData<Int> = MutableLiveData()

    init {
        currentState.value = LockState.UNLOCKED
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

    fun onClick() {
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
        if (state != this.currentState.value) {
            this.currentState.value = state
        }
    }

    private fun setLockedMode() {
        locationManager.positionOnMapEnabled = true
        cameraModel.movementMode = Camera.MovementMode.FollowGpsPosition
        cameraModel.rotationMode = Camera.RotationMode.Free
    }

    private fun setAutoRotateMode() {
        cameraModel.movementMode = Camera.MovementMode.FollowGpsPosition
        cameraModel.rotationMode = Camera.RotationMode.Attitude
    }

    private fun setZoom(zoomLevel: Float) {
        cameraModel.zoomLevel = zoomLevel
    }

    override fun onStop(owner: LifecycleOwner) {
        cameraModel.removeModeChangedListener(this)
        locationManager.setSdkPositionUpdatingEnabled(false)
    }
}