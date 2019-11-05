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

package com.sygic.maps.module.browsemap

import android.app.Application
import android.os.Bundle
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.jraska.livedata.test
import com.nhaarman.mockitokotlin2.*
import com.sygic.maps.module.browsemap.utils.BrowseMapTestLifecycleOwner
import com.sygic.maps.module.browsemap.viewmodel.BrowseMapFragmentViewModel
import com.sygic.maps.module.common.detail.DetailsViewFactory
import com.sygic.maps.module.common.listener.OnMapClickListener
import com.sygic.maps.module.common.mapinteraction.MapSelectionMode
import com.sygic.maps.module.common.mapinteraction.manager.MapInteractionManager
import com.sygic.maps.module.common.theme.ThemeManager
import com.sygic.maps.uikit.viewmodels.common.data.BasicData
import com.sygic.maps.uikit.viewmodels.common.extensions.toPlaceDetailData
import com.sygic.maps.uikit.viewmodels.common.location.LocationManager
import com.sygic.maps.uikit.viewmodels.common.permission.PermissionsManager
import com.sygic.maps.uikit.viewmodels.common.place.PlacesManagerClient
import com.sygic.maps.uikit.viewmodels.common.position.PositionManagerClient
import com.sygic.maps.uikit.viewmodels.common.regional.RegionalManager
import com.sygic.maps.uikit.viewmodels.common.sdk.model.ExtendedMapDataModel
import com.sygic.maps.uikit.viewmodels.common.sdk.viewobject.SelectionType
import com.sygic.sdk.map.`object`.MapCircle
import com.sygic.sdk.map.`object`.MapMarker
import com.sygic.sdk.map.`object`.MapRoute
import com.sygic.sdk.map.`object`.ProxyPlace
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
    private lateinit var regionalManager: RegionalManager
    @Mock
    private lateinit var placesManagerClient: PlacesManagerClient
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
    @Mock
    private lateinit var positionManagerClient: PositionManagerClient

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

        val positionOnMapEnabledMock = mock<MutableLiveData<Boolean>>()
        whenever(positionOnMapEnabledMock.value).thenReturn(true)
        whenever(locationManager.positionOnMapEnabled).thenReturn(positionOnMapEnabledMock)
        whenever(regionalManager.distanceUnit).thenReturn(mock())
        whenever(positionManagerClient.sdkPositionUpdatingEnabled).thenReturn(mock())
        if (BuildConfig.DEBUG) whenever(positionManagerClient.remotePositioningServiceEnabled).thenReturn(mock())

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
            regionalManager,
            extendedMapDataModel,
            placesManagerClient,
            mapInteractionManager,
            locationManager,
            permissionsManager,
            positionManagerClient
        )
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
    fun onCreateTest() {
        val browseMapTestLifecycleOwner = BrowseMapTestLifecycleOwner()

        browseMapFragmentViewModel.onCreate(browseMapTestLifecycleOwner)

        assertEquals(true, browseMapTestLifecycleOwner.mapClickListenerProvider.hasObservers())
        assertEquals(true, browseMapTestLifecycleOwner.moduleConnectionProvidersMap.hasObservers())

        browseMapFragmentViewModel.placeDetailListenerObservable.test().assertValue(browseMapFragmentViewModel)

        verify(mapInteractionManager).addOnMapClickListener(browseMapFragmentViewModel)
    }

    @Test
    fun onStartTestPositionOnMapEnabled() {
        whenever(locationManager.positionOnMapEnabled.value).thenReturn(true)
        browseMapFragmentViewModel.onStart(mock())
        verify(positionManagerClient.sdkPositionUpdatingEnabled).value = true
    }

    @Test
    fun onStartTestPositionOnMapDisabled() {
        whenever(locationManager.positionOnMapEnabled.value).thenReturn(false)
        browseMapFragmentViewModel.onStart(mock())
        verify(positionManagerClient.sdkPositionUpdatingEnabled, never()).value = true
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
        verify(placesManagerClient, never()).getViewObjectData(any(), any())
    }

    @Test
    fun onMapObjectsReceivedSelectionModeNoneTest() {
        val testMapMarker = MapMarker.at(48.143489, 17.150560).build()

        browseMapFragmentViewModel.mapSelectionMode = MapSelectionMode.NONE
        browseMapFragmentViewModel.onMapObjectsReceived(listOf(testMapMarker, mock()))
        verify(placesManagerClient, never()).getViewObjectData(any(), any())
    }

    @Test
    fun onMapObjectsReceivedSelectionModeMarkersOnlyCirclesListTest() {
        val tesViewObject = MapCircle.at(48.143489, 17.150560).setRadius(60.0).build()

        browseMapFragmentViewModel.mapSelectionMode = MapSelectionMode.MARKERS_ONLY
        browseMapFragmentViewModel.onMapObjectsReceived(listOf(tesViewObject))
        verify(placesManagerClient, never()).getViewObjectData(any(), any())
    }

    @Test
    fun onMapObjectsReceivedSelectionModeMarkersOnlyMapMarkerListTest() {
        val testMapMarker = MapMarker.at(48.143489, 17.150560).withPayload(BasicData("Test")).build()

        val callback = argumentCaptor<PlacesManagerClient.Callback>()
        whenever(placesManagerClient.getViewObjectData(any(), callback.capture())).then {
            callback.firstValue.onDataLoaded(testMapMarker.data)
        }

        browseMapFragmentViewModel.mapSelectionMode = MapSelectionMode.MARKERS_ONLY
        browseMapFragmentViewModel.onMapObjectsReceived(listOf(testMapMarker, mock()))
        verify(placesManagerClient).getViewObjectData(eq(testMapMarker), any())
        assertEquals(testMapMarker.data.toPlaceDetailData(), browseMapFragmentViewModel.placeDetailComponentObservable.value!!.data)
    }

    @Test
    fun onMapObjectsReceivedSelectionModeFullMapRouteTest() {
        val tesViewObject = MapRoute.from(mock()).build()

        argumentCaptor<PlacesManagerClient.Callback>().let { callback ->
            whenever(placesManagerClient.getViewObjectData(any(), callback.capture())).then {
                callback.firstValue.onDataLoaded(tesViewObject.data)
            }
        }

        browseMapFragmentViewModel.mapSelectionMode = MapSelectionMode.FULL
        browseMapFragmentViewModel.onMapObjectsReceived(listOf(tesViewObject))
        verify(placesManagerClient).getViewObjectData(any(), any())
        verify(extendedMapDataModel).addMapObject(any<MapMarker>())
        assertEquals(tesViewObject.data.toPlaceDetailData(), browseMapFragmentViewModel.placeDetailComponentObservable.value!!.data)
    }

    @Test
    fun onMapObjectsReceivedSelectionModeFullMapMarkerTest() {
        val testMapMarker = MapMarker.at(48.143489, 17.150560).withPayload(BasicData("Test")).build()

        argumentCaptor<PlacesManagerClient.Callback>().let { callback ->
            whenever(placesManagerClient.getViewObjectData(any(), callback.capture())).then {
                callback.firstValue.onDataLoaded(testMapMarker.data)
            }
        }

        browseMapFragmentViewModel.mapSelectionMode = MapSelectionMode.FULL
        browseMapFragmentViewModel.onMapObjectsReceived(listOf(testMapMarker))
        verify(placesManagerClient).getViewObjectData(eq(testMapMarker), any())
        assertEquals(testMapMarker.data.toPlaceDetailData(), browseMapFragmentViewModel.placeDetailComponentObservable.value!!.data)
    }

    @Test
    fun customOnMapClickListenerOnMapClickTrueTest() {
        val testViewObject = ProxyPlace.create(48.143489, 17.150560, byteArrayOf()).withPayload(BasicData("Test")).build()
        val testMapMarker = mock<MapMarker>()
        val onMapClickListener = mock<OnMapClickListener>()

        whenever(testMapMarker.data).thenReturn(mock())
        whenever(onMapClickListener.onMapClick(any(), any(), any())).thenReturn(true)
        whenever(onMapClickListener.showDetailsView()).thenReturn(true)
        whenever(onMapClickListener.getClickMapMarker(any(), any())).thenReturn(testMapMarker)
        argumentCaptor<PlacesManagerClient.Callback>().let { callback ->
            whenever(placesManagerClient.getViewObjectData(any(), callback.capture())).then {
                callback.firstValue.onDataLoaded(testViewObject.data)
            }
        }

        browseMapFragmentViewModel.onMapClickListener = onMapClickListener
        browseMapFragmentViewModel.mapSelectionMode = MapSelectionMode.FULL
        browseMapFragmentViewModel.onMapObjectsReceived(listOf(testViewObject))
        verify(extendedMapDataModel).addMapObject(any<MapMarker>())
        verify(placesManagerClient).getViewObjectData(any(), any())
        verify(onMapClickListener).onMapClick(eq(SelectionType.PLACE), any(), any())
        verify(onMapClickListener).onMapDataReceived(eq(testViewObject.data))
        browseMapFragmentViewModel.placeDetailComponentObservable.test().assertHasValue()
    }

    @Test
    fun customOnMapClickListenerOnMapClickFalseTest() {
        val testPosition = GeoCoordinates(48.143489, 17.150560)
        val testViewObject = ProxyPlace.create(testPosition.latitude, testPosition.latitude, byteArrayOf()).withPayload(BasicData("Test")).build()
        val onMapClickListener = mock<OnMapClickListener>()

        whenever(onMapClickListener.onMapClick(any(), any(), any())).thenReturn(false)

        browseMapFragmentViewModel.onMapClickListener = onMapClickListener
        browseMapFragmentViewModel.mapSelectionMode = MapSelectionMode.FULL
        browseMapFragmentViewModel.onMapObjectsReceived(listOf(testViewObject))
        verify(extendedMapDataModel, never()).addMapObject(any<MapMarker>())
        verify(placesManagerClient, never()).getViewObjectData(any(), any())
        verify(onMapClickListener).onMapClick(eq(SelectionType.PLACE), any(), any())
        verify(onMapClickListener, never()).onMapDataReceived(any())
        browseMapFragmentViewModel.placeDetailComponentObservable.test().assertNoValue()
    }

    @Test
    fun customOnMapClickListenerShowDetailsViewFalseTest() {
        val testViewObject = ProxyPlace.create(48.143489, 17.150560, byteArrayOf()).withPayload(BasicData("Test")).build()
        val testMapMarker = mock<MapMarker>()
        val onMapClickListener = mock<OnMapClickListener>()

        whenever(testMapMarker.data).thenReturn(mock())
        whenever(onMapClickListener.onMapClick(any(), any(), any())).thenReturn(true)
        whenever(onMapClickListener.showDetailsView()).thenReturn(false)
        whenever(onMapClickListener.getClickMapMarker(any(), any())).thenReturn(testMapMarker)
        argumentCaptor<PlacesManagerClient.Callback>().let { callback ->
            whenever(placesManagerClient.getViewObjectData(any(), callback.capture())).then {
                callback.firstValue.onDataLoaded(testViewObject.data)
            }
        }

        browseMapFragmentViewModel.onMapClickListener = onMapClickListener
        browseMapFragmentViewModel.mapSelectionMode = MapSelectionMode.FULL
        browseMapFragmentViewModel.onMapObjectsReceived(listOf(testViewObject))
        verify(extendedMapDataModel).addMapObject(any<MapMarker>())
        verify(placesManagerClient).getViewObjectData(any(), any())
        verify(onMapClickListener).onMapClick(eq(SelectionType.PLACE), any(), any())
        verify(onMapClickListener).onMapDataReceived(eq(testViewObject.data))
        browseMapFragmentViewModel.placeDetailComponentObservable.test().assertNoValue()
    }

    @Test
    fun customOnMapClickListenerGetClickMapMarkerNullTest() {
        val testPosition = GeoCoordinates(48.143489, 17.150560)
        val testViewObject = ProxyPlace.create(testPosition.latitude, testPosition.latitude, byteArrayOf()).withPayload(BasicData("Test")).build()
        val onMapClickListener = mock<OnMapClickListener>()

        whenever(onMapClickListener.onMapClick(any(), any(), any())).thenReturn(true)
        whenever(onMapClickListener.getClickMapMarker(any(), any())).thenReturn(null)
        argumentCaptor<PlacesManagerClient.Callback>().let { callback ->
            whenever(placesManagerClient.getViewObjectData(any(), callback.capture())).then {
                callback.firstValue.onDataLoaded(testViewObject.data)
            }
        }

        browseMapFragmentViewModel.onMapClickListener = onMapClickListener
        browseMapFragmentViewModel.mapSelectionMode = MapSelectionMode.FULL
        browseMapFragmentViewModel.onMapObjectsReceived(listOf(testViewObject))
        verify(extendedMapDataModel, never()).addMapObject(any<MapMarker>())
        verify(placesManagerClient).getViewObjectData(any(), any())
        verify(onMapClickListener).onMapClick(eq(SelectionType.PLACE), any(), any())
        verify(onMapClickListener).onMapDataReceived(eq(testViewObject.data))
        browseMapFragmentViewModel.placeDetailComponentObservable.test().assertNoValue()
    }

    @Test
    fun placeDetailsViewTest() {
        val testMapMarker = MapMarker.at(48.143489, 17.150560).withPayload(BasicData("Test")).build()
        val detailsViewFactory = mock<DetailsViewFactory>()

        argumentCaptor<PlacesManagerClient.Callback>().let { callback ->
            whenever(placesManagerClient.getViewObjectData(any(), callback.capture())).then {
                callback.firstValue.onDataLoaded(testMapMarker.data)
            }
        }

        browseMapFragmentViewModel.detailsViewFactory = detailsViewFactory
        browseMapFragmentViewModel.mapSelectionMode = MapSelectionMode.FULL
        browseMapFragmentViewModel.onMapObjectsReceived(listOf(testMapMarker))
        verify(extendedMapDataModel, never()).addMapObject(any<MapMarker>())
        verify(placesManagerClient).getViewObjectData(eq(testMapMarker), any())
        verify(extendedMapDataModel).addMapObject(any())
        browseMapFragmentViewModel.placeDetailComponentObservable.test().assertNoValue()
    }

    @Test
    fun onStopTest() {
        browseMapFragmentViewModel.onStop(mock())

        verify(positionManagerClient.sdkPositionUpdatingEnabled).value = false
    }

    @Test
    fun onDestroyTest() {
        browseMapFragmentViewModel.onDestroy(mock())

        assertEquals(null, browseMapFragmentViewModel.onMapClickListener)
        assertEquals(null, browseMapFragmentViewModel.detailsViewFactory)
    }
}