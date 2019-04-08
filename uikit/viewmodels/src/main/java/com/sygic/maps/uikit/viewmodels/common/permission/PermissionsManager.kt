package com.sygic.maps.uikit.viewmodels.common.permission

import androidx.annotation.RestrictTo
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer

const val PERMISSIONS_REQUEST_CODE = 777

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
interface PermissionsManager {

    interface PermissionsRequesterCallback {
        fun onRequestPermissionsResult(permissions: Array<String>, grantResults: IntArray)
    }

    interface PermissionCallback {
        fun onPermissionGranted(permission: String)
        fun onPermissionDenied(permission: String)
    }

    interface PermissionsCallback {
        fun onPermissionsGranted(grantedPermissions: List<String>)
        fun onPermissionsDenied(deniedPermissions: List<String>)
    }

    fun checkPermissionGranted(permission: String, observer: Observer<Boolean>)
    fun shouldShowRationaleForPermission(permission: String, observer: Observer<Boolean>)

    fun requestPermission(permission: String, callback: PermissionCallback)
    fun requestPermissions(permissions: Array<String>, callback: PermissionsCallback)

    fun observe(owner: LifecycleOwner, observer: Observer<PermissionRequest>)

    data class PermissionRequest(val permissions: Array<String>, val callback: PermissionsRequesterCallback) {

        //generated to safe check content of Array in data class
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as PermissionRequest

            if (!permissions.contentEquals(other.permissions)) return false
            if (callback != other.callback) return false

            return true
        }

        override fun hashCode(): Int {
            var result = permissions.contentHashCode()
            result = 31 * result + callback.hashCode()
            return result
        }
    }
}
