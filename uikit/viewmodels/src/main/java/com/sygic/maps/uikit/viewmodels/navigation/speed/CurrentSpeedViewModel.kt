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

package com.sygic.maps.uikit.viewmodels.navigation.speed

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.sygic.maps.tools.annotations.AutoFactory
import com.sygic.maps.uikit.viewmodels.common.regional.RegionalManager
import com.sygic.maps.uikit.viewmodels.common.regional.units.DistanceUnit
import com.sygic.maps.uikit.viewmodels.common.utils.Speed
import com.sygic.maps.uikit.views.common.extensions.combineLatest
import com.sygic.maps.uikit.views.navigation.speed.CurrentSpeedView
import com.sygic.sdk.navigation.NavigationManager
import com.sygic.sdk.navigation.warnings.SpeedLimitInfo
import com.sygic.sdk.position.GeoPosition
import com.sygic.sdk.position.PositionManager
import kotlin.math.roundToInt

/**
 * A [CurrentSpeedViewModel] is a basic ViewModel implementation for the [CurrentSpeedView] view class. It listens
 * to the [NavigationManager.OnSpeedLimitListener] and [PositionManager.PositionChangeListener] and sets the appropriate
 * state to the [CurrentSpeedView] view.
 */
@AutoFactory
@Suppress("unused", "MemberVisibilityCanBePrivate")
open class CurrentSpeedViewModel internal constructor(
    private val regionalManager: RegionalManager,
    private val navigationManager: NavigationManager,
    private val positionManager: PositionManager
) : ViewModel(), NavigationManager.OnSpeedLimitListener, PositionManager.PositionChangeListener {

    val speeding: MutableLiveData<Boolean> = MutableLiveData(false)
    val speedValue: MutableLiveData<Int> = MutableLiveData(0)
    val speedProgress: MutableLiveData<Float> = MutableLiveData(0f)
    val speedUnit: MutableLiveData<String> = MutableLiveData(Speed.getUnitFromDistanceUnit(DistanceUnit.KILOMETERS))

    private var distanceUnit: DistanceUnit = DistanceUnit.KILOMETERS
    private val distanceUnitObserver = Observer<DistanceUnit> {
        speedValue.value = Speed.convertValue(speedValue.value!!, currentDistanceUnit = distanceUnit, targetDistanceUnit = it)
        speedUnit.value = Speed.getUnitFromDistanceUnit(it)
        distanceUnit = it
    }

    private val currentSpeedObserver: MutableLiveData<Int> = MutableLiveData()
    private val speedLimitInfoObserver: MutableLiveData<SpeedLimitInfo> = MutableLiveData()

    init {
        navigationManager.addOnSpeedLimitListener(this)
        positionManager.addPositionChangeListener(this)
        regionalManager.distanceUnit.observeForever(distanceUnitObserver)
        currentSpeedObserver.combineLatest(speedLimitInfoObserver).observeForever {
            speeding.value = Speed.isSpeeding(it.first, it.second, distanceUnit)
            speedProgress.value = Speed.speedProgress(it.first, it.second, distanceUnit)
            speedValue.value = Speed.convertValue(it.first, targetDistanceUnit = distanceUnit)
        }
    }

    override fun onSpeedLimitInfoChanged(speedLimitInfo: SpeedLimitInfo) {
        speedLimitInfoObserver.value = speedLimitInfo
    }

    override fun onPositionChanged(geoPosition: GeoPosition) {
        currentSpeedObserver.value = geoPosition.speed.roundToInt()
    }

    override fun onCleared() {
        super.onCleared()

        navigationManager.removeOnSpeedLimitListener(this)
        positionManager.removePositionChangeListener(this)
        regionalManager.distanceUnit.removeObserver(distanceUnitObserver)
    }
}