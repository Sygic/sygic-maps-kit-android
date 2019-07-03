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

package com.sygic.maps.uikit.viewmodels.common.permission

import android.content.pm.PackageManager
import androidx.annotation.RestrictTo
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.sygic.maps.uikit.viewmodels.common.permission.livedata.PermissionCheckLiveEvent
import com.sygic.maps.uikit.viewmodels.common.permission.livedata.PermissionRationaleLiveEvent
import com.sygic.maps.uikit.viewmodels.common.permission.livedata.PermissionRequestLiveEvent

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

                    if (granted.isNotEmpty()) {
                        callback.onPermissionsGranted(granted)
                    }
                    if (denied.isNotEmpty()) {
                        callback.onPermissionsDenied(denied)
                    }
                }
            })
    }
}
