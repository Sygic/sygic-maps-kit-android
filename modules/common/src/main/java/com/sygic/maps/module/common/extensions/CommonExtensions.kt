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

package com.sygic.maps.module.common.extensions

import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.location.LocationManager
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsStatusCodes
import com.sygic.maps.module.common.R
import com.sygic.maps.module.common.delegate.ApplicationComponentDelegate
import com.sygic.maps.module.common.delegate.FragmentsComponentDelegate
import com.sygic.maps.module.common.di.BaseFragmentComponent
import com.sygic.maps.module.common.di.util.ModuleBuilder
import com.sygic.maps.module.common.listener.OnMapClickListener
import com.sygic.maps.uikit.viewmodels.common.extensions.getSelectionType
import com.sygic.maps.uikit.views.common.extensions.locationManager
import com.sygic.maps.uikit.views.common.utils.logError
import com.sygic.sdk.map.`object`.ViewObject

fun OnMapClickListener.onMapClick(viewObject: ViewObject<*>): Boolean =
    onMapClick(viewObject.getSelectionType(), viewObject.position.latitude, viewObject.position.longitude)

inline fun <reified F : Fragment, C : BaseFragmentComponent<F>, B : ModuleBuilder<C>> F.executeInjector(builder: B) {
    builder
        .plus(
            FragmentsComponentDelegate.getComponent(this, ApplicationComponentDelegate)
        )
        .build()
        .inject(this)
}

fun Fragment.isGooglePlayServicesAvailable(): Boolean = requireContext().isGooglePlayServicesAvailable()

fun Context?.isGooglePlayServicesAvailable(): Boolean {
    return this?.let {
        GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS
    } ?: false
}

fun Fragment.isGpsEnabled(): Boolean = requireContext().isGpsEnabled()

fun Context?.isGpsEnabled(): Boolean {
    return this?.let {
        it.locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER)
    } ?: false
}

fun Context?.isGpsNotEnabled(): Boolean = !isGpsEnabled()

fun Fragment.createGoogleApiLocationRequest(requestCode: Int) =
    requireActivity().createGoogleApiLocationRequest(requestCode)

fun FragmentActivity.createGoogleApiLocationRequest(requestCode: Int) {
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
                            requestCode,
                            null,
                            0,
                            0,
                            0,
                            null
                        )
                    } catch (ignored: IntentSender.SendIntentException) {
                        logError("RequesterWrapper -> SendIntentException")
                    } catch (ignored: ClassCastException) {
                        logError("RequesterWrapper -> ClassCastException")
                    }
            }
        }
    }
}

fun Fragment.showGenericNoGpsDialog(requestCode: Int) = requireActivity().showGenericNoGpsDialog(requestCode)

fun FragmentActivity.showGenericNoGpsDialog(requestCode: Int): AlertDialog = AlertDialog.Builder(this)
    .setTitle(R.string.enable_gps_dialog_title)
    .setMessage(R.string.enable_gps_dialog_text)
    .setNegativeButton(R.string.cancel, null)
    .setPositiveButton(
        R.string.settings
    ) { _, _ ->
        startActivityForResult(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), requestCode)
    }
    .show()