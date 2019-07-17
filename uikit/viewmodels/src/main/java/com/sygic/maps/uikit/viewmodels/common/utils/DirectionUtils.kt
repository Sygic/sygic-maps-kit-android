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

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.sygic.maps.uikit.viewmodels.R
import com.sygic.sdk.route.RouteManeuver

object DirectionUtils {

    fun isManeuverRoundabout(maneuver: RouteManeuver?): Boolean = maneuver?.let {
        when (maneuver.type) {
            RouteManeuver.Type.RoundaboutE,
            RouteManeuver.Type.RoundaboutN,
            RouteManeuver.Type.RoundaboutNE,
            RouteManeuver.Type.RoundaboutNW,
            RouteManeuver.Type.RoundaboutS,
            RouteManeuver.Type.RoundaboutSE,
            RouteManeuver.Type.RoundaboutSW,
            RouteManeuver.Type.RoundaboutW,
            RouteManeuver.Type.RoundaboutLeftE,
            RouteManeuver.Type.RoundaboutLeftN,
            RouteManeuver.Type.RoundaboutLeftNE,
            RouteManeuver.Type.RoundaboutLeftNW,
            RouteManeuver.Type.RoundaboutLeftS,
            RouteManeuver.Type.RoundaboutLeftSE,
            RouteManeuver.Type.RoundaboutLeftSW,
            RouteManeuver.Type.RoundaboutLeftW -> true
            else -> false
        }
    } ?: false

    @DrawableRes
    fun getDirectionDrawable(@RouteManeuver.Type direction: Int): Int = when (direction) {
        RouteManeuver.Type.KeepRight -> R.drawable.ic_direction_right_keep
        RouteManeuver.Type.KeepLeft -> R.drawable.ic_direction_left_keep
        RouteManeuver.Type.EasyRight -> R.drawable.ic_direction_right_45
        RouteManeuver.Type.EasyLeft -> R.drawable.ic_direction_left_45
        RouteManeuver.Type.Right -> R.drawable.ic_direction_right_90
        RouteManeuver.Type.Left -> R.drawable.ic_direction_left_90
        RouteManeuver.Type.SharpRight -> R.drawable.ic_direction_right_135
        RouteManeuver.Type.SharpLeft -> R.drawable.ic_direction_left_135
        RouteManeuver.Type.Via -> R.drawable.ic_direction_finish
        RouteManeuver.Type.Straight -> R.drawable.ic_direction_straight
        RouteManeuver.Type.UTurnRight -> R.drawable.ic_direction_right_180
        RouteManeuver.Type.UTurnLeft -> R.drawable.ic_direction_left_180
        RouteManeuver.Type.End -> R.drawable.ic_direction_finish
        RouteManeuver.Type.Start -> R.drawable.ic_direction_finish
        RouteManeuver.Type.RoundaboutSE -> R.drawable.ic_direction_round_45
        RouteManeuver.Type.RoundaboutE -> R.drawable.ic_direction_round_90
        RouteManeuver.Type.RoundaboutNE -> R.drawable.ic_direction_round_135
        RouteManeuver.Type.RoundaboutN -> R.drawable.ic_direction_round_180
        RouteManeuver.Type.RoundaboutNW -> R.drawable.ic_direction_round_225
        RouteManeuver.Type.RoundaboutW -> R.drawable.ic_direction_round_270
        RouteManeuver.Type.RoundaboutSW -> R.drawable.ic_direction_round_315
        RouteManeuver.Type.RoundaboutS -> R.drawable.ic_direction_round_360
        RouteManeuver.Type.RoundaboutLeftSW -> R.drawable.ic_direction_round_left_45
        RouteManeuver.Type.RoundaboutLeftW -> R.drawable.ic_direction_round_left_90
        RouteManeuver.Type.RoundaboutLeftNW -> R.drawable.ic_direction_round_left_135
        RouteManeuver.Type.RoundaboutLeftN -> R.drawable.ic_direction_round_left_180
        RouteManeuver.Type.RoundaboutLeftNE -> R.drawable.ic_direction_round_left_225
        RouteManeuver.Type.RoundaboutLeftE -> R.drawable.ic_direction_round_left_270
        RouteManeuver.Type.RoundaboutLeftSE -> R.drawable.ic_direction_round_left_315
        RouteManeuver.Type.RoundaboutLeftS -> R.drawable.ic_direction_round_left_360
        RouteManeuver.Type.Ferry -> R.drawable.ic_direction_ferry
        RouteManeuver.Type.StateBoundary -> R.drawable.ic_direction_border_crossing
        RouteManeuver.Type.Follow -> R.drawable.ic_direction_straight
        RouteManeuver.Type.ExitRight -> R.drawable.ic_direction_exit_right
        RouteManeuver.Type.ExitLeft -> R.drawable.ic_direction_exit_left
        RouteManeuver.Type.Motorway -> R.drawable.ic_direction_highway
        RouteManeuver.Type.Tunnel -> R.drawable.ic_direction_tunnel
        else -> 0
    }

    @StringRes
    fun getDirectionInstruction(@RouteManeuver.Type direction: Int): Int = when (direction) {
        RouteManeuver.Type.KeepRight -> R.string.direction_keep_right
        RouteManeuver.Type.KeepLeft -> R.string.direction_keep_left
        RouteManeuver.Type.EasyRight -> R.string.direction_easy_right
        RouteManeuver.Type.EasyLeft -> R.string.direction_easy_Left
        RouteManeuver.Type.Right -> R.string.direction_turn_right
        RouteManeuver.Type.Left -> R.string.direction_turn_left
        RouteManeuver.Type.SharpRight -> R.string.direction_sharp_right
        RouteManeuver.Type.SharpLeft -> R.string.direction_sharp_left
        RouteManeuver.Type.Via -> R.string.direction_via
        RouteManeuver.Type.Straight -> R.string.direction_straight
        RouteManeuver.Type.UTurnRight -> R.string.direction_uturn_right
        RouteManeuver.Type.UTurnLeft -> R.string.direction_uturn_left
        RouteManeuver.Type.End -> R.string.direction_end
        RouteManeuver.Type.Start -> R.string.direction_start
        RouteManeuver.Type.Ferry -> R.string.direction_ferry
        RouteManeuver.Type.StateBoundary -> R.string.direction_border_crossing
        RouteManeuver.Type.Follow -> R.string.direction_straight
        RouteManeuver.Type.ExitRight -> R.string.direction_exit_right
        RouteManeuver.Type.ExitLeft -> R.string.direction_exit_left
        RouteManeuver.Type.Motorway -> R.string.direction_highway
        RouteManeuver.Type.Tunnel -> R.string.direction_tunnel
        RouteManeuver.Type.RoundaboutE,
        RouteManeuver.Type.RoundaboutN,
        RouteManeuver.Type.RoundaboutNE,
        RouteManeuver.Type.RoundaboutNW,
        RouteManeuver.Type.RoundaboutS,
        RouteManeuver.Type.RoundaboutSE,
        RouteManeuver.Type.RoundaboutSW,
        RouteManeuver.Type.RoundaboutW,
        RouteManeuver.Type.RoundaboutLeftE,
        RouteManeuver.Type.RoundaboutLeftN,
        RouteManeuver.Type.RoundaboutLeftNE,
        RouteManeuver.Type.RoundaboutLeftNW,
        RouteManeuver.Type.RoundaboutLeftS,
        RouteManeuver.Type.RoundaboutLeftSE,
        RouteManeuver.Type.RoundaboutLeftSW,
        RouteManeuver.Type.RoundaboutLeftW -> R.string.direction_roundabout
        else -> 0
    }
}
