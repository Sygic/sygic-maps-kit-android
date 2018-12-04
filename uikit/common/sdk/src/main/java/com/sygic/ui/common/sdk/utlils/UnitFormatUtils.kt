package com.sygic.ui.common.sdk.utlils

import com.sygic.sdk.position.GeoCoordinates
import java.util.*

fun getFormattedLocation(geoCoordinates: GeoCoordinates): String {
    if (!geoCoordinates.isValid) {
        return ""
    }

    return String.format(Locale.US, "%.6f, %.6f", geoCoordinates.latitude, geoCoordinates.longitude)
}
