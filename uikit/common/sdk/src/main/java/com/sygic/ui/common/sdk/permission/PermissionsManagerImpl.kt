package com.sygic.ui.common.sdk.permission

import android.content.pm.PackageManager
import androidx.annotation.RestrictTo
import java.lang.ref.WeakReference
import java.util.*

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
class PermissionsManagerImpl(permissionsRequester: PermissionsManager.PermissionsRequester) : PermissionsManager {

    private var permissionRequesterWeakReference: WeakReference<PermissionsManager.PermissionsRequester> =
        WeakReference(permissionsRequester)

    override fun hasPermissionGranted(permission: String): Boolean {
        return permissionRequesterWeakReference.get()?.hasPermissionGranted(permission) ?: false
    }

    override fun shouldShowRationaleForPermission(permission: String): Boolean {
        return permissionRequesterWeakReference.get()?.shouldShowRationaleForPermission(permission) ?: false
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
        permissionRequesterWeakReference.get()
            ?.requestPermissions(permissions, object : PermissionsManager.PermissionsRequesterCallback {
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
