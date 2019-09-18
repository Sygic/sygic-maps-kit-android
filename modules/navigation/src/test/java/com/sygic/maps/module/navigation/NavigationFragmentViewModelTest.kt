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

package com.sygic.maps.module.navigation

import android.app.Application
import android.os.Bundle
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.nhaarman.mockitokotlin2.*
import com.sygic.maps.module.common.theme.ThemeManager
import com.sygic.maps.module.navigation.types.SignpostType
import com.sygic.maps.module.navigation.viewmodel.NavigationFragmentViewModel
import com.sygic.maps.uikit.viewmodels.common.location.LocationManager
import com.sygic.maps.uikit.viewmodels.common.navigation.preview.RouteDemonstrationManager
import com.sygic.maps.uikit.viewmodels.common.permission.PermissionsManager
import com.sygic.maps.uikit.viewmodels.common.regional.RegionalManager
import com.sygic.maps.uikit.viewmodels.common.sdk.model.ExtendedCameraModel
import com.sygic.maps.uikit.viewmodels.common.sdk.model.ExtendedMapDataModel
import com.sygic.maps.uikit.viewmodels.navigation.infobar.button.OnInfobarButtonClickListenerWrapper
import com.sygic.maps.uikit.views.common.units.DistanceUnit
import com.sygic.maps.uikit.views.navigation.actionmenu.listener.ActionMenuItemsProviderWrapper
import com.sygic.sdk.navigation.NavigationManager
import com.sygic.sdk.route.RouteInfo
import junit.framework.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class NavigationFragmentViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var app: Application
    @Mock
    private lateinit var extendedMapDataModel: ExtendedMapDataModel
    @Mock
    private lateinit var navigationManager: NavigationManager
    @Mock
    private lateinit var extendedCameraModel: ExtendedCameraModel
    @Mock
    private lateinit var locationManager: LocationManager
    @Mock
    private lateinit var permissionsManager: PermissionsManager
    @Mock
    private lateinit var regionalManager: RegionalManager
    @Mock
    private lateinit var routeDemonstrationManager: RouteDemonstrationManager
    @Mock
    private lateinit var themeManager: ThemeManager

    private lateinit var navigationFragmentViewModel: NavigationFragmentViewModel

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

        whenever(regionalManager.distanceUnit).thenReturn(MutableLiveData(DistanceUnit.KILOMETERS))

        val arguments = mock<Bundle>()
        whenever(arguments.getParcelable<RouteInfo>(eq(KEY_ROUTE_INFO))).thenReturn(mock()) //todo
        whenever(arguments.getBoolean(eq(KEY_PREVIEW_MODE), any())).thenReturn(false)
        whenever(arguments.getBoolean(eq(KEY_INFOBAR_ENABLED), any())).thenReturn(true)
        whenever(arguments.getBoolean(eq(KEY_PREVIEW_CONTROLS_ENABLED), any())).thenReturn(true)
        whenever(arguments.getBoolean(eq(KEY_CURRENT_SPEED_ENABLED), any())).thenReturn(true)
        whenever(arguments.getBoolean(eq(KEY_SPEED_LIMIT_ENABLED), any())).thenReturn(true)
        whenever(arguments.getBoolean(eq(KEY_SIGNPOST_ENABLED), any())).thenReturn(true)
        whenever(arguments.getBoolean(eq(KEY_LANES_VIEW_ENABLED), any())).thenReturn(true)
        whenever(arguments.getParcelable<SignpostType>(eq(KEY_SIGNPOST_TYPE))).thenReturn(SignpostType.FULL)
        whenever(arguments.getParcelable<DistanceUnit>(eq(KEY_DISTANCE_UNITS))).thenReturn(DistanceUnit.KILOMETERS)

        navigationFragmentViewModel = NavigationFragmentViewModel(
            app,
            arguments,
            themeManager,
            extendedCameraModel,
            extendedMapDataModel,
            regionalManager,
            locationManager,
            permissionsManager,
            navigationManager,
            routeDemonstrationManager
        )
    }

    @Test
    fun initComponentTest() {
        assertEquals(R.layout.layout_signpost_full_view_stub, navigationFragmentViewModel.signpostLayout)
        assertEquals(false, navigationFragmentViewModel.previewMode.value)
        assertEquals(true, navigationFragmentViewModel.signpostEnabled.value)
        assertEquals(true, navigationFragmentViewModel.infobarEnabled.value)
        assertEquals(true, navigationFragmentViewModel.previewControlsEnabled.value)
        assertEquals(true, navigationFragmentViewModel.currentSpeedEnabled.value)
        assertEquals(true, navigationFragmentViewModel.speedLimitEnabled.value)
        assertEquals(true, navigationFragmentViewModel.lanesViewEnabled.value)
        assertEquals(DistanceUnit.KILOMETERS, navigationFragmentViewModel.distanceUnit)

        verify(locationManager).requestToEnableGps(any(), any())
        verify(permissionsManager).checkPermissionGranted(anyString(), any())
    }

    @Test
    fun onCreateTest() {
        val inInfobarButtonClickListenerProviderComponent = mock<LiveData<OnInfobarButtonClickListenerWrapper.ProviderComponent>>()
        val onInfobarButtonClickListenerWrapperLifecycleOwnerMock = mock<LifecycleOwner>(extraInterfaces = arrayOf(OnInfobarButtonClickListenerWrapper::class))
        whenever((onInfobarButtonClickListenerWrapperLifecycleOwnerMock as OnInfobarButtonClickListenerWrapper).infobarButtonClickListenerProvider).thenReturn(
            inInfobarButtonClickListenerProviderComponent
        )
        navigationFragmentViewModel.onCreate(onInfobarButtonClickListenerWrapperLifecycleOwnerMock)
        verify(inInfobarButtonClickListenerProviderComponent).observe(eq(onInfobarButtonClickListenerWrapperLifecycleOwnerMock), any())

        val actionMenuItemsProviderComponent = mock<LiveData<ActionMenuItemsProviderWrapper.ProviderComponent>>()
        val actionMenuItemsProviderWrapperLifecycleOwnerMock = mock<LifecycleOwner>(extraInterfaces = arrayOf(ActionMenuItemsProviderWrapper::class))
        whenever((actionMenuItemsProviderWrapperLifecycleOwnerMock as ActionMenuItemsProviderWrapper).actionMenuItemsProvider).thenReturn(
            actionMenuItemsProviderComponent
        )
        navigationFragmentViewModel.onCreate(actionMenuItemsProviderWrapperLifecycleOwnerMock)
        verify(actionMenuItemsProviderComponent).observe(eq(actionMenuItemsProviderWrapperLifecycleOwnerMock), any())
    }

    /*
    @Test
    fun onStartTestPositionOnMapEnabled() {
        whenever(locationManager.positionOnMapEnabled).thenReturn(true)
        navigationFragmentViewModel.onStart(mock())
        verify(locationManager).setSdkPositionUpdatingEnabled(true)
    }

    @Test
    fun onStartTestPositionOnMapDisabled() {
        whenever(locationManager.positionOnMapEnabled).thenReturn(false)
        navigationFragmentViewModel.onStart(mock())
        verify(locationManager, never()).setSdkPositionUpdatingEnabled(true)
    }

    @Test
    fun onMapObjectsRequestStartedTest() {
        val tesViewObject = MapRoute.from(mock()).build()

        navigationFragmentViewModel.mapSelectionMode = MapSelectionMode.FULL
        navigationFragmentViewModel.onMapObjectsReceived(listOf(tesViewObject))

        navigationFragmentViewModel.onMapObjectsRequestStarted()
        verify(extendedMapDataModel).removeMapObject(anyOrNull<MapMarker>())
    }

    @Test
    fun onMapObjectsReceivedEmptyListTest() {
        navigationFragmentViewModel.onMapObjectsReceived(listOf())
        verify(poiDataManager, never()).getViewObjectData(any(), any())
    }

    @Test
    fun onMapObjectsReceivedSelectionModeNoneTest() {
        val testMapMarker = MapMarker.at(48.143489, 17.150560).build()

        navigationFragmentViewModel.mapSelectionMode = MapSelectionMode.NONE
        navigationFragmentViewModel.onMapObjectsReceived(listOf(testMapMarker, mock()))
        verify(poiDataManager, never()).getViewObjectData(any(), any())
    }

    @Test
    fun onMapObjectsReceivedSelectionModeMarkersOnlyCirclesListTest() {
        val tesViewObject = MapCircle.at(48.143489, 17.150560).setRadius(60.0).build()

        navigationFragmentViewModel.mapSelectionMode = MapSelectionMode.MARKERS_ONLY
        navigationFragmentViewModel.onMapObjectsReceived(listOf(tesViewObject))
        verify(poiDataManager, never()).getViewObjectData(any(), any())
    }

    @Test
    fun onMapObjectsReceivedSelectionModeMarkersOnlyMapMarkerListTest() {
        val testMapMarker = MapMarker.at(48.143489, 17.150560).withPayload(BasicData("Test")).build()

        val callback = argumentCaptor<PoiDataManager.Callback>()
        whenever(poiDataManager.getViewObjectData(any(), callback.capture())).then {
            callback.firstValue.onDataLoaded(testMapMarker.data)
        }

        navigationFragmentViewModel.mapSelectionMode = MapSelectionMode.MARKERS_ONLY
        navigationFragmentViewModel.onMapObjectsReceived(listOf(testMapMarker, mock()))
        verify(poiDataManager).getViewObjectData(eq(testMapMarker), any())
        navigationFragmentViewModel.poiDetailDataObservable.test().assertValue(testMapMarker.data.toPoiDetailData())
    }

    @Test
    fun onMapObjectsReceivedSelectionModeFullMapRouteTest() {
        val tesViewObject = MapRoute.from(mock()).build()

        argumentCaptor<PoiDataManager.Callback>().let { callback ->
            whenever(poiDataManager.getViewObjectData(any(), callback.capture())).then {
                callback.firstValue.onDataLoaded(tesViewObject.data)
            }
        }

        navigationFragmentViewModel.mapSelectionMode = MapSelectionMode.FULL
        navigationFragmentViewModel.onMapObjectsReceived(listOf(tesViewObject))
        verify(poiDataManager).getViewObjectData(any(), any())
        verify(extendedMapDataModel).addMapObject(any<MapMarker>())
        navigationFragmentViewModel.poiDetailDataObservable.test().assertValue(tesViewObject.data.toPoiDetailData())
    }

    @Test
    fun onMapObjectsReceivedSelectionModeFullMapMarkerTest() {
        val testMapMarker = MapMarker.at(48.143489, 17.150560).withPayload(BasicData("Test")).build()

        argumentCaptor<PoiDataManager.Callback>().let { callback ->
            whenever(poiDataManager.getViewObjectData(any(), callback.capture())).then {
                callback.firstValue.onDataLoaded(testMapMarker.data)
            }
        }

        navigationFragmentViewModel.mapSelectionMode = MapSelectionMode.FULL
        navigationFragmentViewModel.onMapObjectsReceived(listOf(testMapMarker))
        verify(poiDataManager).getViewObjectData(eq(testMapMarker), any())
        navigationFragmentViewModel.poiDetailDataObservable.test().assertValue(testMapMarker.data.toPoiDetailData())
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

        navigationFragmentViewModel.onMapClickListener = onMapClickListener
        navigationFragmentViewModel.mapSelectionMode = MapSelectionMode.FULL
        navigationFragmentViewModel.onMapObjectsReceived(listOf(testViewObject))
        verify(extendedMapDataModel).addMapObject(any<MapMarker>())
        verify(poiDataManager).getViewObjectData(any(), any())
        verify(onMapClickListener).onMapClick(eq(SelectionType.POI), any(), any())
        verify(onMapClickListener).onMapDataReceived(eq(testViewObject.data))
        navigationFragmentViewModel.poiDetailDataObservable.test().assertHasValue()
    }

    @Test
    fun customOnMapClickListenerOnMapClickFalseTest() {
        val testPosition = GeoCoordinates(48.143489, 17.150560)
        val testViewObject = ProxyPoi.create(testPosition.latitude, testPosition.latitude, byteArrayOf()).withPayload(BasicData("Test")).build()
        val onMapClickListener = mock<OnMapClickListener>()

        whenever(onMapClickListener.onMapClick(any(), any(), any())).thenReturn(false)

        navigationFragmentViewModel.onMapClickListener = onMapClickListener
        navigationFragmentViewModel.mapSelectionMode = MapSelectionMode.FULL
        navigationFragmentViewModel.onMapObjectsReceived(listOf(testViewObject))
        verify(extendedMapDataModel, never()).addMapObject(any<MapMarker>())
        verify(poiDataManager, never()).getViewObjectData(any(), any())
        verify(onMapClickListener).onMapClick(eq(SelectionType.POI), any(), any())
        verify(onMapClickListener, never()).onMapDataReceived(any())
        navigationFragmentViewModel.poiDetailDataObservable.test().assertNoValue()
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

        navigationFragmentViewModel.onMapClickListener = onMapClickListener
        navigationFragmentViewModel.mapSelectionMode = MapSelectionMode.FULL
        navigationFragmentViewModel.onMapObjectsReceived(listOf(testViewObject))
        verify(extendedMapDataModel).addMapObject(any<MapMarker>())
        verify(poiDataManager).getViewObjectData(any(), any())
        verify(onMapClickListener).onMapClick(eq(SelectionType.POI), any(), any())
        verify(onMapClickListener).onMapDataReceived(eq(testViewObject.data))
        navigationFragmentViewModel.poiDetailDataObservable.test().assertNoValue()
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

        navigationFragmentViewModel.onMapClickListener = onMapClickListener
        navigationFragmentViewModel.mapSelectionMode = MapSelectionMode.FULL
        navigationFragmentViewModel.onMapObjectsReceived(listOf(testViewObject))
        verify(extendedMapDataModel, never()).addMapObject(any<MapMarker>())
        verify(poiDataManager).getViewObjectData(any(), any())
        verify(onMapClickListener).onMapClick(eq(SelectionType.POI), any(), any())
        verify(onMapClickListener).onMapDataReceived(eq(testViewObject.data))
        navigationFragmentViewModel.poiDetailDataObservable.test().assertNoValue()
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

        navigationFragmentViewModel.detailsViewFactory = detailsViewFactory
        navigationFragmentViewModel.mapSelectionMode = MapSelectionMode.FULL
        navigationFragmentViewModel.onMapObjectsReceived(listOf(testMapMarker))
        verify(extendedMapDataModel, never()).addMapObject(any<MapMarker>())
        verify(poiDataManager).getViewObjectData(eq(testMapMarker), any())
        verify(extendedMapDataModel).addMapObject(any())
        navigationFragmentViewModel.poiDetailDataObservable.test().assertNoValue()
    }

    @Test
    fun onStopTest() {
        navigationFragmentViewModel.onStop(mock())
        verify(locationManager).setSdkPositionUpdatingEnabled(false)
    }

    @Test
    fun onDestroyTest() {
        navigationFragmentViewModel.onDestroy(mock())
        assertEquals(navigationFragmentViewModel.onMapClickListener, null)
        assertEquals(navigationFragmentViewModel.detailsViewFactory, null)
    }*/
}