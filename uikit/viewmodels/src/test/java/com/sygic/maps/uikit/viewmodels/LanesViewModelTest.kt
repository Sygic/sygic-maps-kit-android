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
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.sygic.maps.uikit.viewmodels.common.navigation.NavigationManagerClient
import com.sygic.maps.uikit.viewmodels.navigation.lanes.LanesViewModel
import com.sygic.sdk.navigation.routeeventnotifications.LaneInfo
import com.sygic.sdk.navigation.routeeventnotifications.SimpleLaneInfo
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class LanesViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var navigationManagerClient: NavigationManagerClient

    private lateinit var lanesViewModel: LanesViewModel

    @Before
    fun setup() {
        lanesViewModel = LanesViewModel(navigationManagerClient)
    }

    @Test
    fun initTest() {
        verify(navigationManagerClient).addOnLanesListener(lanesViewModel)
    }

    @Test
    fun onLanesInfoChangedTest() {
        val laneInfoMock = mock<LaneInfo>()
        val simpleLaneInfoMock = mock<SimpleLaneInfo>()
        whenever(laneInfoMock.isActive).thenReturn(false)
        whenever(laneInfoMock.simpleLanesInfo).thenReturn(simpleLaneInfoMock)
        lanesViewModel.onLanesInfoChanged(laneInfoMock)

        lanesViewModel.enabled.test().assertValue(false)

        whenever(laneInfoMock.isActive).thenReturn(true)
        whenever(simpleLaneInfoMock.lanes).thenReturn(emptyList())
        lanesViewModel.onLanesInfoChanged(laneInfoMock)

        lanesViewModel.enabled.test().assertValue(false)

        whenever(laneInfoMock.isActive).thenReturn(true)
        whenever(simpleLaneInfoMock.lanes).thenReturn(emptyList())
        lanesViewModel.onLanesInfoChanged(laneInfoMock)

        lanesViewModel.enabled.test().assertValue(false)

        val firstLaneMock = mock<LaneInfo.Lane>()
        val secondLaneMock = mock<LaneInfo.Lane>()
        whenever(firstLaneMock.directions).thenReturn(listOf(LaneInfo.Lane.Direction.HalfRight, LaneInfo.Lane.Direction.Right))
        whenever(firstLaneMock.isHighlighted).thenReturn(false)
        whenever(secondLaneMock.isHighlighted).thenReturn(true)
        whenever(laneInfoMock.isActive).thenReturn(true)
        whenever(simpleLaneInfoMock.lanes).thenReturn(listOf(firstLaneMock, secondLaneMock))
        lanesViewModel.onLanesInfoChanged(laneInfoMock)

        lanesViewModel.enabled.test().assertValue(true)
        assertEquals(false, lanesViewModel.lanesData.value!!.first().highlighted)
        assertEquals(R.drawable.ic_lanedirection_right_half, lanesViewModel.lanesData.value!!.first().directions.first())
        assertEquals(R.drawable.ic_lanedirection_right, lanesViewModel.lanesData.value!!.first().directions[1])
        assertEquals(true, lanesViewModel.lanesData.value!![1].highlighted)
    }
}