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
import com.sygic.maps.uikit.viewmodels.common.initialization.InitializationManager
import com.sygic.maps.uikit.viewmodels.common.initialization.InitializationState
import com.sygic.maps.uikit.viewmodels.common.position.PositionManagerClient
import com.sygic.maps.uikit.views.common.utils.SingletonHolder
import com.sygic.sdk.InitializationCallback
import com.sygic.sdk.context.SygicContext
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

    @InitializationState
    override var initializationState = InitializationState.INITIALIZATION_NOT_STARTED
    private val callbacks = LinkedHashSet<InitializationManager.Callback>()

    private lateinit var navigationManager: NavigationManager

    override fun initialize(callback: InitializationManager.Callback?) {
        synchronized(this) {
            if (initializationState == InitializationState.INITIALIZED) {
                callback?.onInitialized()
                return
            }

            callback?.let { callbacks.add(it) }

            if (initializationState == InitializationState.INITIALIZING) {
                return
            }

            initializationState = InitializationState.INITIALIZING
        }

        NavigationManagerProvider.getInstance(object : InitializationCallback<NavigationManager> {
            override fun onInstance(navigationManager: NavigationManager) {
                synchronized(this) {
                    this@NavigationManagerClientImpl.navigationManager = navigationManager
                    initializationState = InitializationState.INITIALIZED
                }
                with(callbacks) {
                    forEach { it.onInitialized() }
                    clear()
                }
            }
            override fun onError(@SygicContext.OnInitListener.Result result: Int) {
                synchronized(this) { initializationState = InitializationState.ERROR }
                with(callbacks) {
                    forEach { it.onError(result) }
                    clear()
                }
            }
        })
    }

    override val route by lazy {
        object : MutableLiveData<Route>() {

            private val listener by lazy { NavigationManager.OnRouteChangedListener { value = it } }

            override fun setValue(value: Route?) {
                if (value != this.value) {
                    onReady {
                        value?.let { navigationManager.setRouteForNavigation(it) } ?: run { navigationManager.stopNavigation() }
                    }
                    super.setValue(value)
                }
            }

            override fun onActive() = onReady { navigationManager.addOnRouteChangedListener(listener) }
            override fun onInactive() = onReady { navigationManager.removeOnRouteChangedListener(listener) }
        }
    }

    override val laneInfo by lazy {
        object : LiveData<LaneInfo>() {

            private val listener by lazy { NavigationManager.OnLaneListener { value = it } }

            override fun onActive() = onReady { navigationManager.addOnLaneListener(listener) }
            override fun onInactive() = onReady { navigationManager.removeOnLaneListener(listener) }
        }
    }

    override val trafficNotification by lazy {
        object : LiveData<TrafficNotification>() {

            private val listener = NavigationManager.OnTrafficChangedListener { value = it }

            override fun onActive() = onReady { navigationManager.addOnTrafficChangedListener(listener) }
            override fun onInactive() = onReady { navigationManager.removeOnTrafficChangedListener(listener) }
        }
    }

    override val directionInfo by lazy {
        object : LiveData<DirectionInfo>() {

            private val listener by lazy { NavigationManager.OnDirectionListener { value = it } }

            override fun onActive() = onReady { navigationManager.addOnDirectionListener(listener) }
            override fun onInactive() = onReady { navigationManager.removeOnDirectionListener(listener) }
        }
    }

    override val speedLimitInfo by lazy {
        object : LiveData<SpeedLimitInfo>() {

            private val listener by lazy { NavigationManager.OnSpeedLimitListener { value = it } }

            override fun onActive() = onReady { navigationManager.addOnSpeedLimitListener(listener) }
            override fun onInactive() = onReady { navigationManager.removeOnSpeedLimitListener(listener) }
        }
    }

    override val signpostInfoList by lazy {
        object : LiveData<List<SignpostInfo>>() {

            private val listener by lazy { NavigationManager.OnSignpostListener { value = it } }

            override fun onActive() = onReady { navigationManager.addOnSignpostListener(listener) }
            override fun onInactive() = onReady { navigationManager.removeOnSignpostListener(listener) }
        }
    }

    override val routeProgress by lazy {
        object : LiveData<RouteProgress>() {

            private val routeChangedObserver by lazy { Observer<Route> { updateValue() } }
            private val currentPositionObserver by lazy { Observer<GeoPosition> { updateValue() } }
            private val trafficNotificationObserver by lazy { Observer<TrafficNotification> { updateValue() } }

            private fun updateValue() { onReady { value = navigationManager.routeProgress } }

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

    override fun addOnWaypointPassListener(listener: NavigationManager.OnWaypointPassListener) =
        onReady { navigationManager.addOnWaypointPassListener(listener) }

    override fun removeOnWaypointPassListener(listener: NavigationManager.OnWaypointPassListener) =
        onReady { navigationManager.removeOnWaypointPassListener(listener) }

    override fun setAudioBetterRouteListener(listener: NavigationManager.AudioBetterRouteListener?) =
        onReady { navigationManager.setAudioBetterRouteListener(listener) }

    override fun setAudioIncidentListener(listener: NavigationManager.AudioIncidentListener?) =
        onReady { navigationManager.setAudioIncidentListener(listener) }

    override fun setAudioInstructionListener(listener: NavigationManager.AudioInstructionListener?) =
        onReady { navigationManager.setAudioInstructionListener(listener) }

    override fun setAudioRailwayCrossingListener(listener: NavigationManager.AudioRailwayCrossingListener?) =
        onReady { navigationManager.setAudioRailwayCrossingListener(listener) }

    override fun setAudioSharpCurveListener(listener: NavigationManager.AudioSharpCurveListener?) =
        onReady { navigationManager.setAudioSharpCurveListener(listener) }

    override fun setAudioSpeedLimitListener(listener: NavigationManager.AudioSpeedLimitListener?) =
        onReady { navigationManager.setAudioSpeedLimitListener(listener) }

    override fun setAudioTrafficListener(listener: NavigationManager.AudioTrafficListener?) =
        onReady { navigationManager.setAudioTrafficListener(listener) }
}