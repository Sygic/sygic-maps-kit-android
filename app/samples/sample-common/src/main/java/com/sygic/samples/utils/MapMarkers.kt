package com.sygic.samples.utils

import com.sygic.sdk.map.`object`.MapMarker
import com.sygic.ui.common.sdk.data.BasicData

object MapMarkers {

    val sampleMarkerOne: MapMarker = MapMarker
        .from(48.182684, 17.094457)
        .withPayload(BasicData("Test Marker 1"))
        .build()

    val sampleMarkerTwo: MapMarker = MapMarker
        .from(48.162805, 17.101621)
        .withPayload(BasicData("Test Marker 2"))
        .build()

    val sampleMarkerThree: MapMarker = MapMarker
        .from(48.165561, 17.139550)
        .withPayload(BasicData("Test Marker 3"))
        .build()

    val sampleMarkerFour: MapMarker = MapMarker
        .from(48.128453, 17.118402)
        .withPayload(BasicData("Test Marker 4"))
        .build()

    val sampleMarkerFive: MapMarker = MapMarker
        .from(48.141797, 17.097001)
        .withPayload(BasicData("Test Marker 5"))
        .build()

    val sampleMarkerSix: MapMarker = MapMarker
        .from(48.134756, 17.127729)
        .withPayload(BasicData("Test Marker 6"))
        .build()
}