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

package com.sygic.module.browsemap

import android.app.Application
import android.os.Bundle
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.jraska.livedata.test
import com.nhaarman.mockitokotlin2.*
import com.sygic.maps.module.browsemap.*
import com.sygic.maps.module.browsemap.viewmodel.BrowseMapFragmentViewModel
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
import com.sygic.maps.uikit.viewmodels.common.sdk.viewobject.SelectionType
import com.sygic.sdk.map.`object`.MapCircle
import com.sygic.sdk.map.`object`.MapMarker
import com.sygic.sdk.map.`object`.MapRoute
import com.sygic.sdk.map.`object`.ProxyPoi
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
    private lateinit var app: Application
    @Mock
    private lateinit var poiDataManager: PoiDataManager
    @Mock
    private lateinit var extendedMapDataModel: ExtendedMapDataModel
    @Mock
    private lateinit var mapInteractionManager: MapInteractionManager
    @Mock
    private lateinit var locationManager: LocationManager
    @Mock
    private lateinit var permissionsManager: PermissionsManager
    @Mock
    private lateinit var themeManager: ThemeManager

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

        val arguments = mock<Bundle>()
        whenever(arguments.getInt(eq(KEY_MAP_SELECTION_MODE), any())).thenReturn(MapSelectionMode.FULL)
        whenever(arguments.getBoolean(eq(KEY_POSITION_ON_MAP), any())).thenReturn(true)
        whenever(arguments.getBoolean(eq(KEY_COMPASS_ENABLED), any())).thenReturn(true)
        whenever(arguments.getBoolean(eq(KEY_COMPASS_HIDE_IF_NORTH), any())).thenReturn(true)
        whenever(arguments.getBoolean(eq(KEY_POSITION_LOCK_FAB), any())).thenReturn(true)
        whenever(arguments.getBoolean(eq(KEY_ZOOM_CONTROLS), any())).thenReturn(true)

        browseMapFragmentViewModel = BrowseMapFragmentViewModel(
            app,
            arguments,
            themeManager,
            extendedMapDataModel,
            poiDataManager,
            mapInteractionManager,
            locationManager,
            permissionsManager
        )
    }

    @Test
    fun initTest() {
        verify(mapInteractionManager).addOnMapClickListener(browseMapFragmentViewModel)
    }

    @Test
    fun initComponentTest() {
        assertEquals(MapSelectionMode.FULL, browseMapFragmentViewModel.mapSelectionMode)
        assertEquals(true, browseMapFragmentViewModel.positionOnMapEnabled)
        assertEquals(true, browseMapFragmentViewModel.compassEnabled.value)
        assertEquals(true, browseMapFragmentViewModel.compassHideIfNorthUp.value)
        assertEquals(true, browseMapFragmentViewModel.positionLockFabEnabled.value)
        assertEquals(true, browseMapFragmentViewModel.zoomControlsEnabled.value)

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
        val tesViewObject = MapRoute.from(mock()).build()

        browseMapFragmentViewModel.mapSelectionMode = MapSelectionMode.FULL
        browseMapFragmentViewModel.onMapObjectsReceived(listOf(tesViewObject))

        browseMapFragmentViewModel.onMapObjectsRequestStarted()
        verify(extendedMapDataModel).removeMapObject(anyOrNull<MapMarker>())
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
        val tesViewObject = MapCircle.at(48.143489, 17.150560).setRadius(60.0).build()

        browseMapFragmentViewModel.mapSelectionMode = MapSelectionMode.MARKERS_ONLY
        browseMapFragmentViewModel.onMapObjectsReceived(listOf(tesViewObject))
        verify(poiDataManager, never()).getViewObjectData(any(), any())
    }

    @Test
    fun onMapObjectsReceivedSelectionModeMarkersOnlyMapMarkerListTest() {
        val testMapMarker = MapMarker.at(48.143489, 17.150560).withPayload(BasicData("Test")).build()

        val callback = argumentCaptor<PoiDataManager.Callback>()
        whenever(poiDataManager.getViewObjectData(any(), callback.capture())).then {
            callback.firstValue.onDataLoaded(testMapMarker.data)
        }

        browseMapFragmentViewModel.mapSelectionMode = MapSelectionMode.MARKERS_ONLY
        browseMapFragmentViewModel.onMapObjectsReceived(listOf(testMapMarker, mock()))
        verify(poiDataManager).getViewObjectData(eq(testMapMarker), any())
        assertEquals(testMapMarker.data.toPoiDetailData(), browseMapFragmentViewModel.poiDetailComponentObservable.value!!.data)
    }

    @Test
    fun onMapObjectsReceivedSelectionModeFullMapRouteTest() {
        val tesViewObject = MapRoute.from(mock()).build()

        argumentCaptor<PoiDataManager.Callback>().let { callback ->
            whenever(poiDataManager.getViewObjectData(any(), callback.capture())).then {
                callback.firstValue.onDataLoaded(tesViewObject.data)
            }
        }

        browseMapFragmentViewModel.mapSelectionMode = MapSelectionMode.FULL
        browseMapFragmentViewModel.onMapObjectsReceived(listOf(tesViewObject))
        verify(poiDataManager).getViewObjectData(any(), any())
        verify(extendedMapDataModel).addMapObject(any<MapMarker>())
        assertEquals(tesViewObject.data.toPoiDetailData(), browseMapFragmentViewModel.poiDetailComponentObservable.value!!.data)
    }

    @Test
    fun onMapObjectsReceivedSelectionModeFullMapMarkerTest() {
        val testMapMarker = MapMarker.at(48.143489, 17.150560).withPayload(BasicData("Test")).build()

        argumentCaptor<PoiDataManager.Callback>().let { callback ->
            whenever(poiDataManager.getViewObjectData(any(), callback.capture())).then {
                callback.firstValue.onDataLoaded(testMapMarker.data)
            }
        }

        browseMapFragmentViewModel.mapSelectionMode = MapSelectionMode.FULL
        browseMapFragmentViewModel.onMapObjectsReceived(listOf(testMapMarker))
        verify(poiDataManager).getViewObjectData(eq(testMapMarker), any())
        assertEquals(testMapMarker.data.toPoiDetailData(), browseMapFragmentViewModel.poiDetailComponentObservable.value!!.data)
    }

    @Test
    fun customOnMapClickListenerOnMapClickTrueTest() {
        val testViewObject = ProxyPoi.create(48.143489, 17.150560, byteArrayOf()).withPayload(BasicData("Test")).build()
        val testMapMarker = mock<MapMarker>()
        val onMapClickListener = mock<OnMapClickListener>()

        whenever(testMapMarker.data).thenReturn(mock())
        whenever(onMapClickListener.onMapClick(any(), any(), any())).thenReturn(true)
        whenever(onMapClickListener.showDetailsView()).thenReturn(true)
        whenever(onMapClickListener.getClickMapMarker(any(), any())).thenReturn(testMapMarker)
        argumentCaptor<PoiDataManager.Callback>().let { callback ->
            whenever(poiDataManager.getViewObjectData(any(), callback.capture())).then {
                callback.firstValue.onDataLoaded(testViewObject.data)
            }
        }

        browseMapFragmentViewModel.onMapClickListener = onMapClickListener
        browseMapFragmentViewModel.mapSelectionMode = MapSelectionMode.FULL
        browseMapFragmentViewModel.onMapObjectsReceived(listOf(testViewObject))
        verify(extendedMapDataModel).addMapObject(any<MapMarker>())
        verify(poiDataManager).getViewObjectData(any(), any())
        verify(onMapClickListener).onMapClick(eq(SelectionType.POI), any(), any())
        verify(onMapClickListener).onMapDataReceived(eq(testViewObject.data))
        browseMapFragmentViewModel.poiDetailComponentObservable.test().assertHasValue()
    }

    @Test
    fun customOnMapClickListenerOnMapClickFalseTest() {
        val testPosition = GeoCoordinates(48.143489, 17.150560)
        val testViewObject = ProxyPoi.create(testPosition.latitude, testPosition.latitude, byteArrayOf()).withPayload(BasicData("Test")).build()
        val onMapClickListener = mock<OnMapClickListener>()

        whenever(onMapClickListener.onMapClick(any(), any(), any())).thenReturn(false)

        browseMapFragmentViewModel.onMapClickListener = onMapClickListener
        browseMapFragmentViewModel.mapSelectionMode = MapSelectionMode.FULL
        browseMapFragmentViewModel.onMapObjectsReceived(listOf(testViewObject))
        verify(extendedMapDataModel, never()).addMapObject(any<MapMarker>())
        verify(poiDataManager, never()).getViewObjectData(any(), any())
        verify(onMapClickListener).onMapClick(eq(SelectionType.POI), any(), any())
        verify(onMapClickListener, never()).onMapDataReceived(any())
        browseMapFragmentViewModel.poiDetailComponentObservable.test().assertNoValue()
    }

    @Test
    fun customOnMapClickListenerShowDetailsViewFalseTest() {
        val testViewObject = ProxyPoi.create(48.143489, 17.150560, byteArrayOf()).withPayload(BasicData("Test")).build()
        val testMapMarker = mock<MapMarker>()
        val onMapClickListener = mock<OnMapClickListener>()

        whenever(testMapMarker.data).thenReturn(mock())
        whenever(onMapClickListener.onMapClick(any(), any(), any())).thenReturn(true)
        whenever(onMapClickListener.showDetailsView()).thenReturn(false)
        whenever(onMapClickListener.getClickMapMarker(any(), any())).thenReturn(testMapMarker)
        argumentCaptor<PoiDataManager.Callback>().let { callback ->
            whenever(poiDataManager.getViewObjectData(any(), callback.capture())).then {
                callback.firstValue.onDataLoaded(testViewObject.data)
            }
        }

        browseMapFragmentViewModel.onMapClickListener = onMapClickListener
        browseMapFragmentViewModel.mapSelectionMode = MapSelectionMode.FULL
        browseMapFragmentViewModel.onMapObjectsReceived(listOf(testViewObject))
        verify(extendedMapDataModel).addMapObject(any<MapMarker>())
        verify(poiDataManager).getViewObjectData(any(), any())
        verify(onMapClickListener).onMapClick(eq(SelectionType.POI), any(), any())
        verify(onMapClickListener).onMapDataReceived(eq(testViewObject.data))
        browseMapFragmentViewModel.poiDetailComponentObservable.test().assertNoValue()
    }

    @Test
    fun customOnMapClickListenerGetClickMapMarkerNullTest() {
        val testPosition = GeoCoordinates(48.143489, 17.150560)
        val testViewObject = ProxyPoi.create(testPosition.latitude, testPosition.latitude, byteArrayOf()).withPayload(BasicData("Test")).build()
        val onMapClickListener = mock<OnMapClickListener>()

        whenever(onMapClickListener.onMapClick(any(), any(), any())).thenReturn(true)
        whenever(onMapClickListener.getClickMapMarker(any(), any())).thenReturn(null)
        argumentCaptor<PoiDataManager.Callback>().let { callback ->
            whenever(poiDataManager.getViewObjectData(any(), callback.capture())).then {
                callback.firstValue.onDataLoaded(testViewObject.data)
            }
        }

        browseMapFragmentViewModel.onMapClickListener = onMapClickListener
        browseMapFragmentViewModel.mapSelectionMode = MapSelectionMode.FULL
        browseMapFragmentViewModel.onMapObjectsReceived(listOf(testViewObject))
        verify(extendedMapDataModel, never()).addMapObject(any<MapMarker>())
        verify(poiDataManager).getViewObjectData(any(), any())
        verify(onMapClickListener).onMapClick(eq(SelectionType.POI), any(), any())
        verify(onMapClickListener).onMapDataReceived(eq(testViewObject.data))
        browseMapFragmentViewModel.poiDetailComponentObservable.test().assertNoValue()
    }

    @Test
    fun poiDetailsViewTest() {
        val testMapMarker = MapMarker.at(48.143489, 17.150560).withPayload(BasicData("Test")).build()
        val detailsViewFactory = mock<DetailsViewFactory>()

        argumentCaptor<PoiDataManager.Callback>().let { callback ->
            whenever(poiDataManager.getViewObjectData(any(), callback.capture())).then {
                callback.firstValue.onDataLoaded(testMapMarker.data)
            }
        }

        browseMapFragmentViewModel.detailsViewFactory = detailsViewFactory
        browseMapFragmentViewModel.mapSelectionMode = MapSelectionMode.FULL
        browseMapFragmentViewModel.onMapObjectsReceived(listOf(testMapMarker))
        verify(extendedMapDataModel, never()).addMapObject(any<MapMarker>())
        verify(poiDataManager).getViewObjectData(eq(testMapMarker), any())
        verify(extendedMapDataModel).addMapObject(any())
        browseMapFragmentViewModel.poiDetailComponentObservable.test().assertNoValue()
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