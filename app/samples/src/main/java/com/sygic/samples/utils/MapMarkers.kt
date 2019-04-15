/*
 * Copyright (c) 2019 Sygic a.s. All rights reserved.
 *
 * This project is licensed under the MIT License.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.sygic.samples.utils

import com.sygic.maps.uikit.viewmodels.common.data.BasicData
import com.sygic.sdk.map.`object`.MapMarker

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