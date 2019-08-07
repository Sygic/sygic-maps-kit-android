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

package com.sygic.maps.uikit.viewmodels.navigation.signpost

import androidx.annotation.CallSuper
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.sygic.maps.uikit.viewmodels.R
import com.sygic.maps.uikit.viewmodels.common.extensions.getDirectionDrawable
import com.sygic.maps.uikit.viewmodels.common.extensions.getDistanceWithUnits
import com.sygic.maps.uikit.viewmodels.common.regional.RegionalManager
import com.sygic.maps.uikit.viewmodels.common.regional.units.DistanceUnit
import com.sygic.maps.uikit.viewmodels.navigation.signpost.direction.DirectionManeuverType
import com.sygic.maps.uikit.views.common.utils.TextHolder
import com.sygic.sdk.navigation.NavigationManager
import com.sygic.sdk.navigation.warnings.DirectionInfo

@Suppress("unused", "MemberVisibilityCanBePrivate")
abstract class BaseSignpostViewModel(
    private val regionalManager: RegionalManager,
    private val navigationManager: NavigationManager
) : ViewModel(), NavigationManager.OnDirectionListener {

    val distance: MutableLiveData<String> = MutableLiveData()
    val primaryDirection: MutableLiveData<Int> = MutableLiveData()
    val secondaryDirection: MutableLiveData<Int> = MutableLiveData()
    val secondaryDirectionText: Int = R.string.then
    val secondaryDirectionContainerVisible: MutableLiveData<Boolean> = MutableLiveData(false)
    val instructionText: MutableLiveData<TextHolder> = MutableLiveData(TextHolder.empty)

    protected var distanceUnit: DistanceUnit = DistanceUnit.KILOMETERS
    protected val directionInfo: MutableLiveData<DirectionInfo?> = MutableLiveData()

    private val distanceUnitObserver = Observer<DistanceUnit> { distanceUnit = it }
    private val directionInfoObserver = Observer<DirectionInfo?> { directionInfo ->
        directionInfo?.let {
            distance.value = it.getDistanceWithUnits(distanceUnit)
            primaryDirection.value = it.getDirectionDrawable(DirectionManeuverType.PRIMARY)
            secondaryDirection.value = it.getDirectionDrawable(DirectionManeuverType.SECONDARY)
            secondaryDirectionContainerVisible.value = it.secondary.isValid
        }
    }

    init {
        navigationManager.addOnDirectionListener(this)
        directionInfo.observeForever(directionInfoObserver)
        regionalManager.distanceUnit.observeForever(distanceUnitObserver)
    }

    @CallSuper
    override fun onDirectionInfoChanged(directionInfo: DirectionInfo) {
        this.directionInfo.value = directionInfo
    }

    override fun onCleared() {
        super.onCleared()

        navigationManager.removeOnDirectionListener(this)
        regionalManager.distanceUnit.removeObserver(distanceUnitObserver)
    }
}