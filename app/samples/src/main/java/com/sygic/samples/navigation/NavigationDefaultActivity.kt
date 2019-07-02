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
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.sygic.maps.module.navigation.NavigationFragment
import com.sygic.maps.uikit.viewmodels.common.extensions.computePrimaryRoute
import com.sygic.maps.uikit.views.common.extensions.longToast
import com.sygic.samples.R
import com.sygic.samples.app.activities.CommonSampleActivity
import com.sygic.sdk.position.GeoCoordinates
import com.sygic.sdk.route.RoutePlan
import com.sygic.sdk.route.RoutingOptions

private const val REQUEST_CODE_PERMISSION_ACCESS_FINE_LOCATION = 7001
private const val REQUEST_CODE_GOOGLE_API_CLIENT = 7002
private const val REQUEST_CODE_SETTING_ACTIVITY = 7003
private const val REQUEST_CODE_PLAY_SERVICES_RESOLUTION = 7004

class NavigationDefaultActivity : CommonSampleActivity() {

    override val wikiModulePath: String = "Module-Navigation#navigation---default"

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_navigation_default)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_CODE_PERMISSION_ACCESS_FINE_LOCATION -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    createRoutePlanAndComputeRoute()
                } else {
                    finishWithMessage("Sorry, location permission is required!")
                }
                return
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            REQUEST_CODE_SETTING_ACTIVITY -> {
                if (isGpsEnabled()) {
                    createRoutePlanAndComputeRoute()
                } else {
                    finishWithMessage("GPS module is not enabled :(")
                }
            }
            REQUEST_CODE_GOOGLE_API_CLIENT -> {
                if (resultCode == Activity.RESULT_OK) {
                    createRoutePlanAndComputeRoute()
                } else {
                    finishWithMessage("GPS module is not enabled :(")
                }
            }
        }
    }

    private fun requestLastValidLocation(currentLocationCallback: (GeoCoordinates) -> Unit) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_CODE_PERMISSION_ACCESS_FINE_LOCATION
            )
            return
        }

        if (!isGpsEnabled()) {
            if (isGooglePlayServicesAvailable()) {
                createGoogleApiLocationRequest()
            } else {
                showGenericNoGpsDialog()
            }
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                currentLocationCallback.invoke(GeoCoordinates(it.latitude, it.longitude))
            } ?: run {
                finishWithMessage("Unable to get the last valid position.")
            }
        }
    }

    private fun isGooglePlayServicesAvailable(): Boolean {
        val apiAvailability = GoogleApiAvailability.getInstance()
        val resultCode = apiAvailability.isGooglePlayServicesAvailable(this)

        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, REQUEST_CODE_PLAY_SERVICES_RESOLUTION)
            }
            return false
        }

        return true
    }

    private fun isGpsEnabled(): Boolean {
        return (getSystemService(Context.LOCATION_SERVICE) as LocationManager).isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    private fun createGoogleApiLocationRequest() {
        val locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        val locationSettingsRequestBuilder = LocationSettingsRequest.Builder()
            .setAlwaysShow(true)
            .addLocationRequest(locationRequest)

        val responseTask = LocationServices.getSettingsClient(this)
            .checkLocationSettings(locationSettingsRequestBuilder.build())
        responseTask.addOnCompleteListener(this) { task ->
            try {
                task.getResult(ApiException::class.java)
            } catch (exception: ApiException) {
                when (exception.statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED ->
                        try {
                            startIntentSenderForResult(
                                (exception as ResolvableApiException).resolution.intentSender,
                                REQUEST_CODE_GOOGLE_API_CLIENT,
                                null,
                                0,
                                0,
                                0,
                                null
                            )
                        } catch (ignored: IntentSender.SendIntentException) {
                            Log.e("RequesterWrapper", "SendIntentException")
                        } catch (ignored: ClassCastException) {
                            Log.e("RequesterWrapper", "ClassCastException")
                        }
                }
            }
        }
    }

    private fun showGenericNoGpsDialog() {
        AlertDialog.Builder(this)
            .setTitle(R.string.enable_gps_dialog_title)
            .setMessage(R.string.enable_gps_dialog_text)
            .setNegativeButton(R.string.cancel, null)
            .setPositiveButton(
                R.string.settings
            ) { _, _ ->
                startActivityForResult(
                    Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS),
                    REQUEST_CODE_SETTING_ACTIVITY
                )
            }
            .show()
    }

    private fun finishWithMessage(message: String) {
        longToast(message)
        finish()
    }
}