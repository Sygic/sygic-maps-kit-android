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

package com.sygic.maps.module.browsemap.viewmodel

import android.app.Application
import android.os.Bundle
import androidx.annotation.RestrictTo
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.sygic.maps.module.browsemap.*
import com.sygic.maps.module.browsemap.detail.PlaceDetailsObject
import com.sygic.maps.module.common.component.*
import com.sygic.maps.module.common.detail.DetailsViewFactory
import com.sygic.maps.module.common.extensions.onMapClick
import com.sygic.maps.module.common.listener.OnMapClickListener
import com.sygic.maps.module.common.listener.OnMapClickListenerWrapper
import com.sygic.maps.module.common.mapinteraction.MapSelectionMode
import com.sygic.maps.module.common.mapinteraction.manager.MapInteractionManager
import com.sygic.maps.module.common.provider.ModuleConnectionProvider
import com.sygic.maps.module.common.provider.ModuleConnectionProviderWrapper
import com.sygic.maps.module.common.provider.ProviderType
import com.sygic.maps.module.common.theme.ThemeManager
import com.sygic.maps.module.common.viewmodel.MapFragmentViewModel
import com.sygic.maps.tools.annotations.Assisted
import com.sygic.maps.tools.annotations.AutoFactory
import com.sygic.maps.uikit.viewmodels.common.extensions.addMapMarker
import com.sygic.maps.uikit.viewmodels.common.extensions.getCopyWithPayload
import com.sygic.maps.uikit.viewmodels.common.extensions.removeMapMarker
import com.sygic.maps.uikit.viewmodels.common.extensions.toPlaceDetailData
import com.sygic.maps.uikit.viewmodels.common.location.LocationManager
import com.sygic.maps.uikit.viewmodels.common.permission.PermissionsManager
import com.sygic.maps.uikit.viewmodels.common.place.PlacesManagerClient
import com.sygic.maps.uikit.viewmodels.common.position.PositionManagerClient
import com.sygic.maps.uikit.viewmodels.common.regional.RegionalManager
import com.sygic.maps.uikit.viewmodels.common.sdk.model.ExtendedMapDataModel
import com.sygic.maps.uikit.viewmodels.common.utils.requestLocationAccess
import com.sygic.maps.uikit.views.common.components.FragmentComponent
import com.sygic.maps.uikit.views.common.extensions.asSingleEvent
import com.sygic.maps.uikit.views.common.extensions.getBoolean
import com.sygic.maps.uikit.views.common.extensions.getInt
import com.sygic.maps.uikit.views.common.extensions.getParcelableValue
import com.sygic.maps.uikit.views.common.livedata.SingleLiveEvent
import com.sygic.maps.uikit.views.common.utils.logWarning
import com.sygic.maps.uikit.views.placedetail.PlaceDetailBottomDialogFragment
import com.sygic.maps.uikit.views.placedetail.component.PlaceDetailComponent
import com.sygic.sdk.map.`object`.MapMarker
import com.sygic.sdk.map.`object`.ProxyPlace
import com.sygic.sdk.map.`object`.UiObject
import com.sygic.sdk.map.`object`.ViewObject
import com.sygic.sdk.map.`object`.data.ViewObjectData
import com.sygic.sdk.places.PlacesManager
import kotlin.collections.set

@AutoFactory
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
class BrowseMapFragmentViewModel internal constructor(
    app: Application,
    @Assisted arguments: Bundle?,
    themeManager: ThemeManager,
    regionalManager: RegionalManager,
    private val mapDataModel: ExtendedMapDataModel,
    private val placesManagerClient: PlacesManagerClient,
    private val mapInteractionManager: MapInteractionManager,
    private val locationManager: LocationManager,
    private val permissionsManager: PermissionsManager,
    private val positionManagerClient: PositionManagerClient
) : MapFragmentViewModel(app, arguments, themeManager, regionalManager, positionManagerClient),
    MapInteractionManager.Listener, PlaceDetailBottomDialogFragment.Listener {

    @MapSelectionMode
    var mapSelectionMode: Int = MAP_SELECTION_MODE_DEFAULT_VALUE
    var positionOnMapEnabled: Boolean
        get() = locationManager.positionOnMapEnabled.value!!
        set(value) {
            if (value) {
                requestLocationAccess(permissionsManager, locationManager) {
                    locationManager.positionOnMapEnabled.value = true
                }
            } else {
                locationManager.positionOnMapEnabled.value = false
            }
        }

    val compassEnabled = MutableLiveData<Boolean>(COMPASS_ENABLED_DEFAULT_VALUE)
    val compassHideIfNorthUp = MutableLiveData<Boolean>(COMPASS_HIDE_IF_NORTH_UP_DEFAULT_VALUE)
    val positionLockFabEnabled = MutableLiveData<Boolean>(POSITION_LOCK_FAB_ENABLED_DEFAULT_VALUE)
    val searchEnabled = MutableLiveData<Boolean>(SEARCH_ENABLED_DEFAULT_VALUE)
    val zoomControlsEnabled = MutableLiveData<Boolean>(ZOOM_CONTROLS_ENABLED_DEFAULT_VALUE)
    val navigationButtonEnabled = MutableLiveData<Boolean>(NAVIGATION_BUTTON_ENABLED_DEFAULT_VALUE)

    var onMapClickListener: OnMapClickListener? = null
    var detailsViewFactory: DetailsViewFactory? = null
    private val moduleConnectionProvidersMap: Map<ProviderType, ModuleConnectionProvider?> = mutableMapOf()

    val placeDetailVisibleObservable: LiveData<Boolean> = SingleLiveEvent()
    val placeDetailComponentObservable: LiveData<PlaceDetailComponent> = SingleLiveEvent()
    val placeDetailListenerObservable: LiveData<PlaceDetailBottomDialogFragment.Listener> = SingleLiveEvent()
    val openFragmentObservable: LiveData<FragmentComponent> = SingleLiveEvent()

    private var placeDetailsView: UiObject? = null
    private var selectedMarker: MapMarker? = null

    init {
        with(arguments) {
            detailsViewFactory = getParcelableValue(KEY_DETAILS_VIEW_FACTORY)

            mapSelectionMode = getInt(KEY_MAP_SELECTION_MODE, MAP_SELECTION_MODE_DEFAULT_VALUE)
            positionOnMapEnabled = getBoolean(KEY_POSITION_ON_MAP, POSITION_ON_MAP_ENABLED_DEFAULT_VALUE)
            compassEnabled.value = getBoolean(KEY_COMPASS_ENABLED, COMPASS_ENABLED_DEFAULT_VALUE)
            compassHideIfNorthUp.value = getBoolean(KEY_COMPASS_HIDE_IF_NORTH, COMPASS_HIDE_IF_NORTH_UP_DEFAULT_VALUE)
            positionLockFabEnabled.value = getBoolean(KEY_POSITION_LOCK_FAB, POSITION_LOCK_FAB_ENABLED_DEFAULT_VALUE)
            zoomControlsEnabled.value = getBoolean(KEY_ZOOM_CONTROLS, ZOOM_CONTROLS_ENABLED_DEFAULT_VALUE)
        }
    }

    override fun onCreate(owner: LifecycleOwner) {
        if (owner is OnMapClickListenerWrapper) {
            owner.mapClickListenerProvider.observe(owner, Observer { listener ->
                onMapClickListener = listener
            })
        }
        if (owner is ModuleConnectionProviderWrapper) {
            owner.moduleConnectionProvidersMap.observe(owner, Observer { map ->
                map.forEach { updateModuleConnectionProvidersMap(it.key, it.value) }
            })
        }
        placeDetailListenerObservable.asSingleEvent().value = this
        mapInteractionManager.addOnMapClickListener(this)
    }

    override fun onStart(owner: LifecycleOwner) {
        if (positionOnMapEnabled) {
            positionManagerClient.sdkPositionUpdatingEnabled.value = true
        }
    }

    override fun onMapObjectsRequestStarted() {
        mapDataModel.removeMapMarker(selectedMarker)
    }

    override fun onMapObjectsReceived(viewObjects: List<ViewObject<*>>) {
        if (viewObjects.isEmpty()) {
            return
        }

        // Currently, we take care only of the first ViewObject
        var firstViewObject = viewObjects.first()
        val dataLoadAllowed = onMapClickListener?.onMapClick(firstViewObject) ?: true

        // First, check if the PlaceDetailsView is visible
        placeDetailsView?.let {

            // Always remove the previous one
            mapDataModel.removeMapObject(it)
            placeDetailsView = null

            // Check if we should ask or use our own logic
            if (onMapClickListener != null) {
                // This is useful for switching between the same types without hiding the previous one
                if (!dataLoadAllowed) {
                    return
                }
            } else {
                // Currently, we internally support only MapMarker's
                if (firstViewObject !is MapMarker) {
                    return
                }
            }
        }

        when (mapSelectionMode) {

            MapSelectionMode.NONE -> {
                onMapClickListener?.let { logWarning("The OnMapClickListener is set, but map selection mode is NONE.") }
            }

            MapSelectionMode.MARKERS_ONLY -> {
                // Markers only mode check
                if (firstViewObject !is MapMarker) {
                    return
                }

                // Continue only if the ViewObject data load is allowed
                if (!dataLoadAllowed) {
                    return
                }

                getPlaceDataAndNotifyObservers(firstViewObject)
            }

            MapSelectionMode.FULL -> {
                // Add the OnClickMapMarker only if the click is not at MapMarker
                if (firstViewObject !is MapMarker) {
                    getOnClickMapMarker(firstViewObject)?.let { clickMapMarker ->
                        mapDataModel.addMapMarker(
                            when (firstViewObject) {
                                // To persist ProxyPlace data payload we need to create a copy of the provided MapMarker
                                // and give it the same payload
                                is ProxyPlace -> clickMapMarker.getCopyWithPayload(firstViewObject)
                                else -> clickMapMarker
                            }.also {
                                firstViewObject = it
                                selectedMarker = it
                            })
                    }
                }

                // Continue only if the ViewObject data load is allowed
                if (!dataLoadAllowed) {
                    return
                }

                getPlaceDataAndNotifyObservers(firstViewObject)
            }
        }
    }

    private fun getOnClickMapMarker(viewObject: ViewObject<*>): MapMarker? {
        return if (onMapClickListener != null) {
            onMapClickListener?.getClickMapMarker(viewObject.position.latitude, viewObject.position.longitude)
        } else {
            MapMarker.at(viewObject.position).build()
        }
    }

    private fun getPlaceDataAndNotifyObservers(viewObject: ViewObject<*>) {
        val showDetailsView = onMapClickListener?.showDetailsView() ?: true
        if (showDetailsView && detailsViewFactory == null) {
            placeDetailVisibleObservable.asSingleEvent().value = true
        }

        placesManagerClient.getViewObjectData(viewObject, object : PlacesManagerClient.Callback() {
            override fun onDataLoaded(data: ViewObjectData) {
                onMapClickListener?.onMapDataReceived(data)

                if (showDetailsView) {
                    detailsViewFactory?.let { factory ->
                        placeDetailsView = PlaceDetailsObject.create(data, factory, viewObject).also { view ->
                            mapDataModel.addMapObject(view)
                        }
                    } ?: run {
                        placeDetailComponentObservable.asSingleEvent().value =
                            PlaceDetailComponent(data.toPlaceDetailData(), navigationButtonEnabled.value!!)
                    }
                }
            }
            override fun onPlaceError(code: PlacesManager.ErrorCode) { /* Currently do nothing */ }
        })
    }

    private fun updateModuleConnectionProvidersMap(
        type: ProviderType,
        provider: ModuleConnectionProvider?
    ) {
        (moduleConnectionProvidersMap as MutableMap)[type] = provider
        when (type) {
            ProviderType.SEARCH -> searchEnabled.value = provider?.let { true } ?: false
            ProviderType.NAVIGATION -> navigationButtonEnabled.value = provider?.let { true } ?: false
        }
    }

    fun onSearchFabClick() = moduleConnectionProvidersMap[ProviderType.SEARCH]?.let {
        openFragmentObservable.asSingleEvent().value = FragmentComponent(it.fragment, it.getFragmentTag())
    }

    override fun onNavigationButtonClick() {
        placeDetailVisibleObservable.asSingleEvent().value = false
        moduleConnectionProvidersMap[ProviderType.NAVIGATION]?.let {
            openFragmentObservable.asSingleEvent().value = FragmentComponent(it.fragment, it.getFragmentTag())
        }
    }

    override fun onDismiss() = mapDataModel.removeMapMarker(selectedMarker)

    override fun onStop(owner: LifecycleOwner) {
        positionManagerClient.sdkPositionUpdatingEnabled.value = false
    }

    override fun onDestroy(owner: LifecycleOwner) {
        onMapClickListener = null
        (moduleConnectionProvidersMap as MutableMap).clear()
        mapInteractionManager.removeOnMapClickListener(this)
    }
}