package com.sygic.ui.common.sdk.extension

import com.sygic.sdk.places.LocationInfo
import com.sygic.sdk.position.GeoCoordinates
import com.sygic.ui.common.extensions.EMPTY_STRING
import java.util.*

fun LocationInfo.getFirst(@LocationInfo.LocationType locationType: Int): String? {
    // for each type of POI information, there could be multiple results, for instance multiple mail or phone info - get first
    return locationData?.get(locationType)?.firstOrNull()
}

fun GeoCoordinates.getFormattedLocation(): String? {
    if (!isValid) {
        return null
    }

    return String.format(Locale.US, "%.6f, %.6f", latitude, longitude)
}