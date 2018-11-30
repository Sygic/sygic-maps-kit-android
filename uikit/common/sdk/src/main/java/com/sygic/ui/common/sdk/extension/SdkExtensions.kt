package com.sygic.ui.common.sdk.extension

import com.sygic.sdk.places.LocationInfo

fun LocationInfo.getFirst(@LocationInfo.LocationType locationType: Int): String? {
    // for each type of POI information, there could be multiple results, for instance multiple mail or phone info - get first
    return locationData?.get(locationType)?.firstOrNull()
}
