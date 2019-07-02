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

package com.sygic.maps.module.navigation.viewmodel

import android.app.Application
import android.os.Bundle
import android.util.Log
import androidx.annotation.RestrictTo
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.sygic.maps.module.common.theme.ThemeManager
import com.sygic.maps.module.common.viewmodel.ThemeSupportedViewModel
import com.sygic.maps.module.navigation.KEY_PREVIEW_MODE
import com.sygic.maps.module.navigation.component.PREVIEW_MODE_DEFAULT_VALUE
import com.sygic.maps.tools.annotations.Assisted
import com.sygic.maps.tools.annotations.AutoFactory
import com.sygic.maps.uikit.viewmodels.common.location.LocationManager
import com.sygic.maps.uikit.viewmodels.common.permission.PermissionsManager
import com.sygic.maps.uikit.viewmodels.common.sdk.model.ExtendedCameraModel
import com.sygic.maps.uikit.viewmodels.common.sdk.model.ExtendedMapDataModel
import com.sygic.maps.uikit.viewmodels.common.utils.requestLocationAccess
import com.sygic.maps.uikit.views.common.extensions.getBoolean
import com.sygic.sdk.map.Camera
import com.sygic.sdk.map.MapAnimation
import com.sygic.sdk.map.MapCenter
import com.sygic.sdk.map.MapCenterSettings
import com.sygic.sdk.map.`object`.MapRoute
import com.sygic.sdk.navigation.NavigationManager
import com.sygic.sdk.route.RouteInfo

@AutoFactory
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
class NavigationFragmentViewModel internal constructor(
    app: Application,
    @Assisted arguments: Bundle?,
    themeManager: ThemeManager,
    private val cameraModel: ExtendedCameraModel,
    private val mapDataModel: ExtendedMapDataModel,
    private val navigationManager: NavigationManager,
    private val locationManager: LocationManager,
    private val permissionsManager: PermissionsManager
) : ThemeSupportedViewModel(app, themeManager), DefaultLifecycleObserver {

    val previewMode: MutableLiveData<Boolean> = MutableLiveData()
    val routeInfo: MutableLiveData<RouteInfo?> = object: MutableLiveData<RouteInfo?>() {
        override fun setValue(value: RouteInfo?) {
            value?.let {
                if (value != this.value) {
                    super.setValue(it)

                    setCameraNavigationState()
                    if (previewMode.value!!) {
                        startPreviewMode(it)
                    } else {
                        startNavigation(it)
                    }
                }
            }
        }
    }

    init {
        with(arguments) {
            previewMode.value = getBoolean(KEY_PREVIEW_MODE, PREVIEW_MODE_DEFAULT_VALUE)
        }
    }

    override fun onCreate(owner: LifecycleOwner) {
        previewMode.observe(owner, Observer { enabled ->
            Log.d("Tomas", "onCreate() called with: enabled = [$enabled]")
        })
    }

    override fun onStart(owner: LifecycleOwner) {
        locationManager.positionOnMapEnabled = !previewMode.value!!
    }

    private fun setCameraNavigationState() {
        cameraModel.mapCenterSettings = MapCenterSettings(MapCenter(0.5f, 0.5f), MapCenter(0.5f, 0.5f), MapAnimation.NONE, MapAnimation.NONE) //todo
        cameraModel.movementMode = Camera.MovementMode.FollowGpsPositionWithAutozoom
        cameraModel.rotationMode = Camera.RotationMode.Vehicle
        cameraModel.tilt = 60f //todo: ZoomControlsViewModel DEFAULT_TILT_3D
    }

    private fun startPreviewMode(routeInfo: RouteInfo) {
        locationManager.positionOnMapEnabled = false
        mapDataModel.addMapRoute(MapRoute.from(routeInfo).build())
        // navigationManager.setRouteForNavigation(it) //todo: required for demonstrate ???
    }

    private fun startNavigation(routeInfo: RouteInfo) {
        requestLocationAccess(permissionsManager, locationManager) {
            locationManager.positionOnMapEnabled = true
            Log.d("Tomas", "startNavigation() called")

            navigationManager.setRouteForNavigation(routeInfo) //todo: register listener first
            //mapDataModel.addMapRoute(MapRoute.from(it).build()) //todo: wait for new routeInfo from listener
        }
    }

    override fun onStop(owner: LifecycleOwner) {
        locationManager.positionOnMapEnabled = false
    }

    override fun onDestroy(owner: LifecycleOwner) {
        //todo: restore camera state?
    }
}
