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
import com.sygic.sdk.route.RouteInfo
import com.sygic.sdk.route.simulator.RouteDemonstrateSimulator

const val DEFAULT_SPEED = 1F

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
object RouteDemonstrationManagerImpl : RouteDemonstrationManager {

    private var routeDemonstrateSimulator: RouteDemonstrateSimulator? = null

    override var speedMultiplier = MutableLiveData<Float>(DEFAULT_SPEED)
    override val demonstrationState = MutableLiveData<DemonstrationState>(DemonstrationState.INACTIVE)

    init {
        speedMultiplier.observeForever { routeDemonstrateSimulator?.setSpeedMultiplier(it) }
    }

    override fun start(routeInfo: RouteInfo) {
        destroy()
        demonstrationState.value = DemonstrationState.ACTIVE
        routeDemonstrateSimulator = RouteDemonstrateSimulator(routeInfo).also {
            it.start()
            it.setSpeedMultiplier(speedMultiplier.value!!)
        }
    }

    override fun restart() {
        routeDemonstrateSimulator?.let {
            it.start()
            demonstrationState.value = DemonstrationState.ACTIVE
        }
    }

    override fun pause() {
        routeDemonstrateSimulator?.let {
            if (demonstrationState.value!! == DemonstrationState.ACTIVE) {
                it.pause()
                demonstrationState.value = DemonstrationState.PAUSED
            }
        }
    }

    override fun unPause() {
        routeDemonstrateSimulator?.let {
            if (demonstrationState.value!! == DemonstrationState.PAUSED) {
                it.start()
                demonstrationState.value = DemonstrationState.ACTIVE
            }
        }
    }

    override fun stop() {
        routeDemonstrateSimulator?.let {
            it.stop()
            demonstrationState.value = DemonstrationState.INACTIVE
        }
    }

    override fun destroy() {
        routeDemonstrateSimulator?.let {
            it.stop()
            it.destroy()
            routeDemonstrateSimulator = null
        }
    }
}