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

import androidx.lifecycle.*
import com.sygic.maps.tools.annotations.AutoFactory
import com.sygic.maps.uikit.viewmodels.common.navigation.NavigationManagerClient
import com.sygic.maps.uikit.viewmodels.common.position.PositionManagerClient
import com.sygic.maps.uikit.viewmodels.common.regional.RegionalManager
import com.sygic.maps.uikit.viewmodels.common.utils.Speed
import com.sygic.maps.uikit.views.common.extensions.asMutable
import com.sygic.maps.uikit.views.common.extensions.combineLatest
import com.sygic.maps.uikit.views.common.units.DistanceUnit
import com.sygic.maps.uikit.views.navigation.speed.CurrentSpeedView
import com.sygic.sdk.navigation.NavigationManager
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
    private val navigationManagerClient: NavigationManagerClient,
    private val positionManagerClient: PositionManagerClient
) : ViewModel(), DefaultLifecycleObserver {

    val speeding: LiveData<Boolean> = MutableLiveData(false)
    val speedValue: LiveData<Int> = MutableLiveData(0)
    val speedProgress: LiveData<Float> = MutableLiveData(0f)
    val speedUnit: LiveData<String> = MutableLiveData(Speed.getUnitFromDistanceUnit(DistanceUnit.KILOMETERS))

    private var currentDistanceUnit = regionalManager.distanceUnit.value!!

    override fun onCreate(owner: LifecycleOwner) {
        regionalManager.distanceUnit.observe(owner, Observer {
            speedValue.asMutable().value = Speed.convertValue(speedValue.value!!, currentDistanceUnit = currentDistanceUnit, targetDistanceUnit = it)
            speedUnit.asMutable().value = Speed.getUnitFromDistanceUnit(it)
            currentDistanceUnit = it
        })
        combineLatest(positionManagerClient.currentPosition, navigationManagerClient.speedLimitInfo)
            .observe(owner, Observer {
                with(it.first.speed.roundToInt()) {
                    speeding.asMutable().value = Speed.isSpeeding(this, it.second, currentDistanceUnit)
                    speedProgress.asMutable().value = Speed.speedProgress(this, it.second, currentDistanceUnit)
                    speedValue.asMutable().value = Speed.convertValue(this, DistanceUnit.KILOMETERS, currentDistanceUnit)
                }
            })
    }
}