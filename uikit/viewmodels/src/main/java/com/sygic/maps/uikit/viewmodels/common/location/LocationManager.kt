package com.sygic.maps.uikit.viewmodels.common.location

import androidx.annotation.RestrictTo
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer

const val GOOGLE_API_CLIENT_REQUEST_CODE = 4321
const val SETTING_ACTIVITY_REQUEST_CODE = 5432

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
interface LocationManager {

    var positionOnMapEnabled: Boolean

    interface LocationRequesterCallback {
        fun onActivityResult(requestCode: Int, resultCode: Int)
    }

    interface EnableGpsCallback {
        fun onResult(@EnableGpsResult result: Int)
    }

    fun observe(owner: LifecycleOwner, observer: Observer<LocationRequesterCallback>)

    fun checkGpsEnabled(observer: Observer<Boolean>)
    fun requestToEnableGps(onSuccess: () -> Unit, onDenied: () -> Unit = { /* do nothing */ })

    fun setSdkPositionUpdatingEnabled(enabled: Boolean)
}
