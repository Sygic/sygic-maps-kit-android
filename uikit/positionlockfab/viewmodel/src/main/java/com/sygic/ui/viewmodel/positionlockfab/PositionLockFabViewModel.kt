package com.sygic.ui.viewmodel.positionlockfab

import android.view.View
import androidx.lifecycle.*
import com.sygic.sdk.SygicEngine
import com.sygic.sdk.map.Camera
import com.sygic.ui.common.sdk.DEFAULT_ANIMATION
import com.sygic.ui.common.sdk.location.EnableGpsResult
import com.sygic.ui.common.sdk.location.LocationManager

private const val NORTH_UP = 0f

private const val ZOOM_LEVEL_PEDESTRIAN_ROTATE_MAP = 17f
private const val ZOOM_LEVEL_PEDESTRIAN_ROTATE_INDICATOR = 16f

class PositionLockFabViewModel(private val cameraModel: Camera.CameraModel,
                               private val locationManager: LocationManager
) :
    ViewModel(),
    Camera.ModeChangedListener,
    DefaultLifecycleObserver {

    @LockState
    val state: MutableLiveData<Int> = MutableLiveData()

    init {
        SygicEngine.openGpsConnection() //todo
        state.value = LockState.UNLOCKED
    }

    override fun onStart(owner: LifecycleOwner) {
        cameraModel.addModeChangedListener(this)
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

        when (state.value) {
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
        if (state != this.state.value) {
            this.state.value = state
        }
    }

    private fun setLockedMode() {
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
        /*if (!permissionsManager.hasPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION)) {
            requestLocationPermission(object : PermissionsManager.PermissionCallback() {
                fun onPermissionGranted(permission: String) {
                    gpsConnectionSignal.onNext(RxUtils.Notification.INSTANCE)
                    onClick(view)
                }

                fun onPermissionDenied(deniedPermission: String) {
                    permissionDeniedNotificationSignal.onNext(
                        Components.PermissionDeniedSnackBarComponent(deniedPermission,
                            { requestLocationPermission(this) })
                    )
                }
            })
            return false
        }*/

        if (!locationManager.isGpsEnabled()) {
            locationManager.requestToEnableGps(object : LocationManager.EnableGpsCallback {
                override fun onResult(@EnableGpsResult result: Int) {
                    when (result) {
                        EnableGpsResult.ENABLED -> onClick(view)
                        EnableGpsResult.DENIED -> { /*Do nothing*/ }
                    }
                }
            })
            return false
        }

        return true
    }

    /*private fun requestLocationPermission(callback: PermissionsManager.PermissionCallback) {
        permissionsManager.requestPermission(Manifest.permission.ACCESS_FINE_LOCATION, callback)
    }

    fun gpsConnection(): Observable<RxUtils.Notification> {
        return gpsConnectionSignal
    }

    fun enableGpsSnackbar(): Observable<Components.EnableGpsSnackBarComponent> {
        return enableGpsSnackbarSignal
    }

    fun permissionDeniedNotification(): Observable<Components.PermissionDeniedSnackBarComponent> {
        return permissionDeniedNotificationSignal
    }*/

    override fun onStop(owner: LifecycleOwner) {
        cameraModel.addModeChangedListener(this)
    }

    class ViewModelFactory(private val cameraModel: Camera.CameraModel,
                           private val locationManager: LocationManager) :
        ViewModelProvider.NewInstanceFactory() {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return PositionLockFabViewModel(cameraModel, locationManager) as T
        }
    }
}