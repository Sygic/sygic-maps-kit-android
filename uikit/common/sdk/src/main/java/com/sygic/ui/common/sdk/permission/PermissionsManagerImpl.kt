package com.sygic.ui.common.sdk.permission

import android.content.pm.PackageManager
import androidx.annotation.RestrictTo
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.sygic.ui.common.sdk.permission.livedata.PermissionCheckLiveEvent
import com.sygic.ui.common.sdk.permission.livedata.PermissionRationaleLiveEvent
import com.sygic.ui.common.sdk.permission.livedata.PermissionRequestLiveEvent

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
class PermissionsManagerImpl : PermissionsManager {

    private val permissionCheck: PermissionCheckLiveEvent = PermissionCheckLiveEvent()
    private val rationaleCheck: PermissionRationaleLiveEvent = PermissionRationaleLiveEvent()
    private val permissionRequest: PermissionRequestLiveEvent = PermissionRequestLiveEvent()

    override fun observe(owner: LifecycleOwner, observer: Observer<PermissionsManager.PermissionRequest>) {
        permissionCheck.observe(owner)
        rationaleCheck.observe(owner)
        permissionRequest.observe(owner, observer)
    }

    override fun checkPermissionGranted(permission: String, observer: Observer<Boolean>) {
        permissionCheck.check(permission, observer)
    }

    override fun shouldShowRationaleForPermission(permission: String, observer: Observer<Boolean>) {
        rationaleCheck.check(permission, observer)
    }

    override fun requestPermission(permission: String, callback: PermissionsManager.PermissionCallback) {
        requestPermissions(arrayOf(permission), object : PermissionsManager.PermissionsCallback {
            override fun onPermissionsGranted(grantedPermissions: List<String>) {
                callback.onPermissionGranted(grantedPermissions[0])
            }

            override fun onPermissionsDenied(deniedPermissions: List<String>) {
                callback.onPermissionDenied(deniedPermissions[0])
            }
        })
    }

    @Synchronized
    override fun requestPermissions(permissions: Array<String>, callback: PermissionsManager.PermissionsCallback) {
        permissionRequest.value = PermissionsManager.PermissionRequest(
            permissions,
            object : PermissionsManager.PermissionsRequesterCallback {
                override fun onRequestPermissionsResult(permissions: Array<String>, grantResults: IntArray) {
                    val granted = ArrayList<String>()
                    val denied = ArrayList<String>()
                    for (i in permissions.indices) {
                        if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                            granted.add(permissions[i])
                        } else {
                            denied.add(permissions[i])
                        }
                    }

                    if (!granted.isEmpty()) {
                        callback.onPermissionsGranted(granted)
                    }
                    if (!denied.isEmpty()) {
                        callback.onPermissionsDenied(denied)
                    }
                }
            })
    }
}
