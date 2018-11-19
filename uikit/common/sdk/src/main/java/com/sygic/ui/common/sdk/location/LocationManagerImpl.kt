package com.sygic.ui.common.sdk.location

import android.app.Activity

import java.lang.ref.WeakReference

class LocationManagerImpl(locationRequester: LocationManager.LocationRequester) : LocationManager {

    private var locationRequesterWeakReference: WeakReference<LocationManager.LocationRequester> = WeakReference(locationRequester)
    private var wasNoGPSDialogAlreadyShown: Boolean = false

    override fun requestToEnableGps(enableGpsCallback: LocationManager.EnableGpsCallback, forceDialog: Boolean) {
        locationRequesterWeakReference.get()?.let {
            if (!wasNoGPSDialogAlreadyShown || forceDialog) {
                it.requestToEnableGps(object : LocationManager.LocationRequesterCallback {
                    override fun onActivityResult(requestCode: Int, resultCode: Int) {
                        when (requestCode) {
                            GOOGLE_API_CLIENT_REQUEST_CODE -> enableGpsCallback.onResult(if (resultCode == Activity.RESULT_OK) EnableGpsResult.ENABLED else EnableGpsResult.DENIED)
                            SETTING_ACTIVITY_REQUEST_CODE -> enableGpsCallback.onResult(if (isGpsEnabled()) EnableGpsResult.ENABLED else EnableGpsResult.DENIED)
                        }
                    }
                })
                wasNoGPSDialogAlreadyShown = true
            } else {
                enableGpsCallback.onResult(EnableGpsResult.DENIED)
            }
        }
    }

    /**
     * Returns the current enabled/disabled status of the GPS provider.
     *
     *
     * If the user has enabled this provider in the Settings menu, true
     * is returned, false otherwise false is returned
     *
     * @return current GPS status
     */
    override fun isGpsEnabled(): Boolean {
        return locationRequesterWeakReference.get()?.let {
            return it.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)
        } ?: false
    }
}
