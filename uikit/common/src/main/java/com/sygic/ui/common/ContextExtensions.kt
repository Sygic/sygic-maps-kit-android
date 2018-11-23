package com.sygic.ui.common

import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.location.LocationManager
import android.view.View

fun Context.isRtl(): Boolean {
    return this.resources.configuration.layoutDirection == View.LAYOUT_DIRECTION_RTL
}

inline val Context.locationManager: LocationManager?
    get() = getSystemService(LOCATION_SERVICE) as? LocationManager