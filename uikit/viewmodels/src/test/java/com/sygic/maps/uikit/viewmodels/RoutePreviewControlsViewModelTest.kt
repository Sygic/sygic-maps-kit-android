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
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import com.jraska.livedata.test
import com.nhaarman.mockitokotlin2.*
import com.sygic.maps.uikit.viewmodels.common.navigation.preview.DEFAULT_SPEED
import com.sygic.maps.uikit.viewmodels.common.navigation.preview.RouteDemonstrationManager
import com.sygic.maps.uikit.viewmodels.common.navigation.preview.state.DemonstrationState
import com.sygic.maps.uikit.viewmodels.navigation.preview.RoutePreviewControlsViewModel
import com.sygic.maps.uikit.views.navigation.preview.state.PlayPauseButtonState
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class RoutePreviewControlsViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var routeDemonstrationManager: RouteDemonstrationManager

    private lateinit var routePreviewControlsViewModel: RoutePreviewControlsViewModel

    @Before
    fun setup() {
        whenever(routeDemonstrationManager.demonstrationState).thenReturn(mock())
        whenever(routeDemonstrationManager.speedMultiplier).thenReturn(MutableLiveData(DEFAULT_SPEED))
        routePreviewControlsViewModel = RoutePreviewControlsViewModel(routeDemonstrationManager)
    }

    @Test
    fun initTest() {
        assertEquals(PlayPauseButtonState.PLAY, routePreviewControlsViewModel.playPauseButtonState.value!!)
    }

    @Test
    fun onCreateTest() {
        val lifecycleOwnerMock = mock<LifecycleOwner>()
        routePreviewControlsViewModel.onCreate(lifecycleOwnerMock)
        verify(routeDemonstrationManager.demonstrationState).observe(eq(lifecycleOwnerMock), any())
    }

    @Test
    fun onPlayPauseButtonClickTest() {
        whenever(routeDemonstrationManager.demonstrationState).thenReturn(MutableLiveData(DemonstrationState.INACTIVE))
        routePreviewControlsViewModel.onPlayPauseButtonClick()
        verify(routeDemonstrationManager).restart()

        whenever(routeDemonstrationManager.demonstrationState).thenReturn(MutableLiveData(DemonstrationState.ACTIVE))
        routePreviewControlsViewModel.onPlayPauseButtonClick()
        verify(routeDemonstrationManager).pause()

        whenever(routeDemonstrationManager.demonstrationState).thenReturn(MutableLiveData(DemonstrationState.PAUSED))
        routePreviewControlsViewModel.onPlayPauseButtonClick()
        verify(routeDemonstrationManager).unPause()
    }

    @Test
    fun onSpeedButtonClickTest() {
        val expectedValue = routeDemonstrationManager.speedMultiplier.value!! * 2 % 15
        routePreviewControlsViewModel.onSpeedButtonClick()
        routeDemonstrationManager.speedMultiplier.test().assertValue(expectedValue)
    }

    @Test
    fun onStopButtonClickTest() {
        routePreviewControlsViewModel.onStopButtonClick()
        verify(routeDemonstrationManager).stop()
    }
}