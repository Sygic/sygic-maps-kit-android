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

package com.sygic.maps.uikit.viewmodels.common.navigation.preview

import androidx.annotation.RestrictTo
import androidx.lifecycle.MutableLiveData
import com.sygic.maps.uikit.viewmodels.common.navigation.preview.state.DemonstrationState
import com.sygic.sdk.InitializationCallback
import com.sygic.sdk.context.SygicContext
import com.sygic.sdk.position.GeoPosition
import com.sygic.sdk.route.Route
import com.sygic.sdk.route.simulator.PositionSimulator
import com.sygic.sdk.route.simulator.RouteDemonstrateSimulator
import com.sygic.sdk.route.simulator.RouteDemonstrateSimulatorProvider

const val DEFAULT_SPEED = 1F

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
object RouteDemonstrationManagerImpl : RouteDemonstrationManager {

    private var simulator: RouteDemonstrateSimulator? = null

    override var speedMultiplier = MutableLiveData<Float>(DEFAULT_SPEED)
    override val currentPosition = MutableLiveData<GeoPosition>()
    override val demonstrationState = MutableLiveData<DemonstrationState>(DemonstrationState.INACTIVE)

    init {
        speedMultiplier.observeForever { simulator?.setSpeedMultiplier(it) }
    }

    override fun start(route: Route) {
        destroy()
        demonstrationState.value = DemonstrationState.ACTIVE

        RouteDemonstrateSimulatorProvider.getInstance(route, object : InitializationCallback<RouteDemonstrateSimulator> {
            override fun onInstance(simulator: RouteDemonstrateSimulator) {
                with(simulator) {
                    this@RouteDemonstrationManagerImpl.simulator = this
                    start()
                    setSpeedMultiplier(speedMultiplier.value!!)
                    addPositionSimulatorListener(object : PositionSimulator.PositionSimulatorListener {
                        override fun onSimulatedPositionChanged(position: GeoPosition, progress: Float) { currentPosition.value = position }
                        override fun onSimulatedStateChanged(@PositionSimulator.SimulatorState state: Int) { /* Currently do nothing */ }
                    })
                }
            }
            override fun onError(@SygicContext.OnInitListener.Result result: Int) {}
        })
    }

    override fun restart() {
        simulator?.let {
            it.start()
            demonstrationState.value = DemonstrationState.ACTIVE
        }
    }

    override fun pause() {
        simulator?.let {
            if (demonstrationState.value!! == DemonstrationState.ACTIVE) {
                it.pause()
                demonstrationState.value = DemonstrationState.PAUSED
            }
        }
    }

    override fun unPause() {
        simulator?.let {
            if (demonstrationState.value!! == DemonstrationState.PAUSED) {
                it.start()
                demonstrationState.value = DemonstrationState.ACTIVE
            }
        }
    }

    override fun stop() {
        simulator?.let {
            it.stop()
            demonstrationState.value = DemonstrationState.STOPPED
            demonstrationState.value = DemonstrationState.INACTIVE
        }
    }

    override fun destroy() {
        simulator?.let {
            it.stop()
            it.destroy()
            simulator = null
        }
    }
}