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

package com.sygic.maps.uikit.viewmodels.common.utils

import com.sygic.maps.uikit.viewmodels.common.extensions.getSpeedLimitValue
import com.sygic.maps.uikit.views.common.units.DistanceUnit
import com.sygic.sdk.navigation.routeeventnotifications.SpeedLimitInfo
import kotlin.math.roundToInt

object Speed {

    private const val METRIC_SPEED_UNIT = "km/h"
    private const val IMPERIALS_SPEED_UNIT = "mph"
    private const val KMH_TO_MPH_CONVERSION_RATIO = 0.621371192f
    private const val MPH_TO_KMH_CONVERSION_RATIO = 1.609344f
    private const val SPEEDING_THRESHOLD_TOLERANCE = 1.06f

    fun getUnitFromDistanceUnit(distanceUnit: DistanceUnit): String = when (distanceUnit) {
        DistanceUnit.KILOMETERS -> METRIC_SPEED_UNIT
        DistanceUnit.MILES_YARDS, DistanceUnit.MILES_FEETS -> IMPERIALS_SPEED_UNIT
    }

    fun convertValue(speedValue: Int, currentDistanceUnit: DistanceUnit, targetDistanceUnit: DistanceUnit): Int {
        if (currentDistanceUnit == targetDistanceUnit) {
            return speedValue
        }

        return when (targetDistanceUnit) {
            DistanceUnit.KILOMETERS -> (speedValue * MPH_TO_KMH_CONVERSION_RATIO).roundToInt()
            DistanceUnit.MILES_YARDS, DistanceUnit.MILES_FEETS -> (speedValue * KMH_TO_MPH_CONVERSION_RATIO).roundToInt()
        }
    }

    fun isSpeeding(speedValue: Int, speedLimitInfo: SpeedLimitInfo, distanceUnit: DistanceUnit): Boolean {
        val speedLimitValue = speedLimitInfo.getSpeedLimitValue(distanceUnit)
        return speedLimitValue > 0 && speedValue > speedLimitValue * SPEEDING_THRESHOLD_TOLERANCE
    }

    fun speedProgress(
        speedValue: Int,
        speedLimitInfo: SpeedLimitInfo,
        distanceUnit: DistanceUnit
    ): Float = speedValue * 100.0f / speedLimitInfo.getSpeedLimitValue(distanceUnit)
}