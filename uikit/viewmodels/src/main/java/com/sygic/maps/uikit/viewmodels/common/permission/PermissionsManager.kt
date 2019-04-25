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
