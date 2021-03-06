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

package com.sygic.maps.uikit.viewmodels.navigation.infobar

import androidx.lifecycle.*
import com.sygic.maps.tools.annotations.AutoFactory
import com.sygic.maps.uikit.viewmodels.common.datetime.DateTimeManager
import com.sygic.maps.uikit.viewmodels.common.navigation.NavigationManagerClient
import com.sygic.maps.uikit.viewmodels.common.position.PositionManagerClient
import com.sygic.maps.uikit.viewmodels.common.regional.RegionalManager
import com.sygic.maps.uikit.viewmodels.navigation.infobar.text.InfobarTextDataWrapper
import com.sygic.maps.uikit.viewmodels.navigation.infobar.text.InfobarTextType
import com.sygic.maps.uikit.viewmodels.navigation.infobar.text.data.DistanceData
import com.sygic.maps.uikit.viewmodels.navigation.infobar.text.data.PositionData
import com.sygic.maps.uikit.viewmodels.navigation.infobar.text.data.TimeData
import com.sygic.maps.uikit.viewmodels.navigation.infobar.text.items.ActualElevationInfobarItem
import com.sygic.maps.uikit.viewmodels.navigation.infobar.text.items.EstimatedTimeInfobarItem
import com.sygic.maps.uikit.viewmodels.navigation.infobar.text.items.RemainingDistanceInfobarItem
import com.sygic.maps.uikit.viewmodels.navigation.infobar.text.items.RemainingTimeInfobarItem
import com.sygic.maps.uikit.views.common.extensions.SPACE
import com.sygic.maps.uikit.views.common.extensions.VERTICAL_BAR
import com.sygic.maps.uikit.views.common.extensions.asMutable
import com.sygic.maps.uikit.views.common.extensions.observeOnce
import com.sygic.maps.uikit.views.common.units.DistanceUnit
import com.sygic.maps.uikit.views.navigation.infobar.Infobar
import com.sygic.maps.uikit.views.navigation.infobar.items.InfobarTextData
import com.sygic.sdk.navigation.RouteProgress

/**
 * A [InfobarViewModel] is a basic ViewModel implementation for the [Infobar] view class. It listens
 * to the [NavigationManagerClient] `routeProgress`  and set appropriate state to the [Infobar] view.
 * It also listens to the [Infobar] left and right buttons actions.
 */
@AutoFactory
@Suppress("unused", "MemberVisibilityCanBePrivate")
open class InfobarViewModel internal constructor(
    private val regionalManager: RegionalManager,
    private val dateTimeManager: DateTimeManager,
    private val positionManagerClient: PositionManagerClient,
    private val navigationManagerClient: NavigationManagerClient
) : ViewModel(), DefaultLifecycleObserver {

    val textDataPrimary: LiveData<InfobarTextData> = MutableLiveData(InfobarTextData.empty)
    val textDataSecondary: LiveData<InfobarTextData> = MutableLiveData(InfobarTextData.empty)
    private val textDataMap: Map<InfobarTextType, InfobarTextData> = mutableMapOf()

    private var distanceUnit: DistanceUnit = DistanceUnit.KILOMETERS
    private val distanceUnitObserver = Observer<DistanceUnit> { distanceUnit = it }

    init {
        regionalManager.distanceUnit.observeForever(distanceUnitObserver)

        setTextData(
            InfobarTextType.PRIMARY, InfobarTextData(
                items = arrayOf(RemainingTimeInfobarItem())
            )
        )

        setTextData(
            InfobarTextType.SECONDARY, InfobarTextData(
                items = arrayOf(
                    RemainingDistanceInfobarItem(),
                    ActualElevationInfobarItem(),
                    EstimatedTimeInfobarItem()
                ),
                divider = SPACE + VERTICAL_BAR + SPACE
            )
        )
    }

    override fun onCreate(owner: LifecycleOwner) {
        if (owner is InfobarTextDataWrapper) {
            owner.infobarTextDataProvider.observe(owner, Observer { map ->
                map.forEach { setTextData(it.key, it.value) }
            })
        }

        navigationManagerClient.routeProgress.observe(owner, Observer { onRouteProgressChanged(it) })
    }

    private fun onRouteProgressChanged(routeProgress: RouteProgress) {
        if (routeProgress.distanceToEnd == 0
            && routeProgress.timeToEnd == 0
            && routeProgress.timeToEndWithSpeedProfiles == 0
            && routeProgress.timeToEndWithSpeedProfileAndTraffic == 0
            && routeProgress.progress == 0F
        ) {
            return
        }

        positionManagerClient.lastKnownPosition.observeOnce {
            val positionData = PositionData(it.coordinates)
            val distanceData = DistanceData(routeProgress.distanceToEnd, distanceUnit)
            val timeData = TimeData(
                dateTimeManager, routeProgress.timeToEnd,
                routeProgress.timeToEndWithSpeedProfiles,
                routeProgress.timeToEndWithSpeedProfileAndTraffic
            )

            updateTextData(positionData, distanceData, timeData)
        }
    }

    private fun updateTextData(
        positionData: PositionData,
        distanceData: DistanceData,
        timeData: TimeData
    ) {
        textDataMap.forEach {
            setTextData(it.key, it.value.also { infobarTextData ->
                infobarTextData.items.forEach { item ->
                    when (item) {
                        is ActualElevationInfobarItem -> item.update(positionData)
                        is EstimatedTimeInfobarItem -> item.update(timeData)
                        is RemainingDistanceInfobarItem -> item.update(distanceData)
                        is RemainingTimeInfobarItem -> item.update(timeData)
                        else -> item.update(null)
                    }
                }
            })
        }
    }

    fun setTextData(textType: InfobarTextType, data: InfobarTextData) {
        (textDataMap as MutableMap)[textType] = data
        when (textType) {
            InfobarTextType.PRIMARY -> textDataPrimary.asMutable().value = data
            InfobarTextType.SECONDARY -> textDataSecondary.asMutable().value = data
        }
    }

    fun getTextData(textType: InfobarTextType): InfobarTextData = textDataMap[textType] ?: InfobarTextData.empty

    override fun onCleared() {
        super.onCleared()

        regionalManager.distanceUnit.removeObserver(distanceUnitObserver)
    }
}