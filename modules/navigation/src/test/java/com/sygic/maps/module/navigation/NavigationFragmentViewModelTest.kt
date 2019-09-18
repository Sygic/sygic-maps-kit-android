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
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
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
        //todo
        /*argumentCaptor<(() -> Unit)>().let { callback ->
            whenever(locationManager.requestToEnableGps(callback.capture(), any())).then {
                callback.firstValue.invoke()
            }
        }

        argumentCaptor<Observer<Boolean>>().let { callback ->
            whenever(permissionsManager.checkPermissionGranted(anyString(), callback.capture())).then {
                callback.firstValue.onChanged(true)
            }
        }

        whenever(locationManager.positionOnMapEnabled).thenReturn(true)*/
        whenever(regionalManager.distanceUnit).thenReturn(MutableLiveData(DistanceUnit.KILOMETERS))

        val arguments = mock<Bundle>()
        whenever(arguments.getParcelable<RouteInfo>(eq(KEY_ROUTE_INFO))).thenReturn(mock()) //todo
        whenever(arguments.getBoolean(eq(KEY_PREVIEW_MODE), any())).thenReturn(true)
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
}