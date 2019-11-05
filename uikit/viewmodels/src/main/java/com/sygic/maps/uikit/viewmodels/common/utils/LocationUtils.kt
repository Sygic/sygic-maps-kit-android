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

package com.sygic.maps.uikit.viewmodels.common.utils

import android.Manifest
import androidx.lifecycle.Observer
import com.sygic.sdk.SygicEngine
import com.sygic.maps.uikit.viewmodels.common.location.LocationManager
import com.sygic.maps.uikit.viewmodels.common.permission.PermissionsManager

fun requestLocationAccess(permissionsManager: PermissionsManager, locationManager: LocationManager, onSuccess: () -> Unit) {
    requestLocationPermission(permissionsManager) {
        locationManager.requestToEnableGps({
            onSuccess()
        })
    }
}

fun requestLocationPermission(permissionsManager: PermissionsManager, onSuccess: () -> Unit) {
    permissionsManager.checkPermissionGranted(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Observer { permissionEnabled ->
            if (!permissionEnabled) {
                permissionsManager.requestPermission(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    object : PermissionsManager.PermissionCallback {
                        override fun onPermissionGranted(permission: String) {
                            SygicEngine.openGpsConnection()
                            onSuccess()
                        }

                        override fun onPermissionDenied(permission: String) { /* Currently do nothing */ }
                    })
            } else {
                onSuccess()
            }
        })
}

