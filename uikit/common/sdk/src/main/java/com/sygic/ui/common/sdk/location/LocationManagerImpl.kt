package com.sygic.ui.common.sdk.location

import android.app.Activity
import androidx.annotation.RestrictTo
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.sygic.sdk.position.PositionManager
import com.sygic.ui.common.sdk.location.livedata.LocationProviderCheckLiveEvent
import com.sygic.ui.common.sdk.location.livedata.LocationRequestLiveEvent
import com.sygic.ui.common.sdk.utils.POSITION_ON_MAP_ENABLED_BASE_DEFAULT_VALUE

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
class LocationManagerImpl : LocationManager {

    private var wasNoGPSDialogAlreadyShown: Boolean = false

    private val providerCheck: LocationProviderCheckLiveEvent = LocationProviderCheckLiveEvent()
    private val locationRequest: LocationRequestLiveEvent = LocationRequestLiveEvent()

    override var positionOnMapEnabled: Boolean = POSITION_ON_MAP_ENABLED_BASE_DEFAULT_VALUE
        set(value) {
            field = value
            setSdkPositionUpdatingEnabled(value)
        }

    override fun observe(owner: LifecycleOwner, observer: Observer<LocationManager.LocationRequesterCallback>) {
        providerCheck.observe(owner)
        locationRequest.observe(owner, observer)
    }

    override fun setSdkPositionUpdatingEnabled(enabled: Boolean) =
        PositionManager.getInstance().run { if (enabled) startPositionUpdating() else stopPositionUpdating() }

    /**
     * Returns the current enabled/disabled status of the GPS provider.
     *
     * If the user has enabled this provider in the Settings menu, true
     * is returned to the {@link androidx.lifecycle.Observer}, false otherwise
     *
     * @return current GPS status
     */
    override fun checkGpsEnabled(observer: Observer<Boolean>) {
        providerCheck.checkEnabled(android.location.LocationManager.GPS_PROVIDER, observer)
    }

    override fun requestToEnableGps(onSuccess: () -> Unit, onDenied: () -> Unit) {
        checkGpsEnabled(Observer { gpsEnabled ->
            if (!gpsEnabled) {
                requestToEnableGpsInternal(object : LocationManager.EnableGpsCallback {
                    override fun onResult(@EnableGpsResult result: Int) {
                        when (result) {
                            EnableGpsResult.ENABLED -> onSuccess()
                            EnableGpsResult.DENIED -> onDenied()
                        }
                    }
                })
            } else {
                onSuccess()
            }
        })
    }

    private fun requestToEnableGpsInternal(enableGpsCallback: LocationManager.EnableGpsCallback) {
        if (!wasNoGPSDialogAlreadyShown) {
            locationRequest.value = object : LocationManager.LocationRequesterCallback {
                override fun onActivityResult(requestCode: Int, resultCode: Int) {
                    when (requestCode) {
                        GOOGLE_API_CLIENT_REQUEST_CODE -> enableGpsCallback.onResult(if (resultCode == Activity.RESULT_OK) EnableGpsResult.ENABLED else EnableGpsResult.DENIED)
                        SETTING_ACTIVITY_REQUEST_CODE -> checkGpsEnabled(Observer { gpsEnabled ->
                            enableGpsCallback.onResult(if (gpsEnabled) EnableGpsResult.ENABLED else EnableGpsResult.DENIED)
                        })
                    }
                }
            }
            wasNoGPSDialogAlreadyShown = true
        } else {
            enableGpsCallback.onResult(EnableGpsResult.DENIED)
        }
    }
}
