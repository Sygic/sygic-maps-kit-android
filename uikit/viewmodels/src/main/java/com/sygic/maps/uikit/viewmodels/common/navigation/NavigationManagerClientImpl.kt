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
import androidx.lifecycle.Observer
import androidx.lifecycle.Transformations
import com.sygic.maps.uikit.viewmodels.common.initialization.InitializationCallback
import com.sygic.maps.uikit.viewmodels.common.position.PositionManagerClient
import com.sygic.maps.uikit.views.common.extensions.observeOnce
import com.sygic.maps.uikit.views.common.utils.SingletonHolder
import com.sygic.sdk.navigation.NavigationManager
import com.sygic.sdk.navigation.NavigationManagerProvider
import com.sygic.sdk.navigation.RouteProgress
import com.sygic.sdk.navigation.routeeventnotifications.DirectionInfo
import com.sygic.sdk.navigation.routeeventnotifications.LaneInfo
import com.sygic.sdk.navigation.routeeventnotifications.SignpostInfo
import com.sygic.sdk.navigation.routeeventnotifications.SpeedLimitInfo
import com.sygic.sdk.navigation.traffic.TrafficNotification
import com.sygic.sdk.position.GeoPosition
import com.sygic.sdk.route.Route

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
class NavigationManagerClientImpl private constructor(
    private val positionManagerClient: PositionManagerClient
) : NavigationManagerClient {

    companion object : SingletonHolder<NavigationManagerClientImpl>() {
        @JvmStatic
        fun getInstance(client: PositionManagerClient) = getInstance { NavigationManagerClientImpl(client) }
    }

    private val managerProvider: LiveData<NavigationManager> = object : MutableLiveData<NavigationManager>() {
        init { NavigationManagerProvider.getInstance(InitializationCallback<NavigationManager> { value = it }) }
    }

    override val route by lazy {
        Transformations.switchMap<NavigationManager, Route?>(managerProvider) { manager ->
            object : MutableLiveData<Route?>() {

                private val listener by lazy { NavigationManager.OnRouteChangedListener { route, _ -> value = route } }

                override fun setValue(value: Route?) {
                    if (value != this.value) super.setValue(value)
                }

                override fun onActive() = manager.addOnRouteChangedListener(listener)
                override fun onInactive() = manager.removeOnRouteChangedListener(listener)
            }
        } as MutableLiveData
    }

    override val laneInfo by lazy {
        Transformations.switchMap<NavigationManager, LaneInfo>(managerProvider) { manager ->
            object : LiveData<LaneInfo>() {

                private val listener by lazy { NavigationManager.OnLaneListener { value = it } }

                override fun onActive() = manager.addOnLaneListener(listener)
                override fun onInactive() = manager.removeOnLaneListener(listener)
            }
        }
    }

    override val trafficNotification by lazy {
        Transformations.switchMap<NavigationManager, TrafficNotification>(managerProvider) { manager ->
            object : LiveData<TrafficNotification>() {

                private val listener = NavigationManager.OnTrafficChangedListener { value = it }

                override fun onActive() = manager.addOnTrafficChangedListener(listener)
                override fun onInactive() = manager.removeOnTrafficChangedListener(listener)
            }
        }
    }

    override val directionInfo by lazy {
        Transformations.switchMap<NavigationManager, DirectionInfo>(managerProvider) { manager ->
            object : LiveData<DirectionInfo>() {

                private val listener by lazy { NavigationManager.OnDirectionListener { value = it } }

                override fun onActive() = manager.addOnDirectionListener(listener)
                override fun onInactive() = manager.removeOnDirectionListener(listener)
            }
        }
    }

    override val speedLimitInfo by lazy {
        Transformations.switchMap<NavigationManager, SpeedLimitInfo>(managerProvider) { manager ->
            object : LiveData<SpeedLimitInfo>() {

                private val listener by lazy { NavigationManager.OnSpeedLimitListener { value = it } }

                override fun onActive() = manager.addOnSpeedLimitListener(listener)
                override fun onInactive() = manager.removeOnSpeedLimitListener(listener)
            }
        }
    }

    override val signpostInfoList by lazy {
        Transformations.switchMap<NavigationManager, List<SignpostInfo>>(managerProvider) { manager ->
            object : LiveData<List<SignpostInfo>>() {

                private val listener by lazy { NavigationManager.OnSignpostListener { value = it } }

                override fun onActive() = manager.addOnSignpostListener(listener)
                override fun onInactive() = manager.removeOnSignpostListener(listener)
            }
        }
    }

    override val routeProgress by lazy {
        Transformations.switchMap<NavigationManager, RouteProgress>(managerProvider) { manager ->
            object : LiveData<RouteProgress>() {

                private val routeChangedObserver by lazy { Observer<Route?> { updateValue() } }
                private val currentPositionObserver by lazy { Observer<GeoPosition> { updateValue() } }
                private val trafficNotificationObserver by lazy { Observer<TrafficNotification> { updateValue() } }

                private fun updateValue() { value = manager.routeProgress }

                override fun onActive() {
                    route.observeForever(routeChangedObserver)
                    trafficNotification.observeForever(trafficNotificationObserver)
                    positionManagerClient.currentPosition.observeForever(currentPositionObserver)
                }

                override fun onInactive() {
                    route.removeObserver(routeChangedObserver)
                    trafficNotification.removeObserver(trafficNotificationObserver)
                    positionManagerClient.currentPosition.removeObserver(currentPositionObserver)
                }
            }
        }
    }

    override fun addOnWaypointPassListener(listener: NavigationManager.OnWaypointPassListener) =
        managerProvider.observeOnce { it.addOnWaypointPassListener(listener) }

    override fun removeOnWaypointPassListener(listener: NavigationManager.OnWaypointPassListener) =
        managerProvider.observeOnce { it.removeOnWaypointPassListener(listener) }

    override fun setAudioBetterRouteListener(listener: NavigationManager.AudioBetterRouteListener?) =
        managerProvider.observeOnce { it.setAudioBetterRouteListener(listener) }

    override fun setAudioIncidentListener(listener: NavigationManager.AudioIncidentListener?) =
        managerProvider.observeOnce { it.setAudioIncidentListener(listener) }

    override fun setAudioInstructionListener(listener: NavigationManager.AudioInstructionListener?) =
        managerProvider.observeOnce { it.setAudioInstructionListener(listener) }

    override fun setAudioRailwayCrossingListener(listener: NavigationManager.AudioRailwayCrossingListener?) =
        managerProvider.observeOnce { it.setAudioRailwayCrossingListener(listener) }

    override fun setAudioSharpCurveListener(listener: NavigationManager.AudioSharpCurveListener?) =
        managerProvider.observeOnce { it.setAudioSharpCurveListener(listener) }

    override fun setAudioSpeedLimitListener(listener: NavigationManager.AudioSpeedLimitListener?) =
        managerProvider.observeOnce { it.setAudioSpeedLimitListener(listener) }

    override fun setAudioTrafficListener(listener: NavigationManager.AudioTrafficListener?) =
        managerProvider.observeOnce { it.setAudioTrafficListener(listener) }

    init {
        route.observeForever { route ->
            managerProvider.observeOnce { manager ->
                route?.let { manager.setRouteForNavigation(it) } ?: run { manager.stopNavigation() }
            }
        }
    }
}