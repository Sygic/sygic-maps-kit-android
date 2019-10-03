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

package com.sygic.maps.module.navigation.viewmodel

import android.app.Application
import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.annotation.RestrictTo
import androidx.lifecycle.*
import com.sygic.maps.module.common.theme.ThemeManager
import com.sygic.maps.module.common.viewmodel.ThemeSupportedViewModel
import com.sygic.maps.module.navigation.*
import com.sygic.maps.module.navigation.R
import com.sygic.maps.module.navigation.actionmenu.SoundsOffActionMenuItem
import com.sygic.maps.module.navigation.actionmenu.SoundsOnActionMenuItem
import com.sygic.maps.module.navigation.component.*
import com.sygic.maps.module.navigation.infobar.InternalLeftInfobarClickListener
import com.sygic.maps.module.navigation.infobar.NavigationDefaultLeftInfobarButton
import com.sygic.maps.module.navigation.infobar.NavigationDefaultRightInfobarButton
import com.sygic.maps.module.navigation.infobar.NavigationDefaultUnlockedLeftInfobarButton
import com.sygic.maps.module.navigation.listener.EventListener
import com.sygic.maps.module.navigation.listener.EventListenerWrapper
import com.sygic.maps.module.navigation.types.SignpostType
import com.sygic.maps.tools.annotations.Assisted
import com.sygic.maps.tools.annotations.AutoFactory
import com.sygic.maps.uikit.viewmodels.common.extensions.addMapRoute
import com.sygic.maps.uikit.viewmodels.common.extensions.removeAllMapRoutes
import com.sygic.maps.uikit.viewmodels.common.location.LocationManager
import com.sygic.maps.uikit.viewmodels.common.navigation.preview.RouteDemonstrationManager
import com.sygic.maps.uikit.viewmodels.common.navigation.preview.state.DemonstrationState
import com.sygic.maps.uikit.viewmodels.common.permission.PermissionsManager
import com.sygic.maps.uikit.viewmodels.common.regional.RegionalManager
import com.sygic.maps.uikit.viewmodels.common.sdk.model.ExtendedCameraModel
import com.sygic.maps.uikit.viewmodels.common.sdk.model.ExtendedMapDataModel
import com.sygic.maps.uikit.viewmodels.common.utils.requestLocationAccess
import com.sygic.maps.uikit.viewmodels.navigation.infobar.button.InfobarButtonType
import com.sygic.maps.uikit.viewmodels.navigation.infobar.button.OnInfobarButtonClickListener
import com.sygic.maps.uikit.viewmodels.navigation.infobar.button.OnInfobarButtonClickListenerWrapper
import com.sygic.maps.uikit.views.common.extensions.*
import com.sygic.maps.uikit.views.common.livedata.SingleLiveEvent
import com.sygic.maps.uikit.views.common.toast.InfoToastComponent
import com.sygic.maps.uikit.views.common.units.DistanceUnit
import com.sygic.maps.uikit.views.common.utils.TextHolder
import com.sygic.maps.uikit.views.common.utils.UniqueMutableLiveData
import com.sygic.maps.uikit.views.navigation.actionmenu.data.ActionMenuData
import com.sygic.maps.uikit.views.navigation.actionmenu.data.ActionMenuItem
import com.sygic.maps.uikit.views.navigation.actionmenu.listener.ActionMenuItemClickListener
import com.sygic.maps.uikit.views.navigation.actionmenu.listener.ActionMenuItemsProviderWrapper
import com.sygic.maps.uikit.views.navigation.infobar.buttons.InfobarButton
import com.sygic.sdk.map.Camera
import com.sygic.sdk.map.MapAnimation
import com.sygic.sdk.map.MapCenter
import com.sygic.sdk.map.MapCenterSettings
import com.sygic.sdk.map.`object`.MapRoute
import com.sygic.sdk.navigation.NavigationManager
import com.sygic.sdk.navigation.listeners.NoAudioInstructionListener
import com.sygic.sdk.navigation.listeners.NoAudioWarningListener
import com.sygic.sdk.route.RouteInfo
import com.sygic.sdk.route.Waypoint

private const val DEFAULT_NAVIGATION_TILT = 60f
private val PORTRAIT_MAP_CENTER = MapCenter(0.5f, 0.25f)
private val PORTRAIT_MAP_CENTER_SETTING = MapCenterSettings(
    PORTRAIT_MAP_CENTER,
    PORTRAIT_MAP_CENTER,
    MapAnimation.NONE,
    MapAnimation.NONE
)
private val LANDSCAPE_MAP_CENTER = MapCenter(0.75f, 0.3f)
private val LANDSCAPE_MAP_CENTER_SETTING = MapCenterSettings(
    LANDSCAPE_MAP_CENTER,
    LANDSCAPE_MAP_CENTER,
    MapAnimation.NONE,
    MapAnimation.NONE
)

@AutoFactory
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
class NavigationFragmentViewModel internal constructor(
    app: Application,
    @Assisted arguments: Bundle?,
    themeManager: ThemeManager,
    private val cameraModel: ExtendedCameraModel,
    private val mapDataModel: ExtendedMapDataModel,
    private val regionalManager: RegionalManager,
    private val locationManager: LocationManager,
    private val permissionsManager: PermissionsManager,
    private val navigationManager: NavigationManager,
    private val routeDemonstrationManager: RouteDemonstrationManager
) : ThemeSupportedViewModel(app, arguments, themeManager), DefaultLifecycleObserver,
    NavigationManager.OnRouteChangedListener, NavigationManager.OnWaypointPassedListener, Camera.ModeChangedListener {

    @LayoutRes
    val signpostLayout: Int
    val signpostEnabled = MutableLiveData<Boolean>(SIGNPOST_ENABLED_DEFAULT_VALUE)
    val infobarEnabled = MutableLiveData<Boolean>(INFOBAR_ENABLED_DEFAULT_VALUE)
    val previewControlsEnabled = MutableLiveData<Boolean>(PREVIEW_CONTROLS_ENABLED_DEFAULT_VALUE)
    val currentSpeedEnabled = MutableLiveData<Boolean>(CURRENT_SPEED_ENABLED_DEFAULT_VALUE)
    val speedLimitEnabled = MutableLiveData<Boolean>(SPEED_LIMIT_ENABLED_DEFAULT_VALUE)
    val lanesViewEnabled = MutableLiveData<Boolean>(LANES_VIEW_ENABLED_DEFAULT_VALUE)

    val leftInfobarButton = MutableLiveData<InfobarButton?>()
    val rightInfobarButton = MutableLiveData<InfobarButton?>()

    val previewMode = MutableLiveData<Boolean>(false)
    val routeInfo: MutableLiveData<RouteInfo> = UniqueMutableLiveData()

    val infoToastObservable: LiveData<InfoToastComponent> = SingleLiveEvent()
    val actionMenuShowObservable: LiveData<ActionMenuData> = SingleLiveEvent()
    val actionMenuHideObservable: LiveData<Any> = SingleLiveEvent()
    val actionMenuItemClickListenerObservable: LiveData<ActionMenuItemClickListener> = SingleLiveEvent()
    val activityFinishObservable: LiveData<Any> = SingleLiveEvent()

    private val infobarButtonListenersMap: Map<InfobarButtonType, OnInfobarButtonClickListener?> = mutableMapOf()

    private val navigationDefaultLeftInfobarClickListener = object : InternalLeftInfobarClickListener() {
        override val button = NavigationDefaultLeftInfobarButton()
        override fun onButtonClick() { actionMenuShowObservable.asSingleEvent().value = actionMenuData }
    }

    private val navigationUnlockedLeftInfobarClickListener = object : InternalLeftInfobarClickListener() {
        override val button = NavigationDefaultUnlockedLeftInfobarButton()
        override fun onButtonClick() {
            cameraModel.rotationMode = Camera.RotationMode.Vehicle
            cameraModel.movementMode = Camera.MovementMode.FollowGpsPositionWithAutozoom
        }
    }

    private val navigationDefaultRightInfobarClickListener = object : OnInfobarButtonClickListener {
        override val button = NavigationDefaultRightInfobarButton()
        override fun onButtonClick() = activityFinishObservable.asSingleEvent().call()
    }

    private var actionMenuData: ActionMenuData = ActionMenuData(
        TextHolder.from(R.string.action_menu),
        listOf(
            SoundsOnActionMenuItem(),
            SoundsOffActionMenuItem()
        )
    )

    private var eventListener: EventListener? = null

    var actionMenuItemClickListener: ActionMenuItemClickListener =
        object : ActionMenuItemClickListener {
            override fun onActionMenuItemClick(actionMenuItem: ActionMenuItem) {
                when (actionMenuItem) {
                    is SoundsOnActionMenuItem -> {
                        navigationManager.removeAudioWarningListener()
                        navigationManager.removeAudioInstructionListener()
                        infoToastObservable.asSingleEvent().value =
                            InfoToastComponent(R.drawable.ic_sounds_on, R.string.sounds_enabled)
                    }
                    is SoundsOffActionMenuItem -> {
                        navigationManager.setAudioWarningListener(NoAudioWarningListener())
                        navigationManager.setAudioInstructionListener(NoAudioInstructionListener())
                        infoToastObservable.asSingleEvent().value =
                            InfoToastComponent(R.drawable.ic_sounds_off, R.string.sounds_disabled)
                    }
                }
                actionMenuHideObservable.asSingleEvent().call()
            }
        }
        private set

    var distanceUnit: DistanceUnit
        get() = regionalManager.distanceUnit.value!!
        set(value) {
            regionalManager.distanceUnit.value = value
        }

    private val signpostType: SignpostType

    init {
        with(arguments) {
            previewMode.value = getBoolean(KEY_PREVIEW_MODE, PREVIEW_MODE_DEFAULT_VALUE)
            infobarEnabled.value = getBoolean(KEY_INFOBAR_ENABLED, INFOBAR_ENABLED_DEFAULT_VALUE)
            previewControlsEnabled.value =
                getBoolean(KEY_PREVIEW_CONTROLS_ENABLED, PREVIEW_CONTROLS_ENABLED_DEFAULT_VALUE)
            currentSpeedEnabled.value = getBoolean(KEY_CURRENT_SPEED_ENABLED, CURRENT_SPEED_ENABLED_DEFAULT_VALUE)
            speedLimitEnabled.value = getBoolean(KEY_SPEED_LIMIT_ENABLED, SPEED_LIMIT_ENABLED_DEFAULT_VALUE)
            signpostEnabled.value = getBoolean(KEY_SIGNPOST_ENABLED, SIGNPOST_ENABLED_DEFAULT_VALUE)
            lanesViewEnabled.value = getBoolean(KEY_LANES_VIEW_ENABLED, LANES_VIEW_ENABLED_DEFAULT_VALUE)
            signpostType = getParcelableValue(KEY_SIGNPOST_TYPE) ?: SIGNPOST_TYPE_DEFAULT_VALUE
            signpostLayout = when (signpostType) {
                SignpostType.FULL -> R.layout.layout_signpost_full_view_stub
                SignpostType.SIMPLIFIED -> R.layout.layout_signpost_simplified_view_stub
            }
            distanceUnit = getParcelableValue(KEY_DISTANCE_UNITS) ?: DISTANCE_UNITS_DEFAULT_VALUE
            getParcelableValue<RouteInfo>(KEY_ROUTE_INFO)?.let { routeInfo.value = it }
        }

        routeInfo.observeForever(::setRouteInfo)
        previewMode.withLatestFrom(routeInfo).observeForever { processRoutePreview(it.first, it.second) }

        updateInfobarListenersMap(InfobarButtonType.LEFT, navigationDefaultLeftInfobarClickListener)
        updateInfobarListenersMap(InfobarButtonType.RIGHT, navigationDefaultRightInfobarClickListener)
    }

    override fun onCreate(owner: LifecycleOwner) {
        if (owner is EventListenerWrapper) {
            owner.eventListenerProvider.observe(owner, Observer {
                eventListener = it
            })
        }
        if (owner is OnInfobarButtonClickListenerWrapper) {
            owner.infobarButtonClickListenerProvidersMap.observe(owner, Observer { map ->
                map.forEach { updateInfobarListenersMap(it.key, it.value) }
            })
        }
        if (owner is ActionMenuItemsProviderWrapper) {
            owner.actionMenuItemsProvider.observe(owner, Observer {
                actionMenuData = it.actionMenuData
                actionMenuItemClickListener = it.actionMenuItemClickListener
            })
        }

        routeDemonstrationManager.demonstrationState.observe(owner, Observer {
            if (routeInfo.value != null) {
                when (it) {
                    DemonstrationState.ACTIVE -> eventListener?.onNavigationStarted(routeInfo.value)
                    DemonstrationState.STOPPED -> eventListener?.onNavigationFinished()
                    else -> { /* do nothing */ }
                }
            }
        })
        eventListener?.onNavigationCreated()
    }

    fun isLanesViewEmbedded() = signpostEnabled.value!! && signpostType == SignpostType.FULL

    override fun onStart(owner: LifecycleOwner) {
        locationManager.positionOnMapEnabled = !previewMode.value!! || routeDemonstrationManager.demonstrationState.value == DemonstrationState.ACTIVE
        cameraModel.addModeChangedListener(this)
        navigationManager.addOnRouteChangedListener(this)
        navigationManager.addOnWaypointPassedListener(this)
        actionMenuItemClickListenerObservable.asSingleEvent().value = actionMenuItemClickListener
    }

    override fun onResume(owner: LifecycleOwner) {
        cameraModel.mapCenterSettings = if (isLandscape()) LANDSCAPE_MAP_CENTER_SETTING else PORTRAIT_MAP_CENTER_SETTING
    }

    override fun onRouteChanged(routeInfo: RouteInfo?) {
        eventListener?.onRouteChanged(routeInfo)
        mapDataModel.removeAllMapRoutes()
        routeInfo?.let {
            this.routeInfo.value = it
            mapDataModel.addMapRoute(MapRoute.from(it).build())
        }
    }

    override fun onFinishReached() {
        mapDataModel.removeAllMapRoutes()
        navigationManager.stopNavigation()
        eventListener?.onRouteFinishReached()
        eventListener?.onNavigationFinished()
    }

    override fun onMovementModeChanged(@Camera.MovementMode mode: Int) {
        if (infobarButtonListenersMap[InfobarButtonType.LEFT] is InternalLeftInfobarClickListener) {
            when (mode) {
                Camera.MovementMode.Free ->
                    updateInfobarListenersMap(
                        InfobarButtonType.LEFT,
                        navigationUnlockedLeftInfobarClickListener
                    )
                Camera.MovementMode.FollowGpsPosition, Camera.MovementMode.FollowGpsPositionWithAutozoom ->
                    updateInfobarListenersMap(
                        InfobarButtonType.LEFT,
                        navigationDefaultLeftInfobarClickListener
                    )
            }
        }
    }

    override fun onWaypointPassed(waypoint: Waypoint) {}
    override fun onRotationModeChanged(@Camera.RotationMode mode: Int) {}

    private fun setRouteInfo(routeInfo: RouteInfo) {
        // set the default navigation camera state
        cameraModel.apply {
            tilt = DEFAULT_NAVIGATION_TILT
            movementMode = Camera.MovementMode.FollowGpsPositionWithAutozoom
            rotationMode = Camera.RotationMode.Vehicle
        }

        // set the new RouteInfo for navigation
        navigationManager.setRouteForNavigation(routeInfo)
        // notify listener
        eventListener?.onNavigationStarted(routeInfo)
    }

    private fun processRoutePreview(previewActive: Boolean, routeInfo: RouteInfo) {
        if (previewActive) {
            // start preview mode
            locationManager.positionOnMapEnabled = false
            routeDemonstrationManager.start(routeInfo)
        } else {
            // stop the previous demonstration first
            routeDemonstrationManager.stop()

            // start navigation mode
            requestLocationAccess(permissionsManager, locationManager) {
                locationManager.positionOnMapEnabled = true
            }
        }
    }

    private fun updateInfobarListenersMap(
        buttonType: InfobarButtonType,
        listener: OnInfobarButtonClickListener?
    ) {
        (infobarButtonListenersMap as MutableMap)[buttonType] = listener
        when (buttonType) {
            InfobarButtonType.LEFT -> leftInfobarButton.value = listener?.button
            InfobarButtonType.RIGHT -> rightInfobarButton.value = listener?.button
        }
    }

    fun onLeftInfobarButtonClick() = infobarButtonListenersMap[InfobarButtonType.LEFT]?.onButtonClick()

    fun onRightInfobarButtonClick() = infobarButtonListenersMap[InfobarButtonType.RIGHT]?.onButtonClick()

    override fun onStop(owner: LifecycleOwner) {
        locationManager.positionOnMapEnabled = false
        cameraModel.removeModeChangedListener(this)
        navigationManager.removeOnRouteChangedListener(this)
        navigationManager.removeOnWaypointPassedListener(this)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        eventListener?.onNavigationDestroyed()
        eventListener = null
    }

    override fun onCleared() {
        super.onCleared()

        mapDataModel.removeAllMapRoutes()
        routeDemonstrationManager.destroy()
        navigationManager.stopNavigation()
    }
}
