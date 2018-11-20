package com.sygic.modules.common

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.provider.Settings
import android.util.Log
import androidx.annotation.CallSuper
import androidx.annotation.RestrictTo
import androidx.core.content.ContextCompat
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsStatusCodes
import com.sygic.modules.common.manager.SdkInitializationManager
import com.sygic.modules.common.manager.SdkInitializationManagerImpl
import com.sygic.sdk.map.MapFragment
import com.sygic.sdk.online.OnlineManager
import com.sygic.ui.common.sdk.location.GOOGLE_API_CLIENT_REQUEST_CODE
import com.sygic.ui.common.sdk.location.LocationManager
import com.sygic.ui.common.sdk.location.SETTING_ACTIVITY_REQUEST_CODE
import com.sygic.ui.common.sdk.permission.PERMISSIONS_REQUEST_CODE
import com.sygic.ui.common.sdk.permission.PermissionsManager

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
abstract class MapFragmentWrapper : MapFragment(), LocationManager.LocationRequester,
    PermissionsManager.PermissionsRequester, SdkInitializationManager.Callback {

    private var locationRequesterCallback: LocationManager.LocationRequesterCallback? = null
    private var permissionsRequesterCallback: PermissionsManager.PermissionsRequesterCallback? = null

    private lateinit var sdkInitializationManager: SdkInitializationManager

    override fun onAttach(context: Context) {
        super.onAttach(context)

        sdkInitializationManager = SdkInitializationManagerImpl() //ToDo: singleton
        sdkInitializationManager.initialize((context as Activity).application, this)
    }

    @CallSuper
    override fun onSdkInitialized() {
        OnlineManager.getInstance().enableOnlineMapStreaming(true)
    }

    override fun hasPermissionGranted(permission: String): Boolean {
        return context?.let {
            ContextCompat.checkSelfPermission(it, permission) == PackageManager.PERMISSION_GRANTED
        } ?: false
    }

    override fun shouldShowRationaleForPermission(permission: String): Boolean {
        return context?.let {
            shouldShowRequestPermissionRationale(permission)
        } ?: false
    }

    override fun requestPermissions(
        permissions: Array<String>,
        permissionsRequesterCallback: PermissionsManager.PermissionsRequesterCallback
    ) {
        this.permissionsRequesterCallback = permissionsRequesterCallback
        requestPermissions(permissions, PERMISSIONS_REQUEST_CODE)
    }

    override fun requestToEnableGps(locationRequesterCallback: LocationManager.LocationRequesterCallback) {
        this.locationRequesterCallback = locationRequesterCallback
        if (isGooglePlayServicesAvailable()) {
            createGoogleApiLocationRequest()
        } else {
            showNoGoogleApiDialog()
        }
    }

    private fun isGooglePlayServicesAvailable(): Boolean {
        return context?.let {
            GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context) == ConnectionResult.SUCCESS
        } ?: false
    }

    private fun createGoogleApiLocationRequest() {
        activity?.let {
            val locationRequest = LocationRequest.create()
            locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

            val locationSettingsRequestBuilder = LocationSettingsRequest.Builder()
                .setAlwaysShow(true)
                .addLocationRequest(locationRequest)

            val responseTask = LocationServices.getSettingsClient(it)
                .checkLocationSettings(locationSettingsRequestBuilder.build())
            responseTask.addOnCompleteListener(it) { task ->
                try {
                    task.getResult(ApiException::class.java)
                } catch (exception: ApiException) {
                    when (exception.statusCode) {
                        LocationSettingsStatusCodes.RESOLUTION_REQUIRED ->
                            try {
                                startIntentSenderForResult(
                                    (exception as ResolvableApiException).resolution.intentSender,
                                    GOOGLE_API_CLIENT_REQUEST_CODE,
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
    }

    private fun showNoGoogleApiDialog() {
        context.let {
            AlertDialog.Builder(it)
                .setTitle(R.string.enable_gps_dialog_title)
                .setMessage(R.string.enable_gps_dialog_text)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(
                    R.string.settings
                ) { _, _ ->
                    startActivityForResult(
                        Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS),
                        SETTING_ACTIVITY_REQUEST_CODE
                    )
                }
                .show()
        }
    }

    override fun isProviderEnabled(provider: String): Boolean {
        return context?.let {
            (it.getSystemService(Context.LOCATION_SERVICE) as android.location.LocationManager).isProviderEnabled(
                provider
            )
        } ?: false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        locationRequesterCallback?.onActivityResult(requestCode, resultCode)
        locationRequesterCallback = null
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        permissionsRequesterCallback?.onRequestPermissionsResult(permissions, grantResults)
    }
}