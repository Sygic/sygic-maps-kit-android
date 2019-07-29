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

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.sygic.maps.tools.annotations.AutoFactory
import com.sygic.maps.uikit.viewmodels.common.datetime.DateTimeManager
import com.sygic.maps.uikit.viewmodels.common.navigation.preview.RouteDemonstrationManager
import com.sygic.maps.uikit.viewmodels.common.regional.RegionalManager
import com.sygic.maps.uikit.viewmodels.common.regional.units.DistanceUnits
import com.sygic.maps.uikit.viewmodels.common.utils.Distance
import com.sygic.maps.uikit.viewmodels.common.utils.Elevation
import com.sygic.maps.uikit.viewmodels.common.utils.Time
import com.sygic.maps.uikit.views.common.extensions.SPACE
import com.sygic.maps.uikit.views.common.extensions.asSingleEvent
import com.sygic.maps.uikit.views.common.livedata.SingleLiveEvent
import com.sygic.maps.uikit.views.navigation.infobar.Infobar
import com.sygic.maps.uikit.views.navigation.infobar.items.InfobarItemsHolder
import com.sygic.maps.uikit.views.navigation.preview.RoutePreviewControls
import com.sygic.sdk.navigation.NavigationManager
import com.sygic.sdk.position.PositionManager
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * A [InfobarViewModel] is a basic ViewModel implementation for the [Infobar] view class. It listens
 * to the [NavigationManager.OnNaviStatsListener] and set appropriate state to the [Infobar] view.
 * It also listens to the [Infobar] left and right buttons actions.
 */
@AutoFactory
@Suppress("unused", "MemberVisibilityCanBePrivate")
open class InfobarViewModel internal constructor(
    private val regionalManager: RegionalManager,
    private val dateTimeManager: DateTimeManager,
    private val positionManager: PositionManager,
    private val navigationManager: NavigationManager
) : ViewModel(), NavigationManager.OnNaviStatsListener {

    val leftButtonVisible = true
    val rightButtonVisible = true

    val primaryItemsHolder = MutableLiveData(InfobarItemsHolder(divider = SPACE))
    val secondaryItemsHolder = MutableLiveData(InfobarItemsHolder.empty())

    val activityFinishObservable: LiveData<Any> = SingleLiveEvent()

    private var distanceUnits: DistanceUnits = DistanceUnits.KILOMETERS
    private val distanceUnitsObserver = Observer<DistanceUnits> { distanceUnits -> this.distanceUnits = distanceUnits }

    init {
        navigationManager.addOnNaviStatsListener(this)
        regionalManager.distanceUnits.observeForever(distanceUnitsObserver)
    }

    override fun onNaviStatsChanged(
        distanceToEnd: Int, timeToEndIdeal: Int, timeToEndWithSpeedProfile: Int,
        timeToEndWithSpeedProfileAndTraffic: Int, routeProgress: Int
    ) {
        if (distanceToEnd == 0 && timeToEndIdeal == 0 && timeToEndWithSpeedProfile == 0
            && timeToEndWithSpeedProfileAndTraffic == 0 && routeProgress == 0
        ) {
            return
        }

        primaryItemsHolder.value = InfobarItemsHolder(
            formatRemainingTime(timeToEndWithSpeedProfileAndTraffic)
        )
        secondaryItemsHolder.value = InfobarItemsHolder(
            formatRemainingDistance(distanceToEnd),
            formatCurrentElevation(),
            formatEstimatedTime(timeToEndWithSpeedProfileAndTraffic)
        )
    }

    open fun formatRemainingTime(time: Int) = Time.getFormattedTime(time)

    open fun formatRemainingDistance(distance: Int) = Distance.getFormattedDistance(distanceUnits, distance)

    //TODO: Waiting from SDK PR (https://git.sygic.com/projects/NAVI/repos/sdk/pull-requests/3903/overview)
    open fun formatCurrentElevation() = Elevation.getFormattedElevation(/*positionManager.lastKnownPosition.coordinates.altitude*/ 600)

    open fun formatEstimatedTime(estimatedTime: Int) =
        dateTimeManager.formatTime(Date(System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(estimatedTime.toLong())))

    open fun onLeftButtonClick() {
        //todo
    }

    open fun onRightButtonClick() = activityFinishObservable.asSingleEvent().call()

    override fun onCleared() {
        super.onCleared()

        navigationManager.removeOnNaviStatsListener(this)
        regionalManager.distanceUnits.removeObserver(distanceUnitsObserver)
    }
}