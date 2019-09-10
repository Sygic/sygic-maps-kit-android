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

import com.sygic.maps.uikit.views.common.units.DistanceUnit
import com.sygic.maps.uikit.views.common.extensions.EMPTY_STRING
import com.sygic.maps.uikit.views.common.extensions.getDecimal
import com.sygic.maps.uikit.viewmodels.common.extensions.getSpeedLimitValue
import com.sygic.sdk.navigation.warnings.SpeedLimitInfo
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

object Distance {

    private const val METRIC_KM_DISTANCE_UNIT = "km"
    private const val METRIC_M_DISTANCE_UNIT = "m"
    private const val IMPERIALS_MI_DISTANCE_UNIT = "mi"
    private const val IMPERIALS_FEET_DISTANCE_UNIT = "ft"
    private const val IMPERIALS_YARD_DISTANCE_UNIT = "yd"
    private const val M_TO_KM_CONVERSION_RATIO = 0.001f
    private const val M_TO_MI_CONVERSION_RATIO = 0.00062f
    private const val M_TO_FT_CONVERSION_RATIO = 3.28084f
    private const val M_TO_YD_CONVERSION_RATIO = 1.09361f

    private val DECIMAL_FORMAT = DecimalFormat("#.#", DecimalFormatSymbols())

    fun getFormattedDistance(
        distanceUnit: DistanceUnit,
        distanceInMeters: Int,
        roundSmallUnits: Boolean = true
    ): String = when (distanceUnit) {
        DistanceUnit.KILOMETERS -> {
            val kilometers = distanceInMeters * M_TO_KM_CONVERSION_RATIO
            when {
                kilometers >= 10 -> "${kilometers.roundToInt()} $METRIC_KM_DISTANCE_UNIT"
                kilometers >= 1 -> "${DECIMAL_FORMAT.format(kilometers.toDouble())} $METRIC_KM_DISTANCE_UNIT"
                roundSmallUnits -> getFormattedDistance(distanceUnit, roundDistance(distanceInMeters), false)
                else -> "$distanceInMeters $METRIC_M_DISTANCE_UNIT"
            }
        }

        DistanceUnit.MILES_YARDS -> getDistanceInMilesWithUnits(
            distanceInMeters,
            M_TO_YD_CONVERSION_RATIO,
            IMPERIALS_YARD_DISTANCE_UNIT,
            roundSmallUnits
        )

        DistanceUnit.MILES_FEETS -> getDistanceInMilesWithUnits(
            distanceInMeters,
            M_TO_FT_CONVERSION_RATIO,
            IMPERIALS_FEET_DISTANCE_UNIT,
            roundSmallUnits
        )
    }

    private fun getDistanceInMilesWithUnits(
        meters: Int,
        smallUnitConversionRatio: Float,
        smallUnitText: String,
        roundSmallUnits: Boolean
    ): String {
        val miles = meters * M_TO_MI_CONVERSION_RATIO
        val smallUnitsValue = (meters * smallUnitConversionRatio).roundToInt()

        return when {
            miles >= 10 -> "${miles.roundToInt()} $IMPERIALS_MI_DISTANCE_UNIT"
            smallUnitsValue >= 1000 -> {
                val roundedMiles = miles.toInt()
                val rest = miles.getDecimal()

                if (rest < 0.3f) {
                    if (roundedMiles == 0) "¼ $IMPERIALS_MI_DISTANCE_UNIT" else "$roundedMiles $IMPERIALS_MI_DISTANCE_UNIT"
                } else if (rest < 0.6f) {
                    if (roundedMiles == 0) "½ $IMPERIALS_MI_DISTANCE_UNIT" else "$roundedMiles ½ $IMPERIALS_MI_DISTANCE_UNIT"
                } else {
                    "${roundedMiles + 1} $IMPERIALS_MI_DISTANCE_UNIT"
                }
            }
            else -> "${if (roundSmallUnits) roundDistance(smallUnitsValue) else smallUnitsValue} $smallUnitText"
        }
    }

    private fun roundDistance(distance: Int): Int {
        return when {
            distance < 5 -> 0
            distance < 30 -> (distance + 2).let { it - it % 5 }
            distance < 250 -> (distance + 5).let { it - it % 10 }
            distance < 800 -> (distance + 25).let { it - it % 50 }
            distance < 10000 -> (distance + 50).let { it - it % 100 }
            else -> (distance + 500).let { it - it % 1000 }
        }
    }
}

object Time {

    fun getFormattedTime(seconds: Int): String {

        val totalHours = TimeUnit.SECONDS.toHours(seconds.toLong())
        val totalMinutes = TimeUnit.SECONDS.toMinutes(seconds.toLong())

        val days = TimeUnit.SECONDS.toDays(seconds.toLong())
        val hours = totalHours - days * 24
        val minutes = totalMinutes - totalHours * 60

        return when {
            days > 0 -> "${days}d:${hours}h"
            hours > 0 -> "${hours}h:${minutes}min"
            minutes > 0 -> "${minutes}min"
            seconds > 0 -> "1min"
            else -> EMPTY_STRING
        }
    }
}

object Elevation {

    fun getFormattedElevation(meters: Int): String = "$meters m asl"
}

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

    fun convertValue(speedValue: Int, targetDistanceUnit: DistanceUnit) =
        convertValue(speedValue, DistanceUnit.KILOMETERS, targetDistanceUnit)

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

    fun speedProgress(speedValue: Int, speedLimitInfo: SpeedLimitInfo, distanceUnit: DistanceUnit): Float {
        val speedLimitValue = speedLimitInfo.getSpeedLimitValue(distanceUnit)
        return (speedValue * 100.0f) / speedLimitValue
    }
}