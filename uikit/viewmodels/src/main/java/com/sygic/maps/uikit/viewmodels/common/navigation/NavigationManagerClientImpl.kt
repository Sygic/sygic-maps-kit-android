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

    override val route = object : MutableLiveData<Route>() {

        private val routeChangedListener = NavigationManager.OnRouteChangedListener { value = it }

        override fun setValue(value: Route?) {
            if (value != this.value) {
                value?.let { onReady { navigationManager.setRouteForNavigation(it) } }
                super.setValue(value)
            }
        }

        override fun onActive() = addOnRouteChangedListener(routeChangedListener)
        override fun onInactive() = removeOnRouteChangedListener(routeChangedListener)
    }

    override val laneInfo = object : LiveData<LaneInfo>() {

        private val laneInfoListener = NavigationManager.OnLaneListener { value = it }

        override fun onActive() = onReady { navigationManager.addOnLaneListener(laneInfoListener) }
        override fun onInactive() = onReady { navigationManager.removeOnLaneListener(laneInfoListener) }
    }

    override val routeProgress = object : LiveData<RouteProgress>() {

        private val currentPositionObserver = Observer<GeoPosition> { updateValue() }
        private val trafficChangeListener = NavigationManager.OnTrafficChangedListener { updateValue() }
        private val routeChangedListener = NavigationManager.OnRouteChangedListener { updateValue() }

        private fun updateValue() { onReady { value = navigationManager.routeProgress } }

        override fun onActive() {
            positionManagerClient.currentPosition.observeForever(currentPositionObserver)
            addOnTrafficChangedListener(trafficChangeListener)
            addOnRouteChangedListener(routeChangedListener)
        }

        override fun onInactive() {
            positionManagerClient.currentPosition.removeObserver(currentPositionObserver)
            removeOnTrafficChangedListener(trafficChangeListener)
            removeOnRouteChangedListener(routeChangedListener)
        }
    }

    override val directionInfo = object : LiveData<DirectionInfo>() {

        private val directionInfoListener = NavigationManager.OnDirectionListener { value = it }

        override fun onActive() = onReady { navigationManager.addOnDirectionListener(directionInfoListener) }
        override fun onInactive() = onReady { navigationManager.removeOnDirectionListener(directionInfoListener) }
    }

    override fun stopNavigation() = onReady { navigationManager.stopNavigation() }

    override fun addOnTrafficChangedListener(listener: NavigationManager.OnTrafficChangedListener) =
        onReady { navigationManager.addOnTrafficChangedListener(listener) }

    override fun removeOnTrafficChangedListener(listener: NavigationManager.OnTrafficChangedListener)  =
        onReady { navigationManager.removeOnTrafficChangedListener(listener) }

    override fun addOnSignpostListener(listener: NavigationManager.OnSignpostListener) =
        onReady { navigationManager.addOnSignpostListener(listener) }

    override fun removeOnSignpostListener(listener: NavigationManager.OnSignpostListener) =
        onReady { navigationManager.removeOnSignpostListener(listener) }

    override fun addOnSpeedLimitListener(listener: NavigationManager.OnSpeedLimitListener) =
        onReady { navigationManager.addOnSpeedLimitListener(listener) }

    override fun removeOnSpeedLimitListener(listener: NavigationManager.OnSpeedLimitListener) =
        onReady { navigationManager.removeOnSpeedLimitListener(listener) }

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

    private fun addOnRouteChangedListener(listener: NavigationManager.OnRouteChangedListener) =
        onReady { navigationManager.addOnRouteChangedListener(listener) }

    private fun removeOnRouteChangedListener(listener: NavigationManager.OnRouteChangedListener) =
        onReady { navigationManager.removeOnRouteChangedListener(listener) }

}