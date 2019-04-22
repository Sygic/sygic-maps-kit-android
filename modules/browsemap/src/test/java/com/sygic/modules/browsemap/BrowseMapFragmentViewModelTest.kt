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

package com.sygic.modules.browsemap

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.jraska.livedata.test
import com.nhaarman.mockitokotlin2.*
import com.sygic.maps.module.browsemap.viewmodel.BrowseMapFragmentViewModel
import com.sygic.maps.module.common.component.MapFragmentInitComponent
import com.sygic.maps.module.common.detail.DetailsViewFactory
import com.sygic.maps.module.common.listener.OnMapClickListener
import com.sygic.maps.module.common.mapinteraction.MapSelectionMode
import com.sygic.maps.module.common.mapinteraction.manager.MapInteractionManager
import com.sygic.maps.module.common.poi.manager.PoiDataManager
import com.sygic.maps.module.common.theme.ThemeManager
import com.sygic.maps.uikit.viewmodels.common.data.BasicData
import com.sygic.maps.uikit.viewmodels.common.extensions.toPoiDetailData
import com.sygic.maps.uikit.viewmodels.common.location.LocationManager
import com.sygic.maps.uikit.viewmodels.common.permission.PermissionsManager
import com.sygic.maps.uikit.viewmodels.common.sdk.model.ExtendedMapDataModel
import com.sygic.sdk.map.`object`.MapCircle
import com.sygic.sdk.map.`object`.MapMarker
import com.sygic.sdk.map.`object`.MapRoute
import com.sygic.sdk.map.`object`.data.MarkerData
import com.sygic.sdk.map.`object`.data.ViewObjectData
import com.sygic.sdk.position.GeoCoordinates
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class BrowseMapFragmentViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    internal lateinit var app: Application
    @Mock
    internal lateinit var poiDataManager: PoiDataManager
    @Mock
    internal lateinit var extendedMapDataModel: ExtendedMapDataModel
    @Mock
    internal lateinit var mapInteractionManager: MapInteractionManager
    @Mock
    private lateinit var locationManager: LocationManager
    @Mock
    internal lateinit var permissionsManager: PermissionsManager
    @Mock
    internal lateinit var themeManager: ThemeManager

    private lateinit var mapFragmentInitComponent: MapFragmentInitComponent
    private lateinit var browseMapFragmentViewModel: BrowseMapFragmentViewModel

    @Before
    fun setup() {
        argumentCaptor<(() -> Unit)>().let { callback ->
            whenever(locationManager.requestToEnableGps(callback.capture(), any())).then {
                callback.firstValue.invoke()
            }
        }

        argumentCaptor<Observer<Boolean>>().let { callback ->
            whenever(permissionsManager.checkPermissionGranted(anyString(), callback.capture())).then {
                callback.firstValue.onChanged(true)
            }
        }

        whenever(locationManager.positionOnMapEnabled).thenReturn(true)

        mapFragmentInitComponent = MapFragmentInitComponent()
        mapFragmentInitComponent.mapSelectionMode = MapSelectionMode.FULL
        mapFragmentInitComponent.positionOnMapEnabled = true
        mapFragmentInitComponent.compassEnabled = true
        mapFragmentInitComponent.compassHideIfNorthUp = true
        mapFragmentInitComponent.positionLockFabEnabled = true
        mapFragmentInitComponent.zoomControlsEnabled = true

        browseMapFragmentViewModel = BrowseMapFragmentViewModel(
            app,
            mapFragmentInitComponent,
            extendedMapDataModel,
            poiDataManager,
            mapInteractionManager,
            locationManager,
            permissionsManager,
            themeManager
        )
    }

    @Test
    fun initTest() {
        verify(mapInteractionManager).addOnMapClickListener(browseMapFragmentViewModel)
    }

    @Test
    fun initComponentTest() {
        assertEquals(browseMapFragmentViewModel.mapSelectionMode, MapSelectionMode.FULL)
        assertEquals(browseMapFragmentViewModel.positionOnMapEnabled, true)
        assertEquals(browseMapFragmentViewModel.compassEnabled.value, true)
        assertEquals(browseMapFragmentViewModel.compassHideIfNorthUp.value, true)
        assertEquals(browseMapFragmentViewModel.positionLockFabEnabled.value, true)
        assertEquals(browseMapFragmentViewModel.zoomControlsEnabled.value, true)

        verify(locationManager).requestToEnableGps(any(), any())
        verify(permissionsManager).checkPermissionGranted(anyString(), any())
    }

    @Test
    fun onStartTestPositionOnMapEnabled() {
        whenever(locationManager.positionOnMapEnabled).thenReturn(true)
        browseMapFragmentViewModel.onStart(mock())
        verify(locationManager).setSdkPositionUpdatingEnabled(true)
    }

    @Test
    fun onStartTestPositionOnMapDisabled() {
        whenever(locationManager.positionOnMapEnabled).thenReturn(false)
        browseMapFragmentViewModel.onStart(mock())
        verify(locationManager, never()).setSdkPositionUpdatingEnabled(true)
    }

    @Test
    fun onMapObjectsRequestStartedTest() {
        browseMapFragmentViewModel.onMapObjectsRequestStarted()
        verify(extendedMapDataModel).removeOnClickMapMarker()
    }

    @Test
    fun onMapObjectsReceivedEmptyListTest() {
        browseMapFragmentViewModel.onMapObjectsReceived(listOf())
        verify(poiDataManager, never()).getViewObjectData(any(), any())
    }

    @Test
    fun onMapObjectsReceivedSelectionModeNoneTest() {
        val testMapMarker = MapMarker.at(48.143489, 17.150560).build()

        browseMapFragmentViewModel.mapSelectionMode = MapSelectionMode.NONE
        browseMapFragmentViewModel.onMapObjectsReceived(listOf(testMapMarker, mock()))
        verify(poiDataManager, never()).getViewObjectData(any(), any())
    }

    @Test
    fun onMapObjectsReceivedSelectionModeMarkersOnlyCirclesListTest() {
        val tesViewObject = MapCircle(GeoCoordinates(48.143489, 17.150560), 60.0)

        browseMapFragmentViewModel.mapSelectionMode = MapSelectionMode.MARKERS_ONLY
        browseMapFragmentViewModel.onMapObjectsReceived(listOf(tesViewObject))
        verify(poiDataManager, never()).getViewObjectData(any(), any())
    }

    @Test
    fun onMapObjectsReceivedSelectionModeMarkersOnlyMapMarkerListTest() {
        val testMarkerData = MarkerData(48.143489, 17.150560, BasicData("Test"))
        val testMapMarker = MapMarker.from(testMarkerData).build()

        argumentCaptor<PoiDataManager.Callback>().let { callback ->
            whenever(poiDataManager.getViewObjectData(any(), callback.capture())).then {
                callback.firstValue.onDataLoaded(testMarkerData)
            }
        }

        browseMapFragmentViewModel.mapSelectionMode = MapSelectionMode.MARKERS_ONLY
        browseMapFragmentViewModel.onMapObjectsReceived(listOf(testMapMarker, mock()))
        verify(poiDataManager).getViewObjectData(eq(testMapMarker), any())
        browseMapFragmentViewModel.poiDetailDataObservable.test().assertValue(testMarkerData.toPoiDetailData())
    }

    @Test
    fun onMapObjectsReceivedSelectionModeFullMapRouteTest() {
        val tesViewObject = MapRoute(mock(), MapRoute.RouteType.Primary)
        val testViewObjectData = ViewObjectData(48.143489, 17.150560, BasicData("Test"))

        argumentCaptor<PoiDataManager.Callback>().let { callback ->
            whenever(poiDataManager.getViewObjectData(any(), callback.capture())).then {
                callback.firstValue.onDataLoaded(testViewObjectData)
            }
        }

        browseMapFragmentViewModel.mapSelectionMode = MapSelectionMode.FULL
        browseMapFragmentViewModel.onMapObjectsReceived(listOf(tesViewObject))
        verify(poiDataManager).getViewObjectData(any(), any())
        verify(extendedMapDataModel).addOnClickMapMarker(any())
        browseMapFragmentViewModel.poiDetailDataObservable.test().assertValue(testViewObjectData.toPoiDetailData())
    }

    @Test
    fun onMapObjectsReceivedSelectionModeFullMapMarkerTest() {
        val testMarkerData = MarkerData(48.143489, 17.150560, BasicData("Test"))
        val testMapMarker = MapMarker.from(testMarkerData).build()

        argumentCaptor<PoiDataManager.Callback>().let { callback ->
            whenever(poiDataManager.getViewObjectData(any(), callback.capture())).then {
                callback.firstValue.onDataLoaded(testMarkerData)
            }
        }

        browseMapFragmentViewModel.mapSelectionMode = MapSelectionMode.FULL
        browseMapFragmentViewModel.onMapObjectsReceived(listOf(testMapMarker))
        verify(poiDataManager).getViewObjectData(eq(testMapMarker), any())
        browseMapFragmentViewModel.poiDetailDataObservable.test().assertValue(testMarkerData.toPoiDetailData())
    }

    @Test
    fun customOnMapClickListenerConsumedTest() {
        val testMarkerData = MarkerData(48.143489, 17.150560, BasicData("Test"))
        val testMapMarker = MapMarker.from(testMarkerData).build()
        val onMapClickListener = mock<OnMapClickListener>()

        whenever(onMapClickListener.onMapClick(any())).thenReturn(true)
        argumentCaptor<PoiDataManager.Callback>().let { callback ->
            whenever(poiDataManager.getViewObjectData(any(), callback.capture())).then {
                callback.firstValue.onDataLoaded(testMarkerData)
            }
        }

        browseMapFragmentViewModel.onMapClickListener = onMapClickListener
        browseMapFragmentViewModel.mapSelectionMode = MapSelectionMode.FULL
        browseMapFragmentViewModel.onMapObjectsReceived(listOf(testMapMarker))
        verify(extendedMapDataModel, never()).addOnClickMapMarker(any())
        verify(poiDataManager).getViewObjectData(eq(testMapMarker), any())
        verify(onMapClickListener).onMapClick(eq(testMarkerData))
        browseMapFragmentViewModel.poiDetailDataObservable.test().assertNoValue()
    }

    @Test
    fun customOnMapClickListenerNotConsumedTest() {
        val testMarkerData = MarkerData(48.143489, 17.150560, BasicData("Test"))
        val testMapMarker = MapMarker.from(testMarkerData).build()
        val onMapClickListener = mock<OnMapClickListener>()

        argumentCaptor<PoiDataManager.Callback>().let { callback ->
            whenever(poiDataManager.getViewObjectData(any(), callback.capture())).then {
                callback.firstValue.onDataLoaded(testMarkerData)
            }
        }

        browseMapFragmentViewModel.onMapClickListener = onMapClickListener
        browseMapFragmentViewModel.mapSelectionMode = MapSelectionMode.FULL
        browseMapFragmentViewModel.onMapObjectsReceived(listOf(testMapMarker))
        verify(extendedMapDataModel, never()).addOnClickMapMarker(any())
        verify(poiDataManager).getViewObjectData(eq(testMapMarker), any())
        verify(onMapClickListener).onMapClick(eq(testMarkerData))
        browseMapFragmentViewModel.poiDetailDataObservable.test().assertHasValue()
    }

    @Test
    fun poiDetailsViewTest() {
        val testMarkerData = MarkerData(48.143489, 17.150560, BasicData("Test"))
        val testMapMarker = MapMarker.from(testMarkerData).build()
        val detailsViewFactory = mock<DetailsViewFactory>()

        argumentCaptor<PoiDataManager.Callback>().let { callback ->
            whenever(poiDataManager.getViewObjectData(any(), callback.capture())).then {
                callback.firstValue.onDataLoaded(testMarkerData)
            }
        }

        browseMapFragmentViewModel.detailsViewFactory = detailsViewFactory
        browseMapFragmentViewModel.mapSelectionMode = MapSelectionMode.FULL
        browseMapFragmentViewModel.onMapObjectsReceived(listOf(testMapMarker))
        verify(extendedMapDataModel, never()).addOnClickMapMarker(any())
        verify(poiDataManager).getViewObjectData(eq(testMapMarker), any())
        verify(extendedMapDataModel).addMapObject(isA())
        browseMapFragmentViewModel.poiDetailDataObservable.test().assertNoValue()
    }

    @Test
    fun onStopTest() {
        browseMapFragmentViewModel.onStop(mock())
        verify(locationManager).setSdkPositionUpdatingEnabled(false)
    }

    @Test
    fun onDestroyTest() {
        browseMapFragmentViewModel.onDestroy(mock())
        assertEquals(browseMapFragmentViewModel.onMapClickListener, null)
        assertEquals(browseMapFragmentViewModel.detailsViewFactory, null)
    }
}