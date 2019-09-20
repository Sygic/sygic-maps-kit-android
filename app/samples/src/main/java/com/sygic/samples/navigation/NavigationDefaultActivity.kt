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

package com.sygic.samples.navigation

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import com.sygic.maps.module.common.extensions.*
import com.sygic.maps.module.navigation.NavigationFragment
import com.sygic.maps.uikit.viewmodels.common.extensions.computePrimaryRoute
import com.sygic.maps.uikit.viewmodels.common.extensions.getLastValidLocation
import com.sygic.maps.uikit.views.common.extensions.isPermissionNotGranted
import com.sygic.maps.uikit.views.common.extensions.longToast
import com.sygic.maps.uikit.views.common.extensions.requestPermission
import com.sygic.samples.R
import com.sygic.samples.app.activities.CommonSampleActivity
import com.sygic.sdk.position.GeoCoordinates
import com.sygic.sdk.route.RoutePlan
import com.sygic.sdk.route.RoutingOptions

private const val REQUEST_CODE_PERMISSION_ACCESS_FINE_LOCATION = 7001
private const val REQUEST_CODE_GOOGLE_API_CLIENT = 7002
private const val REQUEST_CODE_SETTING_ACTIVITY = 7003

class NavigationDefaultActivity : CommonSampleActivity() {

    override val wikiModulePath: String = "Module-Navigation#navigation---default"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_navigation_default)

        if (savedInstanceState == null) {
            createRoutePlanAndComputeRoute()
        }
    }

    private fun createRoutePlanAndComputeRoute() {
        requestLastValidLocation { lastValidLocation ->
            val routePlan = RoutePlan().apply {
                setStart(lastValidLocation)
                setDestination(GeoCoordinates(41.893056, 12.482778))
                routingOptions = RoutingOptions().apply {
                    transportMode = RoutingOptions.TransportMode.Car
                    routingType = RoutingOptions.RoutingType.Economic
                }
            }

            computePrimaryRoute(routePlan) { route ->
                (supportFragmentManager.findFragmentById(R.id.navigationFragment) as NavigationFragment).routeInfo =
                    route
            }
        }
    }

    private fun requestLastValidLocation(currentLocationCallback: (GeoCoordinates) -> Unit) {
        if (isPermissionNotGranted(Manifest.permission.ACCESS_FINE_LOCATION)) {
            requestPermission(Manifest.permission.ACCESS_FINE_LOCATION, REQUEST_CODE_PERMISSION_ACCESS_FINE_LOCATION)
            return
        }

        if (isGpsNotEnabled()) {
            if (isGooglePlayServicesAvailable()) {
                createGoogleApiLocationRequest(REQUEST_CODE_GOOGLE_API_CLIENT)
            } else {
                showGenericNoGpsDialog(REQUEST_CODE_SETTING_ACTIVITY)
            }
            return
        }

        getLastValidLocation(currentLocationCallback)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_CODE_PERMISSION_ACCESS_FINE_LOCATION -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    createRoutePlanAndComputeRoute()
                } else {
                    longToast("Sorry, location permission is needed!")
                    finish()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            REQUEST_CODE_SETTING_ACTIVITY, REQUEST_CODE_GOOGLE_API_CLIENT -> {
                if (isGpsEnabled()) {
                    createRoutePlanAndComputeRoute()
                } else {
                    longToast("GPS module is not enabled :(")
                    finish()
                }
            }
        }
    }
}