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
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import com.sygic.maps.module.browsemap.*
import com.sygic.maps.module.browsemap.detail.PoiDetailsObject
import com.sygic.maps.module.common.component.*
import com.sygic.maps.module.common.detail.DetailsViewFactory
import com.sygic.maps.module.common.extensions.onMapClick
import com.sygic.maps.module.common.listener.OnMapClickListener
import com.sygic.maps.module.common.listener.OnMapClickListenerWrapper
import com.sygic.maps.module.common.mapinteraction.MapSelectionMode
import com.sygic.maps.module.common.mapinteraction.manager.MapInteractionManager
import com.sygic.maps.module.common.poi.manager.PoiDataManager
import com.sygic.maps.module.common.provider.ModuleConnectionProvider
import com.sygic.maps.module.common.provider.ModuleConnectionProviderWrapper
import com.sygic.maps.module.common.theme.ThemeManager
import com.sygic.maps.module.common.theme.ThemeSupportedViewModel
import com.sygic.maps.tools.annotations.Assisted
import com.sygic.maps.tools.annotations.AutoFactory
import com.sygic.maps.uikit.viewmodels.common.extensions.getCopyWithPayload
import com.sygic.maps.uikit.viewmodels.common.extensions.toPoiDetailData
import com.sygic.maps.uikit.viewmodels.common.location.LocationManager
import com.sygic.maps.uikit.viewmodels.common.permission.PermissionsManager
import com.sygic.maps.uikit.viewmodels.common.sdk.model.ExtendedMapDataModel
import com.sygic.maps.uikit.viewmodels.common.utils.requestLocationAccess
import com.sygic.maps.uikit.views.common.extensions.asSingleEvent
import com.sygic.maps.uikit.views.common.extensions.getBoolean
import com.sygic.maps.uikit.views.common.extensions.getInt
import com.sygic.maps.uikit.views.common.livedata.SingleLiveEvent
import com.sygic.maps.uikit.views.common.utils.logWarning
import com.sygic.maps.uikit.views.poidetail.data.PoiDetailData
import com.sygic.maps.uikit.views.poidetail.listener.DialogFragmentListener
import com.sygic.sdk.map.`object`.MapMarker
import com.sygic.sdk.map.`object`.ProxyPoi
import com.sygic.sdk.map.`object`.UiObject
import com.sygic.sdk.map.`object`.ViewObject
import com.sygic.sdk.map.`object`.data.ViewObjectData

@AutoFactory
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
class BrowseMapFragmentViewModel internal constructor(
    app: Application,
    @Assisted arguments: Bundle?,
    private val mapDataModel: ExtendedMapDataModel,
    private val poiDataManager: PoiDataManager,
    private val mapInteractionManager: MapInteractionManager,
    private val locationManager: LocationManager,
    private val permissionsManager: PermissionsManager,
    private val themeManager: ThemeManager
) : AndroidViewModel(app), ThemeSupportedViewModel, DefaultLifecycleObserver, MapInteractionManager.Listener {

    @MapSelectionMode
    var mapSelectionMode: Int = MAP_SELECTION_MODE_DEFAULT_VALUE
    var positionOnMapEnabled: Boolean
        get() = locationManager.positionOnMapEnabled
        set(value) {
            if (value) {
                requestLocationAccess(permissionsManager, locationManager) {
                    locationManager.positionOnMapEnabled = true
                }
            } else {
                locationManager.positionOnMapEnabled = false
            }
        }

    val compassEnabled: MutableLiveData<Boolean> = MutableLiveData()
    val compassHideIfNorthUp: MutableLiveData<Boolean> = MutableLiveData()
    val positionLockFabEnabled: MutableLiveData<Boolean> = MutableLiveData()
    val searchEnabled: MutableLiveData<Boolean> = MutableLiveData()
    val zoomControlsEnabled: MutableLiveData<Boolean> = MutableLiveData()

    var onMapClickListener: OnMapClickListener? = null
    var detailsViewFactory: DetailsViewFactory? = null
    var searchConnectionProvider: ModuleConnectionProvider? = null
        set(value) {
            field = value
            searchEnabled.value = value?.let { true } ?: false
        }

    val poiDetailObservable: LiveData<Any> = SingleLiveEvent()
    val poiDetailDataObservable: LiveData<PoiDetailData> = SingleLiveEvent()
    val poiDetailListenerObservable: LiveData<DialogFragmentListener> = SingleLiveEvent()
    val openFragmentObservable: LiveData<Fragment> = SingleLiveEvent()

    val dialogFragmentListener: DialogFragmentListener = object : DialogFragmentListener {
        override fun onDismiss() {
            mapDataModel.removeOnClickMapMarker()
        }
    }

    private var poiDetailsView: UiObject? = null

    init {
        detailsViewFactory = arguments?.getParcelable(KEY_DETAILS_VIEW_FACTORY)

        mapSelectionMode = arguments.getInt(KEY_MAP_SELECTION_MODE, MAP_SELECTION_MODE_DEFAULT_VALUE)
        positionOnMapEnabled = arguments.getBoolean(KEY_POSITION_ON_MAP, POSITION_ON_MAP_ENABLED_DEFAULT_VALUE)
        compassEnabled.value = arguments.getBoolean(KEY_COMPASS_ENABLED, COMPASS_ENABLED_DEFAULT_VALUE)
        compassHideIfNorthUp.value = arguments.getBoolean(KEY_COMPASS_HIDE_IF_NORTH, COMPASS_HIDE_IF_NORTH_UP_DEFAULT_VALUE)
        positionLockFabEnabled.value = arguments.getBoolean(KEY_POSITION_LOCK_FAB, POSITION_LOCK_FAB_ENABLED_DEFAULT_VALUE)
        zoomControlsEnabled.value = arguments.getBoolean(KEY_ZOOM_CONTROLS, ZOOM_CONTROLS_ENABLED_DEFAULT_VALUE)

        arguments?.getString(ThemeManager.SkinLayer.DayNight.toString())?.let { skin -> themeManager.setSkinAtLayer(ThemeManager.SkinLayer.DayNight, skin) }
        arguments?.getString(ThemeManager.SkinLayer.Vehicle.toString())?.let { skin -> themeManager.setSkinAtLayer(ThemeManager.SkinLayer.Vehicle, skin) }

        mapInteractionManager.addOnMapClickListener(this)
    }

    override fun onCreate(owner: LifecycleOwner) {
        if (owner is OnMapClickListenerWrapper) {
            owner.mapClickListenerProvider.observe(owner, Observer { listener ->
                onMapClickListener = listener
            })
        }
        if (owner is ModuleConnectionProviderWrapper) {
            owner.moduleConnectionProvider.observe(owner, Observer { provider ->
                searchConnectionProvider = provider
            })

        }
        poiDetailListenerObservable.asSingleEvent().value = dialogFragmentListener
    }

    override fun onStart(owner: LifecycleOwner) {
        if (positionOnMapEnabled) {
            locationManager.setSdkPositionUpdatingEnabled(true)
        }
    }

    override fun onMapObjectsRequestStarted() {
        mapDataModel.removeOnClickMapMarker()
    }

    override fun onMapObjectsReceived(viewObjects: List<ViewObject<*>>) {
        if (viewObjects.isEmpty()) {
            return
        }

        // Currently, we take care only of the first ViewObject
        var firstViewObject = viewObjects.first()
        val dataLoadAllowed = onMapClickListener?.onMapClick(firstViewObject) ?: true

        // First, check if the PoiDetailsView is visible
        poiDetailsView?.let {

            // Always remove the previous one
            mapDataModel.removeMapObject(it)
            poiDetailsView = null

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

                getPoiDataAndNotifyObservers(firstViewObject)
            }

            MapSelectionMode.FULL -> {
                // Add the OnClickMapMarker only if the click is not at MapMarker
                if (firstViewObject !is MapMarker) {
                    getOnClickMapMarker(firstViewObject)?.let { clickMapMarker ->
                        mapDataModel.addOnClickMapMarker(
                            when (firstViewObject) {
                                // To persist ProxyPoi data payload we need to create a copy of the provided MapMarker
                                // and give it the same payload
                                is ProxyPoi -> clickMapMarker.getCopyWithPayload(firstViewObject)
                                else -> clickMapMarker
                            }.also { firstViewObject = it })
                    }
                }

                // Continue only if the ViewObject data load is allowed
                if (!dataLoadAllowed) {
                    return
                }

                getPoiDataAndNotifyObservers(firstViewObject)
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

    private fun getPoiDataAndNotifyObservers(viewObject: ViewObject<*>) {
        val showDetailsView = onMapClickListener?.showDetailsView() ?: true
        if (showDetailsView && detailsViewFactory == null) {
            poiDetailObservable.asSingleEvent().call()
        }

        poiDataManager.getViewObjectData(viewObject, object : PoiDataManager.Callback() {
            override fun onDataLoaded(data: ViewObjectData) {
                onMapClickListener?.onMapDataReceived(data)

                if (showDetailsView) {
                    detailsViewFactory?.let { factory ->
                        poiDetailsView = PoiDetailsObject.create(data, factory, viewObject).also { view ->
                            mapDataModel.addMapObject(view)
                        }
                    } ?: run {
                        poiDetailDataObservable.asSingleEvent().value = data.toPoiDetailData()
                    }
                }
            }
        })
    }

    override fun setSkinAtLayer(layer: ThemeManager.SkinLayer, skin: String) = themeManager.setSkinAtLayer(layer, skin)

    fun onSearchFabClick() =
        searchConnectionProvider?.let { openFragmentObservable.asSingleEvent().value = it.fragment }

    override fun onStop(owner: LifecycleOwner) {
        locationManager.setSdkPositionUpdatingEnabled(false)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        onMapClickListener = null
        searchConnectionProvider = null
    }

    override fun onCleared() {
        super.onCleared()
        mapInteractionManager.removeOnMapClickListener(this)
    }
}