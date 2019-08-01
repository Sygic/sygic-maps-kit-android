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
import androidx.lifecycle.ViewModel
import com.sygic.maps.tools.annotations.AutoFactory
import com.sygic.maps.uikit.viewmodels.common.extensions.toSpeedLimitType
import com.sygic.maps.uikit.views.navigation.speed.SpeedLimitView
import com.sygic.maps.uikit.views.navigation.speed.limit.SpeedLimitType
import com.sygic.sdk.navigation.NavigationManager
import com.sygic.sdk.navigation.warnings.SpeedLimitInfo

/**
 * A [SpeedLimitViewModel] is a basic ViewModel implementation for the [SpeedLimitView] view class. It listens
 * to the [NavigationManager.OnSpeedLimitListener] and sets the appropriate state to the [SpeedLimitView] view.
 */
@AutoFactory
@Suppress("unused", "MemberVisibilityCanBePrivate")
open class SpeedLimitViewModel internal constructor(
    private val navigationManager: NavigationManager
) : ViewModel(), NavigationManager.OnSpeedLimitListener {

    val speedLimitType: MutableLiveData<SpeedLimitType> = MutableLiveData(SpeedLimitType.EU)
    val speedLimitValue: MutableLiveData<Int> = MutableLiveData(0)
    val speedLimitVisible: MutableLiveData<Boolean> = MutableLiveData(false)

    init {
        navigationManager.addOnSpeedLimitListener(this)
    }

    override fun onSpeedLimitInfoChanged(speedLimitInfo: SpeedLimitInfo) {
        speedLimitValue.value = speedLimitInfo.getSpeedLimit(speedLimitInfo.countrySpeedUnits)
        speedLimitType.value = speedLimitInfo.countrySignage.toSpeedLimitType()
        speedLimitVisible.value = speedLimitValue.value!! > 0
    }

    override fun onCleared() {
        super.onCleared()

        navigationManager.removeOnSpeedLimitListener(this)
    }
}