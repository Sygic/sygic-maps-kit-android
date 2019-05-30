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
import androidx.annotation.RestrictTo
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import com.sygic.maps.module.browsemap.detail.PoiDataDetailsFactory
import com.sygic.maps.module.browsemap.extensions.resolveAttributes
import com.sygic.maps.module.common.component.MapFragmentInitComponent
import com.sygic.maps.module.common.detail.DetailsViewFactory
import com.sygic.maps.module.common.mapinteraction.MapSelectionMode
import com.sygic.maps.module.common.mapinteraction.manager.MapInteractionManager
import com.sygic.maps.module.common.poi.manager.PoiDataManager
import com.sygic.maps.module.common.theme.ThemeManager
import com.sygic.maps.module.common.theme.ThemeSupportedViewModel
import com.sygic.sdk.map.`object`.UiObject
import com.sygic.sdk.map.`object`.ViewObject
import com.sygic.maps.uikit.views.common.extensions.asSingleEvent
import com.sygic.maps.uikit.views.poidetail.listener.DialogFragmentListener
import com.sygic.maps.uikit.views.common.livedata.SingleLiveEvent
import com.sygic.maps.module.common.listener.OnMapClickListener
import com.sygic.maps.module.common.provider.ModuleConnectionProvider
import com.sygic.maps.tools.annotations.Assisted
import com.sygic.maps.tools.annotations.AutoFactory
import com.sygic.maps.uikit.viewmodels.common.data.PoiData
import com.sygic.maps.uikit.viewmodels.common.extensions.getFormattedLocation
import com.sygic.maps.uikit.viewmodels.common.location.LocationManager
import com.sygic.maps.uikit.viewmodels.common.sdk.mapobject.MapMarker
import com.sygic.maps.uikit.viewmodels.common.sdk.model.ExtendedMapDataModel
import com.sygic.maps.uikit.viewmodels.common.permission.PermissionsManager
import com.sygic.maps.uikit.viewmodels.common.utils.requestLocationAccess
import com.sygic.maps.uikit.views.common.utils.logWarning
import com.sygic.maps.uikit.views.poidetail.data.PoiDetailData

@AutoFactory
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
class BrowseMapFragmentViewModel internal constructor(
    app: Application,
    @Assisted initComponent: MapFragmentInitComponent,
    private val mapDataModel: ExtendedMapDataModel,
    private val poiDataManager: PoiDataManager,
    private val mapInteractionManager: MapInteractionManager,
    private val locationManager: LocationManager,
    private val permissionsManager: PermissionsManager,
    private val themeManager: ThemeManager
) : AndroidViewModel(app), ThemeSupportedViewModel, DefaultLifecycleObserver, MapInteractionManager.Listener {

    @MapSelectionMode
    var mapSelectionMode: Int
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

    val poiDetailDataObservable: LiveData<PoiDetailData> = SingleLiveEvent()
    val openFragmentObservable: LiveData<Fragment> = SingleLiveEvent()

    val dialogFragmentListener: DialogFragmentListener = object : DialogFragmentListener {
        override fun onDismiss() {
            mapDataModel.removeOnClickMapMarker()
        }
    }

    private var poiDetailsView: UiObject? = null

    init {
        with(initComponent) {
            resolveAttributes(app)
            this@BrowseMapFragmentViewModel.mapSelectionMode = mapSelectionMode
            this@BrowseMapFragmentViewModel.positionOnMapEnabled = positionOnMapEnabled
            this@BrowseMapFragmentViewModel.compassEnabled.value = compassEnabled
            this@BrowseMapFragmentViewModel.compassHideIfNorthUp.value = compassHideIfNorthUp
            this@BrowseMapFragmentViewModel.positionLockFabEnabled.value = positionLockFabEnabled
            this@BrowseMapFragmentViewModel.zoomControlsEnabled.value = zoomControlsEnabled
            this@BrowseMapFragmentViewModel.onMapClickListener = onMapClickListener
            this@BrowseMapFragmentViewModel.detailsViewFactory = detailsViewFactory
            this@BrowseMapFragmentViewModel.searchConnectionProvider = searchConnectionProvider
            skins.forEach { entry -> themeManager.setSkinAtLayer(entry.key, entry.value) }
            recycle()
        }

        mapInteractionManager.addOnMapClickListener(this)
    }

    override fun onStart(owner: LifecycleOwner) {
        if (positionOnMapEnabled) {
            locationManager.setSdkPositionUpdatingEnabled(true)
        }
    }

    override fun onMapObjectsRequestStarted() {
        mapDataModel.removeOnClickMapMarker()
    }

    override fun onMapObjectsReceived(viewObjects: List<ViewObject>) {
        if (viewObjects.isEmpty()) {
            return
        }

        var firstViewObject = viewObjects.first()
        poiDetailsView?.let {
            mapDataModel.removeMapObject(it)
            poiDetailsView = null
            if (firstViewObject !is MapMarker) {
                return
            }
        }

        when (mapSelectionMode) {
            MapSelectionMode.NONE -> {
                onMapClickListener?.let { logWarning("The OnMapClickListener is set, but map selection mode is NONE.") }
            }
            MapSelectionMode.MARKERS_ONLY -> {
                if (firstViewObject !is MapMarker) {
                    return
                }

                getPoiDataAndNotifyObservers(firstViewObject)
            }
            MapSelectionMode.FULL -> {
                if (firstViewObject !is MapMarker && onMapClickListener == null) {
                    firstViewObject = MapMarker(firstViewObject)
                    mapDataModel.addOnClickMapMarker(firstViewObject)
                }

                getPoiDataAndNotifyObservers(firstViewObject)
            }
        }
    }

    private fun getPoiDataAndNotifyObservers(viewObject: ViewObject) {
        poiDataManager.getPoiData(viewObject, object : PoiDataManager.Callback() {
            override fun onDataLoaded(poiData: PoiData) {
                onMapClickListener?.let {
                    if (it.onMapClick(poiData)) {
                        return
                    }
                }

                detailsViewFactory?.let { factory ->
                    poiDetailsView = object : UiObject(poiData.coordinates, PoiDataDetailsFactory(factory, poiData)) {
                        override fun onMeasured(width: Int, height: Int) {
                            super.onMeasured(width, height)

                            val markerHeight: Int = if (viewObject is MapMarker)
                                viewObject.getBitmap(getApplication())?.height ?: 0 else 0

                            setAnchor(
                                0.5f - (factory.getXOffset() / width),
                                1f + ((markerHeight + factory.getYOffset()) / height)
                            )
                        }
                    }.also {
                        mapDataModel.addMapObject(it)
                    }
                } ?: run {
                    poiDetailDataObservable.asSingleEvent().value = poiData.toPoiDetailData()
                }
            }
        })
    }

    override fun setSkinAtLayer(layer: ThemeManager.SkinLayer, skin: String) = themeManager.setSkinAtLayer(layer, skin)

    fun onSearchFabClick() = searchConnectionProvider?.let { openFragmentObservable.asSingleEvent().value = it.fragment }

    override fun onStop(owner: LifecycleOwner) {
        locationManager.setSdkPositionUpdatingEnabled(false)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        onMapClickListener = null
        detailsViewFactory = null
        searchConnectionProvider = null
    }

    override fun onCleared() {
        super.onCleared()
        mapInteractionManager.removeOnMapClickListener(this)
    }
}

// ToDo: Update me when PR Feature - SDK MapMarker refactor 2/2 is done
private fun PoiData.toPoiDetailData(): PoiDetailData {
    return PoiDetailData(title, description, url, email, phone, coordinates.getFormattedLocation())
}
