package com.sygic.samples.utils

import com.sygic.sdk.map.`object`.MapMarker
import com.sygic.ui.common.sdk.data.BasicData

object MapMarkers {

    val testMarkerOne: MapMarker = MapMarker
        .from(48.182684, 17.094457)
        .payload(BasicData("Test Marker 1"))
        .build()

    val testMarkerTwo: MapMarker = MapMarker
        .from(48.162805, 17.101621)
        .payload(BasicData("Test Marker 2"))
        .build()

    val testMarkerThree: MapMarker = MapMarker
        .from(48.165561, 17.139550)
        .payload(BasicData("Test Marker 3"))
        .build()

    val testMarkerFour: MapMarker = MapMarker
        .from(48.128453, 17.118402)
        .payload(BasicData("Test Marker 4"))
        .build()

    val testMarkerFive: MapMarker = MapMarker
        .from(48.141797, 17.097001)
        .payload(BasicData("Test Marker 5"))
        .build()

    val testMarkerSix: MapMarker = MapMarker
        .from(48.134756, 17.127729)
        .payload(BasicData("Test Marker 6"))
        .build()
}