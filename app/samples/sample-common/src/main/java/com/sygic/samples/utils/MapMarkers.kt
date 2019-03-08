package com.sygic.samples.utils

import com.sygic.sdk.map.`object`.MapMarker

object MapMarkers {

    val testMarkerOne: MapMarker = MapMarker.Builder()
        .coordinates(48.182684, 17.094457)
        .title("Test Marker 1")
        .build()

    val testMarkerTwo: MapMarker = MapMarker.Builder()
        .coordinates(48.162805, 17.101621)
        .title("Test Marker 2")
        .build()

    val testMarkerThree: MapMarker = MapMarker.Builder()
        .coordinates(48.165561, 17.139550)
        .title("Test Marker 3")
        .build()

    val testMarkerFour: MapMarker = MapMarker.Builder()
        .coordinates(48.128453, 17.118402)
        .title("Test Marker 4")
        .build()

    val testMarkerFive: MapMarker = MapMarker.Builder()
        .coordinates(48.141797, 17.097001)
        .title("Test Marker 5")
        .build()

    val testMarkerSix: MapMarker = MapMarker.Builder()
        .coordinates(48.134756, 17.127729)
        .title("Test Marker 6")
        .build()
}