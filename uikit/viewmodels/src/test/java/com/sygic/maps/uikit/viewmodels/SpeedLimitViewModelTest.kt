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
import com.sygic.maps.uikit.viewmodels.navigation.speed.SpeedLimitViewModel
import com.sygic.maps.uikit.views.navigation.speed.limit.SpeedLimitType
import com.sygic.sdk.map.MapView
import com.sygic.sdk.navigation.NavigationManager
import com.sygic.sdk.navigation.warnings.SpeedLimitInfo
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class SpeedLimitViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var navigationManager: NavigationManager

    private lateinit var speedLimitViewModel: SpeedLimitViewModel

    @Before
    fun setup() {
        speedLimitViewModel = SpeedLimitViewModel(navigationManager)
    }

    @Test
    fun initTest() {
        verify(navigationManager).addOnSpeedLimitListener(speedLimitViewModel)
        assertEquals(SpeedLimitType.EU, speedLimitViewModel.speedLimitType.value!!)
    }

    @Test
    fun onSpeedLimitInfoChangedTest() {
        val speedLimitInfo = mock<SpeedLimitInfo>()
        val speedLimitValue50 = 50
        whenever(speedLimitInfo.getSpeedLimit(speedLimitInfo.countrySpeedUnits)).thenReturn(speedLimitValue50)
        whenever(speedLimitInfo.countrySignage).thenReturn(MapView.CountrySignage.World)
        speedLimitViewModel.onSpeedLimitInfoChanged(speedLimitInfo)

        verify(speedLimitInfo, times(2)).getSpeedLimit(speedLimitInfo.countrySpeedUnits)
        verify(speedLimitInfo).countrySignage

        speedLimitViewModel.speedLimitType.test().assertValue(SpeedLimitType.EU)
        speedLimitViewModel.speedLimitValue.test().assertValue(speedLimitValue50)
        speedLimitViewModel.speedLimitVisible.test().assertValue(true)

        val speedLimitValue0 = 0
        whenever(speedLimitInfo.getSpeedLimit(speedLimitInfo.countrySpeedUnits)).thenReturn(speedLimitValue0)
        whenever(speedLimitInfo.countrySignage).thenReturn(MapView.CountrySignage.World)
        speedLimitViewModel.onSpeedLimitInfoChanged(speedLimitInfo)

        speedLimitViewModel.speedLimitType.test().assertValue(SpeedLimitType.EU)
        speedLimitViewModel.speedLimitValue.test().assertValue(speedLimitValue0)
        speedLimitViewModel.speedLimitVisible.test().assertValue(false)

        val speedLimitValue100 = 100
        whenever(speedLimitInfo.getSpeedLimit(speedLimitInfo.countrySpeedUnits)).thenReturn(speedLimitValue100)
        whenever(speedLimitInfo.countrySignage).thenReturn(MapView.CountrySignage.America)
        speedLimitViewModel.onSpeedLimitInfoChanged(speedLimitInfo)

        speedLimitViewModel.speedLimitType.test().assertValue(SpeedLimitType.US)
        speedLimitViewModel.speedLimitValue.test().assertValue(speedLimitValue100)
        speedLimitViewModel.speedLimitVisible.test().assertValue(true)
    }
}