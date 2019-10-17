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
import androidx.lifecycle.Observer
import com.jraska.livedata.test
import com.nhaarman.mockitokotlin2.*
import com.sygic.maps.module.common.KEY_DISTANCE_UNITS
import com.sygic.maps.module.common.theme.ThemeManager
import com.sygic.maps.module.navigation.types.SignpostType
import com.sygic.maps.module.navigation.utils.NavigationTestLifecycleOwner
import com.sygic.maps.module.navigation.viewmodel.NavigationFragmentViewModel
import com.sygic.maps.uikit.viewmodels.common.location.LocationManager
import com.sygic.maps.uikit.viewmodels.common.navigation.NavigationManagerClient
import com.sygic.maps.uikit.viewmodels.common.navigation.preview.RouteDemonstrationManagerClient
import com.sygic.maps.uikit.viewmodels.common.permission.PermissionsManager
import com.sygic.maps.uikit.viewmodels.common.position.PositionManagerClient
import com.sygic.maps.uikit.viewmodels.common.regional.RegionalManager
import com.sygic.maps.uikit.viewmodels.common.sdk.model.ExtendedCameraModel
import com.sygic.maps.uikit.viewmodels.common.sdk.model.ExtendedMapDataModel
import com.sygic.maps.uikit.viewmodels.common.sound.SoundManager
import com.sygic.maps.uikit.viewmodels.navigation.infobar.button.InfobarButtonType
import com.sygic.maps.uikit.viewmodels.navigation.infobar.button.OnInfobarButtonClickListener
import com.sygic.maps.uikit.viewmodels.navigation.infobar.button.OnInfobarButtonClickListenerWrapper
import com.sygic.maps.uikit.views.common.extensions.asMutable
import com.sygic.maps.uikit.views.common.extensions.asSingleEvent
import com.sygic.maps.uikit.views.common.extensions.put
import com.sygic.maps.uikit.views.common.units.DistanceUnit
import com.sygic.maps.uikit.views.common.utils.TextHolder
import com.sygic.maps.uikit.views.navigation.actionmenu.data.ActionMenuData
import com.sygic.maps.uikit.views.navigation.actionmenu.data.ActionMenuItem
import com.sygic.maps.uikit.views.navigation.actionmenu.listener.ActionMenuItemClickListener
import com.sygic.maps.uikit.views.navigation.actionmenu.listener.ActionMenuItemsProviderWrapper
import com.sygic.maps.uikit.views.navigation.infobar.buttons.InfobarButton
import com.sygic.sdk.route.RouteInfo
import org.junit.Assert.assertEquals
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
    private lateinit var extendedCameraModel: ExtendedCameraModel
    @Mock
    private lateinit var navigationManagerClient: NavigationManagerClient
    @Mock
    private lateinit var locationManager: LocationManager
    @Mock
    private lateinit var positionManagerClient: PositionManagerClient
    @Mock
    private lateinit var permissionsManager: PermissionsManager
    @Mock
    private lateinit var regionalManager: RegionalManager
    @Mock
    private lateinit var routeDemonstrationManagerClient: RouteDemonstrationManagerClient
    @Mock
    private lateinit var soundManager: SoundManager
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

        whenever(app.resources).thenReturn(mock())
        whenever(app.resources.configuration).thenReturn(mock())
        whenever(regionalManager.distanceUnit).thenReturn(mock())
        whenever(routeDemonstrationManagerClient.demonstrationState).thenReturn(mock())

        val arguments = mock<Bundle>()
        whenever(arguments.getParcelable<RouteInfo>(eq(KEY_ROUTE))).thenReturn(mock())
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
            regionalManager,
            positionManagerClient,
            extendedCameraModel,
            extendedMapDataModel,
            soundManager,
            locationManager,
            permissionsManager,
            navigationManagerClient,
            routeDemonstrationManagerClient
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

        verify(locationManager).requestToEnableGps(any(), any())
        verify(permissionsManager).checkPermissionGranted(anyString(), any())

        assertEquals(R.drawable.ic_more, navigationFragmentViewModel.leftInfobarButton.value!!.imageResource)
        assertEquals(R.color.white, navigationFragmentViewModel.leftInfobarButton.value!!.imageTintColor)
        assertEquals(R.drawable.bg_infobar_button_rounded, navigationFragmentViewModel.leftInfobarButton.value!!.backgroundResource)
        assertEquals(R.color.colorAccent, navigationFragmentViewModel.leftInfobarButton.value!!.backgroundTintColor)

        assertEquals(R.drawable.ic_close, navigationFragmentViewModel.rightInfobarButton.value!!.imageResource)
        assertEquals(R.color.white, navigationFragmentViewModel.rightInfobarButton.value!!.imageTintColor)
        assertEquals(R.drawable.bg_infobar_button_rounded, navigationFragmentViewModel.rightInfobarButton.value!!.backgroundResource)
        assertEquals(R.color.brick_red, navigationFragmentViewModel.rightInfobarButton.value!!.backgroundTintColor)
    }

    @Test
    fun onCreateTest() {
        val inInfobarButtonClickListenerProviderComponentMock = mock<LiveData<Map<InfobarButtonType, OnInfobarButtonClickListener?>>>()
        val onInfobarButtonClickListenerWrapperLifecycleOwnerMock = mock<LifecycleOwner>(extraInterfaces = arrayOf(OnInfobarButtonClickListenerWrapper::class))
        whenever((onInfobarButtonClickListenerWrapperLifecycleOwnerMock as OnInfobarButtonClickListenerWrapper).infobarButtonClickListenerProvidersMap).thenReturn(
            inInfobarButtonClickListenerProviderComponentMock
        )
        navigationFragmentViewModel.onCreate(onInfobarButtonClickListenerWrapperLifecycleOwnerMock)
        verify(inInfobarButtonClickListenerProviderComponentMock).observe(eq(onInfobarButtonClickListenerWrapperLifecycleOwnerMock), any())

        val actionMenuItemsProviderComponentMock = mock<LiveData<ActionMenuItemsProviderWrapper.ProviderComponent>>()
        val actionMenuItemsProviderWrapperLifecycleOwnerMock = mock<LifecycleOwner>(extraInterfaces = arrayOf(ActionMenuItemsProviderWrapper::class))
        whenever((actionMenuItemsProviderWrapperLifecycleOwnerMock as ActionMenuItemsProviderWrapper).actionMenuItemsProvider).thenReturn(
            actionMenuItemsProviderComponentMock
        )
        navigationFragmentViewModel.onCreate(actionMenuItemsProviderWrapperLifecycleOwnerMock)
        verify(actionMenuItemsProviderComponentMock).observe(eq(actionMenuItemsProviderWrapperLifecycleOwnerMock), any())
    }

    @Test
    fun onStartTest() {
        reset(locationManager)
        navigationFragmentViewModel.onStart(mock())

        verify(locationManager).positionOnMapEnabled = any()
        verify(extendedCameraModel).addModeChangedListener(navigationFragmentViewModel)
        verify(navigationManagerClient).addOnRouteChangedListener(navigationFragmentViewModel)
        navigationFragmentViewModel.actionMenuItemClickListenerObservable.asSingleEvent().test().assertValue(navigationFragmentViewModel.actionMenuItemClickListener)
    }

    @Test
    fun onResumeTest() {
        navigationFragmentViewModel.onResume(mock())

        verify(extendedCameraModel).mapCenterSettings = any()
    }

    @Test
    fun onLeftInfobarButtonClickTest() {
        val infobarButtonType = InfobarButtonType.LEFT
        val onInfobarButtonClickListener = spy(object : OnInfobarButtonClickListener {
            override val button: InfobarButton = InfobarButton(R.drawable.ic_map_lock_full, R.drawable.bg_info_toast, R.color.black, R.color.white)
            override fun onButtonClick() {}
        })
        val testLifecycleOwner = NavigationTestLifecycleOwner()
        testLifecycleOwner.infobarButtonClickListenerProvidersMap.asMutable().put(infobarButtonType, onInfobarButtonClickListener)
        testLifecycleOwner.onResume()

        navigationFragmentViewModel.onCreate(testLifecycleOwner)

        assertEquals(R.drawable.ic_map_lock_full, navigationFragmentViewModel.leftInfobarButton.value!!.imageResource)
        assertEquals(R.color.black, navigationFragmentViewModel.leftInfobarButton.value!!.imageTintColor)
        assertEquals(R.drawable.bg_info_toast, navigationFragmentViewModel.leftInfobarButton.value!!.backgroundResource)
        assertEquals(R.color.white, navigationFragmentViewModel.leftInfobarButton.value!!.backgroundTintColor)

        navigationFragmentViewModel.onLeftInfobarButtonClick()
        verify(onInfobarButtonClickListener).onButtonClick()
    }

    @Test
    fun onRightInfobarButtonClickTest() {
        val infobarButtonType = InfobarButtonType.RIGHT
        val onInfobarButtonClickListener = spy(object : OnInfobarButtonClickListener {
            override val button: InfobarButton = InfobarButton(R.drawable.ic_location, R.drawable.bg_signpost_rounded, R.color.brick_red, R.color.black)
            override fun onButtonClick() {}
        })
        val testLifecycleOwner = NavigationTestLifecycleOwner()
        testLifecycleOwner.infobarButtonClickListenerProvidersMap.asMutable().put(infobarButtonType, onInfobarButtonClickListener)
        testLifecycleOwner.onResume()

        navigationFragmentViewModel.onCreate(testLifecycleOwner)

        assertEquals(R.drawable.ic_location, navigationFragmentViewModel.rightInfobarButton.value!!.imageResource)
        assertEquals(R.color.brick_red, navigationFragmentViewModel.rightInfobarButton.value!!.imageTintColor)
        assertEquals(R.drawable.bg_signpost_rounded, navigationFragmentViewModel.rightInfobarButton.value!!.backgroundResource)
        assertEquals(R.color.black, navigationFragmentViewModel.rightInfobarButton.value!!.backgroundTintColor)

        navigationFragmentViewModel.onRightInfobarButtonClick()
        verify(onInfobarButtonClickListener).onButtonClick()
    }

    @Test
    fun infobarActionMenuItemsTest() {
        val actionMenuData = ActionMenuData(
            TextHolder.from(R.string.follow_the_route),
            listOf(
                ActionMenuItem(R.drawable.ic_bus, TextHolder.from("Bus")),
                ActionMenuItem(R.drawable.ic_train_station),
                ActionMenuItem(R.drawable.ic_cafe, TextHolder.from(R.string.category)),
                ActionMenuItem(R.drawable.ic_cake)
            )
        )
        val actionMenuItemClickListener = spy(object : ActionMenuItemClickListener {
            override fun onActionMenuItemClick(actionMenuItem: ActionMenuItem) {}
        })
        val actionMenuItemsProviderComponent = ActionMenuItemsProviderWrapper.ProviderComponent(actionMenuData, actionMenuItemClickListener)
        val testLifecycleOwner = NavigationTestLifecycleOwner()
        testLifecycleOwner.actionMenuItemsProvider.asMutable().value = actionMenuItemsProviderComponent
        testLifecycleOwner.onResume()

        navigationFragmentViewModel.onCreate(testLifecycleOwner)
        navigationFragmentViewModel.onLeftInfobarButtonClick()

        navigationFragmentViewModel.actionMenuShowObservable.test().assertHasValue()
        navigationFragmentViewModel.actionMenuShowObservable.test().assertValue(actionMenuData)
    }

    @Test
    fun onRouteChangedTest() {
        val routeInfoMock = mock<RouteInfo>()

        whenever(extendedMapDataModel.getMapObjects()).thenReturn(setOf())

        navigationFragmentViewModel.onRouteChanged(routeInfoMock)

        verify(extendedMapDataModel).removeAllMapRoutes()
        verify(extendedMapDataModel).addMapRoute(any())
        navigationFragmentViewModel.routeInfo.test().assertValue(routeInfoMock)
    }

    @Test
    fun onStopTest() {
        navigationFragmentViewModel.onStop(mock())

        verify(locationManager).positionOnMapEnabled = false
        verify(extendedCameraModel).removeModeChangedListener(navigationFragmentViewModel)
        verify(navigationManagerClient).removeOnRouteChangedListener(navigationFragmentViewModel)
    }
}