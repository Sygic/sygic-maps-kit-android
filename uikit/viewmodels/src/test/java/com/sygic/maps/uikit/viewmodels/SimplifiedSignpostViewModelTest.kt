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
import androidx.lifecycle.MutableLiveData
import com.jraska.livedata.test
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.sygic.maps.uikit.viewmodels.common.extensions.getDirectionDrawable
import com.sygic.maps.uikit.viewmodels.common.regional.RegionalManager
import com.sygic.maps.uikit.viewmodels.navigation.signpost.SimplifiedSignpostViewModel
import com.sygic.maps.uikit.views.common.units.DistanceUnit
import com.sygic.sdk.navigation.NavigationManager
import com.sygic.sdk.navigation.warnings.DirectionInfo
import com.sygic.sdk.route.RouteManeuver
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class SimplifiedSignpostViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var regionalManager: RegionalManager
    @Mock
    private lateinit var navigationManager: NavigationManager

    private lateinit var simplifiedSignpostViewModel: SimplifiedSignpostViewModel

    @Before
    fun setup() {
        whenever(regionalManager.distanceUnit).thenReturn(MutableLiveData(DistanceUnit.KILOMETERS))

        simplifiedSignpostViewModel = SimplifiedSignpostViewModel(regionalManager, navigationManager)
    }

    @Test
    fun initTest() {
        verify(navigationManager).addOnDirectionListener(simplifiedSignpostViewModel)
    }

    @Test
    fun onDirectionInfoChangedTest() {
        val nextRoadName = "Main road"
        val directionInfoMock = mock<DirectionInfo>()
        val primaryRouteManeuverMock = mock<RouteManeuver>()
        val secondaryRouteManeuverMock = mock<RouteManeuver>()
        whenever(primaryRouteManeuverMock.isValid).thenReturn(true)
        whenever(primaryRouteManeuverMock.nextRoadName).thenReturn(nextRoadName)
        whenever(primaryRouteManeuverMock.getDirectionDrawable()).thenReturn(RouteManeuver.Type.Right)
        whenever(secondaryRouteManeuverMock.isValid).thenReturn(true)
        whenever(secondaryRouteManeuverMock.getDirectionDrawable()).thenReturn(RouteManeuver.Type.End)
        whenever(directionInfoMock.primary).thenReturn(primaryRouteManeuverMock)
        whenever(directionInfoMock.secondary).thenReturn(secondaryRouteManeuverMock)
        whenever(directionInfoMock.distance).thenReturn(100)
        simplifiedSignpostViewModel.onDirectionInfoChanged(directionInfoMock)

        simplifiedSignpostViewModel.distance.test().assertValue("100 m")
        simplifiedSignpostViewModel.primaryDirection.test().assertValue(R.drawable.ic_direction_right_90)
        simplifiedSignpostViewModel.secondaryDirection.test().assertValue(R.drawable.ic_direction_finish)
        simplifiedSignpostViewModel.secondaryDirectionText.test().assertValue(R.string.then)
        simplifiedSignpostViewModel.secondaryDirectionContainerVisible.test().assertValue(true)
        assertEquals(nextRoadName, simplifiedSignpostViewModel.instructionText.value!!.getText(mock()))
    }
}