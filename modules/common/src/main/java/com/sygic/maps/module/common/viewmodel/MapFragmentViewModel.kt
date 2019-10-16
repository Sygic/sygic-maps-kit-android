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

package com.sygic.maps.module.common.viewmodel

import android.app.Application
import android.os.Bundle
import androidx.annotation.RestrictTo
import androidx.lifecycle.DefaultLifecycleObserver
import com.sygic.maps.module.common.BuildConfig
import com.sygic.maps.module.common.KEY_DISTANCE_UNITS
import com.sygic.maps.module.common.component.DISTANCE_UNITS_DEFAULT_VALUE
import com.sygic.maps.module.common.theme.ThemeManager
import com.sygic.maps.uikit.viewmodels.common.position.PositionManagerClient
import com.sygic.maps.uikit.viewmodels.common.regional.RegionalManager
import com.sygic.maps.uikit.views.common.extensions.getParcelableValue
import com.sygic.maps.uikit.views.common.units.DistanceUnit

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
abstract class MapFragmentViewModel constructor(
    app: Application,
    arguments: Bundle?,
    themeManager: ThemeManager,
    private val regionalManager: RegionalManager,
    private val positionManagerClient: PositionManagerClient
) : ThemeSupportedViewModel(app, arguments, themeManager), DefaultLifecycleObserver {

    var distanceUnit: DistanceUnit
        get() = regionalManager.distanceUnit.value!!
        set(value) {
            regionalManager.distanceUnit.value = value
        }

    init {
        with(arguments) {
            distanceUnit = getParcelableValue(KEY_DISTANCE_UNITS) ?: DISTANCE_UNITS_DEFAULT_VALUE
        }

        if (BuildConfig.DEBUG) {
            positionManagerClient.remotePositioningServiceEnabled.value = true
        }
    }

    override fun onCleared() {
        super.onCleared()

        if (BuildConfig.DEBUG) {
            positionManagerClient.remotePositioningServiceEnabled.value = false
        }
    }
}