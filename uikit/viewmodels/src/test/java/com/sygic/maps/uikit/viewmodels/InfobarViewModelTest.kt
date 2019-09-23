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

import android.os.Parcel
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import com.nhaarman.mockitokotlin2.*
import com.sygic.maps.uikit.viewmodels.common.datetime.DateTimeManager
import com.sygic.maps.uikit.viewmodels.common.regional.RegionalManager
import com.sygic.maps.uikit.viewmodels.navigation.infobar.InfobarViewModel
import com.sygic.maps.uikit.viewmodels.navigation.infobar.text.InfobarTextDataWrapper
import com.sygic.maps.uikit.viewmodels.navigation.infobar.text.InfobarTextType
import com.sygic.maps.uikit.viewmodels.navigation.infobar.text.data.ProgressData
import com.sygic.maps.uikit.viewmodels.navigation.infobar.text.items.ActualElevationInfobarItem
import com.sygic.maps.uikit.viewmodels.navigation.infobar.text.items.EstimatedTimeInfobarItem
import com.sygic.maps.uikit.viewmodels.navigation.infobar.text.items.RemainingDistanceInfobarItem
import com.sygic.maps.uikit.viewmodels.navigation.infobar.text.items.RemainingTimeInfobarItem
import com.sygic.maps.uikit.views.common.extensions.SPACE
import com.sygic.maps.uikit.views.common.extensions.VERTICAL_BAR
import com.sygic.maps.uikit.views.navigation.infobar.items.InfobarItem
import com.sygic.maps.uikit.views.navigation.infobar.items.InfobarTextData
import com.sygic.sdk.navigation.NavigationManager
import com.sygic.sdk.position.GeoCoordinates
import com.sygic.sdk.position.GeoPosition
import com.sygic.sdk.position.PositionManager
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class InfobarViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var regionalManager: RegionalManager
    @Mock
    private lateinit var dateTimeManager: DateTimeManager
    @Mock
    private lateinit var positionManager: PositionManager
    @Mock
    private lateinit var navigationManager: NavigationManager

    private lateinit var infobarViewModel: InfobarViewModel

    @Before
    fun setup() {
        whenever(regionalManager.distanceUnit).thenReturn(mock())
        whenever(positionManager.lastKnownPosition).thenReturn(GeoPosition(GeoCoordinates(50.14, 9.22), 10.0, 0.0))
        whenever(dateTimeManager.formatTime(any())).thenReturn("12:22")

        infobarViewModel = InfobarViewModel(regionalManager, dateTimeManager, positionManager, navigationManager)
    }

    @Test
    fun initTest() {
        verify(navigationManager).addOnNaviStatsListener(infobarViewModel)
        verify(regionalManager.distanceUnit).observeForever(any())
        assertEquals(true, infobarViewModel.textDataPrimary.value!!.items.first() is RemainingTimeInfobarItem)
        assertEquals(true, infobarViewModel.textDataSecondary.value!!.items.first() is RemainingDistanceInfobarItem)
        assertEquals(true, infobarViewModel.textDataSecondary.value!!.items[1] is ActualElevationInfobarItem)
        assertEquals(true, infobarViewModel.textDataSecondary.value!!.items[2] is EstimatedTimeInfobarItem)
        assertEquals(SPACE + VERTICAL_BAR + SPACE, infobarViewModel.textDataSecondary.value!!.divider)
    }

    @Test
    fun onCreateTest() {
        val infobarTextDataWrapperProviderComponentMock = mock<LiveData<InfobarTextDataWrapper.ProviderComponent>>()
        val infobarTextDataWrapperLifecycleOwnerMock = mock<LifecycleOwner>(extraInterfaces = arrayOf(InfobarTextDataWrapper::class))
        whenever((infobarTextDataWrapperLifecycleOwnerMock as InfobarTextDataWrapper).infobarTextDataProvider).thenReturn(
            infobarTextDataWrapperProviderComponentMock
        )
        infobarViewModel.onCreate(infobarTextDataWrapperLifecycleOwnerMock)
        verify(infobarTextDataWrapperProviderComponentMock).observe(eq(infobarTextDataWrapperLifecycleOwnerMock), any())
    }

    @Test
    fun setTextDataTest() {
        val primaryInfobarItemFirst = object : InfobarItem<ProgressData> {
            override var text: CharSequence = "Test"
            override fun update(data: ProgressData?) {}
            override fun writeToParcel(dest: Parcel?, flags: Int) {}
            override fun describeContents() = 0

        }
        val primaryInfobarItemThird = object : InfobarItem<ProgressData> {
            override var text: CharSequence = ""
            override fun update(data: ProgressData?) { data?.let { text = "Progress is: ${it.routeProgress}" } }
            override fun writeToParcel(dest: Parcel?, flags: Int) {}
            override fun describeContents() = 0

        }
        val infobarTextDataPrimary = InfobarTextData(
            items = arrayOf(primaryInfobarItemFirst, RemainingTimeInfobarItem(), primaryInfobarItemThird)
        )
        infobarViewModel.setTextData(InfobarTextType.PRIMARY, infobarTextDataPrimary)
        assertEquals(true, infobarViewModel.textDataPrimary.value!! == infobarTextDataPrimary)
        assertEquals(infobarTextDataPrimary, infobarViewModel.getTextData(InfobarTextType.PRIMARY))

        val infobarTextDataSecondary = InfobarTextData(
            items = arrayOf(RemainingTimeInfobarItem())
        )
        infobarViewModel.setTextData(InfobarTextType.SECONDARY, infobarTextDataSecondary)
        assertEquals(true, infobarViewModel.textDataSecondary.value!! == infobarTextDataSecondary)
        assertEquals(infobarTextDataSecondary, infobarViewModel.getTextData(InfobarTextType.SECONDARY))
    }

    @Test
    fun onNaviStatsChangedTest() {
        val distanceValue = 1000
        val primaryInfobarItemMock = mock<RemainingDistanceInfobarItem>()
        whenever(primaryInfobarItemMock.text).thenReturn(distanceValue.toString())
        infobarViewModel.setTextData(InfobarTextType.PRIMARY, InfobarTextData(arrayOf(primaryInfobarItemMock)))

        infobarViewModel.onNaviStatsChanged(0, 0, 0, 0, 0)
        verify(primaryInfobarItemMock, never()).update(anyOrNull())

        infobarViewModel.onNaviStatsChanged(distanceValue, 0, 0, 0, 0)
        verify(primaryInfobarItemMock).update(any())
        assertEquals(true, infobarViewModel.textDataPrimary.value!!.items.first().text == distanceValue.toString())
    }
}