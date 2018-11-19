package com.sygic.ui.viewmodel.positionlockfab

import android.Manifest
import android.view.View
import androidx.lifecycle.*
import com.sygic.sdk.SygicEngine
import com.sygic.sdk.map.Camera
import com.sygic.sdk.position.PositionManager
import com.sygic.ui.common.sdk.DEFAULT_ANIMATION
import com.sygic.ui.common.sdk.location.EnableGpsResult
import com.sygic.ui.common.sdk.location.LocationManager
import com.sygic.ui.common.sdk.permission.PermissionsManager

private const val NORTH_UP = 0f

private const val ZOOM_LEVEL_PEDESTRIAN_ROTATE_MAP = 17f
private const val ZOOM_LEVEL_PEDESTRIAN_ROTATE_INDICATOR = 16f

class PositionLockFabViewModel(
    private val cameraModel: Camera.CameraModel,
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
            PositionManager.getInstance().startPositionUpdating() //ToDo: MS-4555
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

    fun onClick(view: View) {
        if (!canSetNextLockState(view)) {
            return
        }

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

    private fun setState(@LockState state: Int) {
        if (state != this.currentState.value) {
            this.currentState.value = state
        }
    }

    private fun setLockedMode() {
        PositionManager.getInstance().startPositionUpdating() //ToDo: MS-4555
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

    private fun canSetNextLockState(view: View): Boolean {
        if (!permissionsManager.hasPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION)) {
            permissionsManager.requestPermission(Manifest.permission.ACCESS_FINE_LOCATION, object : PermissionsManager.PermissionCallback {
                override fun onPermissionGranted(permission: String) {
                    SygicEngine.openGpsConnection()
                    onClick(view)
                }

                override fun onPermissionDenied(permission: String) {
                    /* Currently do nothing */
                }
            })
            return false
        }

        if (!locationManager.isGpsEnabled()) {
            locationManager.requestToEnableGps(object : LocationManager.EnableGpsCallback {
                override fun onResult(@EnableGpsResult result: Int) {
                    when (result) {
                        EnableGpsResult.ENABLED -> onClick(view)
                        EnableGpsResult.DENIED -> { /* Currently do nothing */ }
                    }
                }
            })
            return false
        }

        return true
    }

    override fun onStop(owner: LifecycleOwner) {
        cameraModel.addModeChangedListener(this)
        PositionManager.getInstance().stopPositionUpdating() //ToDo: MS-4555
    }

    class ViewModelFactory(
        private val cameraModel: Camera.CameraModel,
        private val locationManager: LocationManager,
        private val permissionsManager: PermissionsManager
    ) :
        ViewModelProvider.NewInstanceFactory() {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return PositionLockFabViewModel(cameraModel, locationManager, permissionsManager) as T
        }
    }
}