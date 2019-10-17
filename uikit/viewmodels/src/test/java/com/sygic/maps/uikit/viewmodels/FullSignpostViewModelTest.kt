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
import com.sygic.maps.uikit.viewmodels.common.navigation.NavigationManagerClient
import com.sygic.maps.uikit.viewmodels.common.regional.RegionalManager
import com.sygic.maps.uikit.viewmodels.navigation.signpost.FullSignpostViewModel
import com.sygic.maps.uikit.views.common.units.DistanceUnit
import com.sygic.sdk.navigation.routeeventnotifications.DirectionInfo
import com.sygic.sdk.navigation.routeeventnotifications.SignpostInfo
import com.sygic.sdk.route.RouteManeuver
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class FullSignpostViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var regionalManager: RegionalManager
    @Mock
    private lateinit var navigationManagerClient: NavigationManagerClient

    private lateinit var fullSignpostViewModel: FullSignpostViewModel

    @Before
    fun setup() {
        whenever(regionalManager.distanceUnit).thenReturn(MutableLiveData(DistanceUnit.KILOMETERS))

        fullSignpostViewModel = FullSignpostViewModel(regionalManager, navigationManagerClient)
    }

    @Test
    fun initTest() {
        verify(navigationManagerClient).addOnNaviSignListener(fullSignpostViewModel)
        verify(navigationManagerClient).addOnDirectionListener(fullSignpostViewModel)
    }

    @Test
    fun onDirectionInfoChangedTest() {
        val nextRoadName = "Main road"
        val placeName = "London eye"
        val directionInfoMock = mock<DirectionInfo>()
        val signpostInfoMock = mock<SignpostInfo>()
        val placeNaviSignInfoSignElementMock = mock<SignpostInfo.SignElement>()
        val pictogramNaviSignInfoSignElementMock = mock<SignpostInfo.SignElement>()
        val routeNumberNaviSignInfoSignElementMock = mock<SignpostInfo.SignElement>()
        val naviSignElements = listOf(placeNaviSignInfoSignElementMock, pictogramNaviSignInfoSignElementMock, routeNumberNaviSignInfoSignElementMock)
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
        whenever(signpostInfoMock.isOnRoute).thenReturn(true)
        whenever(signpostInfoMock.signElements).thenReturn(naviSignElements)
        whenever(placeNaviSignInfoSignElementMock.elementType).thenReturn(SignpostInfo.SignElement.SignElementType.PlaceName)
        whenever(placeNaviSignInfoSignElementMock.text).thenReturn(placeName)
        whenever(pictogramNaviSignInfoSignElementMock.pictogramType).thenReturn(SignpostInfo.SignElement.PictogramType.Airport)
        whenever(pictogramNaviSignInfoSignElementMock.elementType).thenReturn(SignpostInfo.SignElement.SignElementType.Pictogram)
        whenever(routeNumberNaviSignInfoSignElementMock.elementType).thenReturn(SignpostInfo.SignElement.SignElementType.RouteNumber)
        whenever(routeNumberNaviSignInfoSignElementMock.roadNumberFormat).thenReturn(mock())
        whenever(routeNumberNaviSignInfoSignElementMock.roadNumberFormat.insideNumber).thenReturn("5")

        fullSignpostViewModel.onDirectionInfoChanged(directionInfoMock)
        fullSignpostViewModel.onNaviSignChanged(listOf(signpostInfoMock))

        fullSignpostViewModel.distance.test().assertValue("100 m")
        fullSignpostViewModel.primaryDirection.test().assertValue(R.drawable.ic_direction_right_90)
        fullSignpostViewModel.secondaryDirection.test().assertValue(R.drawable.ic_direction_finish)
        fullSignpostViewModel.secondaryDirectionText.test().assertValue(R.string.then)
        fullSignpostViewModel.secondaryDirectionContainerVisible.test().assertValue(true)
        fullSignpostViewModel.pictogram.test().assertValue(R.drawable.ic_pictogram_airport)
        assertEquals(true, fullSignpostViewModel.roadSigns.value!!.isNotEmpty())
        assertEquals(placeName, fullSignpostViewModel.instructionText.value!!.getText(mock()))
    }
}