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

package com.sygic.maps.uikit.viewmodels.common.navigation

import androidx.annotation.RestrictTo
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sygic.maps.uikit.viewmodels.common.initialization.InitializationManager
import com.sygic.sdk.navigation.NavigationManager
import com.sygic.sdk.navigation.RouteProgress
import com.sygic.sdk.navigation.routeeventnotifications.DirectionInfo
import com.sygic.sdk.navigation.routeeventnotifications.LaneInfo
import com.sygic.sdk.route.Route

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
interface NavigationManagerClient : InitializationManager<InitializationManager.Callback> {

    val route: MutableLiveData<Route>
    val laneInfo: LiveData<LaneInfo>
    val routeProgress: LiveData<RouteProgress>
    val directionInfo: LiveData<DirectionInfo>

    fun stopNavigation() //todo
    fun addOnTrafficChangedListener(listener: NavigationManager.OnTrafficChangedListener) //todo
    fun removeOnTrafficChangedListener(listener: NavigationManager.OnTrafficChangedListener) //todo
    fun addOnSignpostListener(listener: NavigationManager.OnSignpostListener) //todo
    fun removeOnSignpostListener(listener: NavigationManager.OnSignpostListener) //todo
    fun addOnSpeedLimitListener(listener: NavigationManager.OnSpeedLimitListener) //todo
    fun removeOnSpeedLimitListener(listener: NavigationManager.OnSpeedLimitListener) //todo
    fun addOnWaypointPassListener(listener: NavigationManager.OnWaypointPassListener) //todo
    fun removeOnWaypointPassListener(listener: NavigationManager.OnWaypointPassListener) //todo
    //todo: migrate audio listeners also to live data?
    fun setAudioBetterRouteListener(listener: NavigationManager.AudioBetterRouteListener?)
    fun setAudioIncidentListener(listener: NavigationManager.AudioIncidentListener?)
    fun setAudioInstructionListener(listener: NavigationManager.AudioInstructionListener?)
    fun setAudioRailwayCrossingListener(listener: NavigationManager.AudioRailwayCrossingListener?)
    fun setAudioSharpCurveListener(listener: NavigationManager.AudioSharpCurveListener?)
    fun setAudioSpeedLimitListener(listener: NavigationManager.AudioSpeedLimitListener?)
    fun setAudioTrafficListener(listener: NavigationManager.AudioTrafficListener?)
}