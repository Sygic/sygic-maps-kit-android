package com.sygic.ui.common.sdk.permission

const val PERMISSIONS_REQUEST_CODE = 777

interface PermissionsManager {

    interface PermissionsRequester {
        fun hasPermissionGranted(permission: String): Boolean
        fun shouldShowRationaleForPermission(permission: String): Boolean
        fun requestPermissions(permissions: Array<String>, permissionsRequesterCallback: PermissionsRequesterCallback)
    }

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

    fun hasPermissionGranted(permission: String): Boolean
    fun shouldShowRationaleForPermission(permission: String): Boolean

    fun requestPermission(permission: String, callback: PermissionCallback)
    fun requestPermissions(permissions: Array<String>, callback: PermissionsCallback)
}
