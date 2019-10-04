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

package com.sygic.maps.uikit.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.jraska.livedata.test
import com.nhaarman.mockitokotlin2.*
import com.sygic.maps.uikit.viewmodels.common.regional.RegionalManager
import com.sygic.maps.uikit.viewmodels.navigation.speed.CurrentSpeedViewModel
import com.sygic.sdk.navigation.NavigationManager
import com.sygic.sdk.navigation.warnings.SpeedLimitInfo
import com.sygic.sdk.position.GeoPosition
import com.sygic.sdk.position.PositionManager
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import kotlin.math.roundToInt

private const val NOT_CHANGED_DEFAULT_VALUE_ONLY = 1

@RunWith(MockitoJUnitRunner::class)
class CurrentSpeedViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var positionManager: PositionManager
    @Mock
    private lateinit var regionalManager: RegionalManager
    @Mock
    private lateinit var navigationManager: NavigationManager

    private lateinit var currentSpeedViewModel: CurrentSpeedViewModel

    @Before
    fun setup() {
        whenever(regionalManager.distanceUnit).thenReturn(mock())

        currentSpeedViewModel = CurrentSpeedViewModel(regionalManager, navigationManager, positionManager)
    }

    @Test
    fun initTest() {
        verify(navigationManager).addOnSpeedLimitListener(currentSpeedViewModel)
        verify(positionManager).addPositionChangeListener(currentSpeedViewModel)
        verify(regionalManager.distanceUnit).observeForever(any())
    }

    @Test
    fun onPositionChangedTest() {
        currentSpeedViewModel.onPositionChanged(mock())
        currentSpeedViewModel.speeding.test().assertHistorySize(NOT_CHANGED_DEFAULT_VALUE_ONLY)
        currentSpeedViewModel.speedProgress.test().assertHistorySize(NOT_CHANGED_DEFAULT_VALUE_ONLY)
        currentSpeedViewModel.speedValue.test().assertHistorySize(NOT_CHANGED_DEFAULT_VALUE_ONLY)
    }

    @Test
    fun onSpeedLimitInfoChangedTest() {
        currentSpeedViewModel.onSpeedLimitInfoChanged(mock())
        currentSpeedViewModel.speeding.test().assertHistorySize(NOT_CHANGED_DEFAULT_VALUE_ONLY)
        currentSpeedViewModel.speedProgress.test().assertHistorySize(NOT_CHANGED_DEFAULT_VALUE_ONLY)
        currentSpeedViewModel.speedValue.test().assertHistorySize(NOT_CHANGED_DEFAULT_VALUE_ONLY)
    }

    @Test
    fun onPositionChangedAndOnSpeedLimitInfoChangedTest() {
        val geoPositionMock = mock<GeoPosition>()
        val speedLimitInfoMock = mock<SpeedLimitInfo>()
        val currentSpeedValue30 = 30.0
        val speedLimitValue = 50
        whenever(geoPositionMock.speed).thenReturn(currentSpeedValue30)
        whenever(speedLimitInfoMock.getSpeedLimit(speedLimitInfoMock.countrySpeedUnits)).thenReturn(speedLimitValue)
        currentSpeedViewModel.onPositionChanged(geoPositionMock)
        currentSpeedViewModel.onSpeedLimitInfoChanged(speedLimitInfoMock)

        verify(speedLimitInfoMock).getSpeedLimit(speedLimitInfoMock.countrySpeedUnits)

        currentSpeedViewModel.speeding.test().assertValue(false)
        currentSpeedViewModel.speedProgress.test().assertValue(60f)
        currentSpeedViewModel.speedValue.test().assertValue(currentSpeedValue30.roundToInt())

        val currentSpeedValue70 = 70.0
        whenever(geoPositionMock.speed).thenReturn(currentSpeedValue70)
        currentSpeedViewModel.onPositionChanged(geoPositionMock)
        currentSpeedViewModel.onSpeedLimitInfoChanged(speedLimitInfoMock)

        currentSpeedViewModel.speeding.test().assertValue(true)
        currentSpeedViewModel.speedProgress.test().assertValue(140f)
        currentSpeedViewModel.speedValue.test().assertValue(currentSpeedValue70.roundToInt())
    }
}