package com.sygic.modules.browsemap

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.jraska.livedata.test
import com.nhaarman.mockitokotlin2.*
import com.sygic.modules.browsemap.viewmodel.BrowseMapFragmentViewModel
import com.sygic.modules.common.component.MapFragmentInitComponent
import com.sygic.modules.common.detail.DetailsViewFactory
import com.sygic.modules.common.mapinteraction.MapSelectionMode
import com.sygic.modules.common.mapinteraction.manager.MapInteractionManager
import com.sygic.modules.common.poi.manager.PoiDataManager
import com.sygic.sdk.map.`object`.MapCircle
import com.sygic.sdk.map.`object`.MapMarker
import com.sygic.sdk.map.`object`.MapRoute
import com.sygic.sdk.map.`object`.payload.BasicPayload
import com.sygic.sdk.position.GeoCoordinates
import com.sygic.ui.common.sdk.listener.OnMapClickListener
import com.sygic.ui.common.sdk.location.LocationManager
import com.sygic.ui.common.sdk.model.ExtendedMapDataModel
import com.sygic.ui.common.sdk.permission.PermissionsManager
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
            permissionsManager
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
    fun onStartTest() {
        whenever(locationManager.positionOnMapEnabled).thenReturn(true)
        browseMapFragmentViewModel.onStart(mock())
        verify(locationManager).setSdkPositionUpdatingEnabled(true)

        reset(locationManager)

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
        verify(poiDataManager, never()).getPayloadData(any(), any())
    }

    @Test
    fun onMapObjectsReceivedSelectionModeNoneTest() {
        val testMapMarker = MapMarker(48.143489, 17.150560)

        browseMapFragmentViewModel.mapSelectionMode = MapSelectionMode.NONE
        browseMapFragmentViewModel.onMapObjectsReceived(listOf(testMapMarker, mock()))
        verify(poiDataManager, never()).getPayloadData(any(), any())
    }

    @Test
    fun onMapObjectsReceivedSelectionModeMarkersOnlyCirclesListTest() {
        val tesViewObject = MapCircle(GeoCoordinates(48.143489, 17.150560), 60.0)

        browseMapFragmentViewModel.mapSelectionMode = MapSelectionMode.MARKERS_ONLY
        browseMapFragmentViewModel.onMapObjectsReceived(listOf(tesViewObject))
        verify(poiDataManager, never()).getPayloadData(any(), any())
    }

    @Test
    fun onMapObjectsReceivedSelectionModeMarkersOnlyMapMarkerListTest() {
        val testMapMarker = MapMarker(48.143489, 17.150560)
        val testPayload = BasicPayload(GeoCoordinates(48.143489, 17.150560), "Test", "")

        argumentCaptor<PoiDataManager.Callback>().let { callback ->
            whenever(poiDataManager.getPayloadData(any(), callback.capture())).then {
                callback.firstValue.onDataLoaded(testPayload)
            }
        }

        browseMapFragmentViewModel.mapSelectionMode = MapSelectionMode.MARKERS_ONLY
        browseMapFragmentViewModel.onMapObjectsReceived(listOf(testMapMarker, mock()))
        verify(poiDataManager).getPayloadData(eq(testMapMarker), any())
        browseMapFragmentViewModel.dataPayloadObservable.test().assertValue(testPayload)
    }

    @Test
    fun onMapObjectsReceivedSelectionModeFullMapRouteTest() {
        val tesViewObject = MapRoute(mock(), MapRoute.RouteType.Primary)
        val testPayload = BasicPayload(GeoCoordinates(48.143489, 17.150560), "Test", "")

        argumentCaptor<PoiDataManager.Callback>().let { callback ->
            whenever(poiDataManager.getPayloadData(any(), callback.capture())).then {
                callback.firstValue.onDataLoaded(testPayload)
            }
        }

        browseMapFragmentViewModel.mapSelectionMode = MapSelectionMode.FULL
        browseMapFragmentViewModel.onMapObjectsReceived(listOf(tesViewObject))
        verify(poiDataManager).getPayloadData(any(), any())
        verify(extendedMapDataModel).addOnClickMapMarker(any())
        browseMapFragmentViewModel.dataPayloadObservable.test().assertValue(testPayload)
    }

    @Test
    fun onMapObjectsReceivedSelectionModeFullMapMarkerTest() {
        val testPayload = BasicPayload(GeoCoordinates(48.143489, 17.150560), "Test", "")
        val tesMapMarker = MapMarker.Builder().payload(testPayload).build()

        argumentCaptor<PoiDataManager.Callback>().let { callback ->
            whenever(poiDataManager.getPayloadData(any(), callback.capture())).then {
                callback.firstValue.onDataLoaded(testPayload)
            }
        }

        browseMapFragmentViewModel.mapSelectionMode = MapSelectionMode.FULL
        browseMapFragmentViewModel.onMapObjectsReceived(listOf(tesMapMarker))
        verify(poiDataManager).getPayloadData(eq(tesMapMarker), any())
        browseMapFragmentViewModel.dataPayloadObservable.test().assertValue(testPayload)
    }

    @Test
    fun customOnMapClickListenerConsumedTest() {
        val testPayload = BasicPayload(GeoCoordinates(48.143489, 17.150560), "Test", "")
        val tesMapMarker = MapMarker.Builder().payload(testPayload).build()
        val onMapClickListener = mock<OnMapClickListener>()

        whenever(onMapClickListener.onMapClick(any())).thenReturn(true)
        argumentCaptor<PoiDataManager.Callback>().let { callback ->
            whenever(poiDataManager.getPayloadData(any(), callback.capture())).then {
                callback.firstValue.onDataLoaded(testPayload)
            }
        }

        browseMapFragmentViewModel.onMapClickListener = onMapClickListener
        browseMapFragmentViewModel.mapSelectionMode = MapSelectionMode.FULL
        browseMapFragmentViewModel.onMapObjectsReceived(listOf(tesMapMarker))
        verify(extendedMapDataModel, never()).addOnClickMapMarker(any())
        verify(poiDataManager).getPayloadData(eq(tesMapMarker), any())
        verify(onMapClickListener).onMapClick(eq(testPayload))
        browseMapFragmentViewModel.dataPayloadObservable.test().assertNoValue()
    }

    @Test
    fun customOnMapClickListenerNotConsumedTest() {
        val testPayload = BasicPayload(GeoCoordinates(48.143489, 17.150560), "Test", "")
        val tesMapMarker = MapMarker.Builder().payload(testPayload).build()
        val onMapClickListener = mock<OnMapClickListener>()

        argumentCaptor<PoiDataManager.Callback>().let { callback ->
            whenever(poiDataManager.getPayloadData(any(), callback.capture())).then {
                callback.firstValue.onDataLoaded(testPayload)
            }
        }

        browseMapFragmentViewModel.onMapClickListener = onMapClickListener
        browseMapFragmentViewModel.mapSelectionMode = MapSelectionMode.FULL
        browseMapFragmentViewModel.onMapObjectsReceived(listOf(tesMapMarker))
        verify(extendedMapDataModel, never()).addOnClickMapMarker(any())
        verify(poiDataManager).getPayloadData(eq(tesMapMarker), any())
        verify(onMapClickListener).onMapClick(eq(testPayload))
        browseMapFragmentViewModel.dataPayloadObservable.test().assertHasValue()
    }

    @Test
    fun poiDetailsViewTest() {
        val testPayload = BasicPayload(GeoCoordinates(48.143489, 17.150560), "Test", "")
        val tesMapMarker = MapMarker.Builder().payload(testPayload).build()
        val detailsViewFactory = mock<DetailsViewFactory>()

        argumentCaptor<PoiDataManager.Callback>().let { callback ->
            whenever(poiDataManager.getPayloadData(any(), callback.capture())).then {
                callback.firstValue.onDataLoaded(testPayload)
            }
        }

        browseMapFragmentViewModel.detailsViewFactory = detailsViewFactory
        browseMapFragmentViewModel.mapSelectionMode = MapSelectionMode.FULL
        browseMapFragmentViewModel.onMapObjectsReceived(listOf(tesMapMarker))
        verify(extendedMapDataModel, never()).addOnClickMapMarker(any())
        verify(poiDataManager).getPayloadData(eq(tesMapMarker), any())
        verify(extendedMapDataModel).addMapObject(isA())
        browseMapFragmentViewModel.dataPayloadObservable.test().assertNoValue()
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