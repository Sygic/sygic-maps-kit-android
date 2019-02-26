package com.sygic.modules.browsemap

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.nhaarman.mockitokotlin2.*
import com.sygic.modules.browsemap.viewmodel.BrowseMapFragmentViewModel
import com.sygic.modules.common.component.MapFragmentInitComponent
import com.sygic.modules.common.mapinteraction.MapSelectionMode
import com.sygic.modules.common.mapinteraction.manager.MapInteractionManager
import com.sygic.modules.common.poi.manager.PoiDataManager
import com.sygic.sdk.map.`object`.MapMarker
import com.sygic.sdk.map.`object`.ViewObject
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
        val locationSuccessCallback = argumentCaptor<(() -> Unit)>()
        whenever(
            locationManager.requestToEnableGps(
                locationSuccessCallback.capture(),
                any()
            )
        ).then {
            locationSuccessCallback.firstValue.invoke()
        }

        val permissionCallback = argumentCaptor<Observer<Boolean>>()
        whenever(permissionsManager.checkPermissionGranted(anyString(), permissionCallback.capture())).then {
            permissionCallback.firstValue.onChanged(true)
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
    fun onMapClickTest() {
        browseMapFragmentViewModel.onMapObjectsRequestStarted()
        verify(extendedMapDataModel).removeOnClickMapMarker()

        val emptyViewObjectList = listOf<ViewObject>()
        browseMapFragmentViewModel.onMapObjectsReceived(emptyViewObjectList)
        //todo: test it and reset

        val viewObjectList = mutableListOf<ViewObject>()
        viewObjectList.add(MapMarker(48.143489, 17.150560)) //todo: switch to map marker branch

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