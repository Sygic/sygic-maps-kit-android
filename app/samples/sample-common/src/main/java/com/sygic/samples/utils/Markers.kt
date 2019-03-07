package com.sygic.samples.utils

import com.sygic.sdk.map.`object`.MapMarker

object Markers {

    val testMarker : MapMarker = MapMarker.Builder()
        .coordinates(48.182684, 17.094457)
        .title("Test Marker")
        .build()
}