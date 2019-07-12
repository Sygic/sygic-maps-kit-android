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

package com.sygic.maps.uikit.viewmodels.navigation

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.sygic.maps.uikit.viewmodels.common.extensions.getDirectionDrawable
import com.sygic.maps.uikit.viewmodels.common.extensions.getDistanceWithUnits
import com.sygic.maps.uikit.viewmodels.common.extensions.getNaviSignInfoOnRoute
import com.sygic.maps.uikit.viewmodels.common.regional.RegionalManager
import com.sygic.maps.uikit.viewmodels.common.regional.units.DistanceUnits
import com.sygic.maps.uikit.viewmodels.navigation.direction.DirectionManeuverType
import com.sygic.sdk.navigation.NavigationManager
import com.sygic.sdk.navigation.warnings.DirectionInfo
import com.sygic.sdk.navigation.warnings.NaviSignInfo

abstract class BaseSignpostViewModel(
    private val regionalManager: RegionalManager,
    private val navigationManager: NavigationManager
) : ViewModel(), DefaultLifecycleObserver, NavigationManager.OnDirectionListener, NavigationManager.OnNaviSignListener {

    val distance: MutableLiveData<String> = MutableLiveData()
    val primaryDirection: MutableLiveData<Int> = MutableLiveData()

    private var distanceUnits: DistanceUnits = DistanceUnits.KILOMETERS
    private var directionInfo: DirectionInfo? = null
        set(value) {
            value?.let {
                if (field != it) {
                    field = it
                    distance.value = it.getDistanceWithUnits(distanceUnits)
                    primaryDirection.value = it.getDirectionDrawable(DirectionManeuverType.PRIMARY)
                }
            }
        }

    init {
        navigationManager.addOnNaviSignListener(this)
        navigationManager.addOnDirectionListener(this)
    }

    override fun onCreate(owner: LifecycleOwner) {
        regionalManager.distanceUnits.observe(owner, Observer { distanceUnits ->
            this.distanceUnits = distanceUnits
        })
    }

    override fun onDirectionInfoChanged(directionInfo: DirectionInfo) {
        this.directionInfo = directionInfo
    }

    override fun onNaviSignChanged(naviSignInfoList: List<NaviSignInfo>) {
        naviSignInfoList.getNaviSignInfoOnRoute()?.let {
            //todo
        }
    }

    override fun onCleared() {
        super.onCleared()

        navigationManager.removeOnNaviSignListener(this)
        navigationManager.removeOnDirectionListener(this)
    }
}