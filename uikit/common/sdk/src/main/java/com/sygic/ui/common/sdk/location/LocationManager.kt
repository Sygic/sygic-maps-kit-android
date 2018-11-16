package com.sygic.ui.common.sdk.location

const val GOOGLE_API_CLIENT_REQUEST_CODE = 4321
const val SETTING_ACTIVITY_REQUEST_CODE = 5432

interface LocationManager {

    interface LocationRequester {
        fun requestToEnableGps(locationRequesterCallback : LocationRequesterCallback)
        fun isProviderEnabled(provider: String): Boolean
    }

    interface LocationRequesterCallback {
        fun onActivityResult(requestCode: Int, resultCode: Int)
    }

    interface EnableGpsCallback {
        fun onResult(@EnableGpsResult result: Int)
    }

    fun isGpsEnabled(): Boolean
    fun requestToEnableGps(enableGpsCallback: EnableGpsCallback, forceDialog: Boolean = true)
}
