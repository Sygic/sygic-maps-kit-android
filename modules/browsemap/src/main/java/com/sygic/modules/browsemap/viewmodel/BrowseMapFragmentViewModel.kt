package com.sygic.modules.browsemap.viewmodel

import android.app.Application
import android.util.Log
import androidx.annotation.RestrictTo
import androidx.lifecycle.*
import com.sygic.modules.browsemap.detail.PoiDataDetailsFactory
import com.sygic.modules.common.component.MapFragmentComponent
import com.sygic.modules.common.mapinteraction.MapSelectionMode
import com.sygic.modules.common.mapinteraction.manager.MapInteractionManager
import com.sygic.modules.common.poi.manager.PoiDataManager
import com.sygic.sdk.map.`object`.UiObject
import com.sygic.sdk.map.`object`.ViewObject
import com.sygic.tools.annotations.AutoFactory
import com.sygic.ui.common.extensions.asSingleEvent
import com.sygic.ui.common.listeners.DialogFragmentListener
import com.sygic.ui.common.livedata.SingleLiveEvent
import com.sygic.ui.common.sdk.data.PoiData
import com.sygic.ui.common.sdk.location.LocationManager
import com.sygic.ui.common.sdk.mapobject.MapMarker
import com.sygic.ui.common.sdk.permission.PermissionsManager
import com.sygic.ui.common.sdk.utils.requestLocationAccess

@AutoFactory
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
class BrowseMapFragmentViewModel internal constructor(
    app: Application,
    val component: MapFragmentComponent,
    private val poiDataManager: PoiDataManager,
    private val mapInteractionManager: MapInteractionManager,
    private val locationManager: LocationManager,
    private val permissionsManager: PermissionsManager
) : AndroidViewModel(app), MapInteractionManager.Listener, DefaultLifecycleObserver {

    val poiDataObservable: LiveData<PoiData> = SingleLiveEvent()

    val dialogFragmentListener: DialogFragmentListener = object : DialogFragmentListener {
        override fun onDismiss() {
            component.mapDataModel.removeOnClickMapMarker()
        }
    }

    private val positionOnMapEnabledObserver: Observer<Boolean> = Observer {
        if (it) {
            requestLocationAccess(permissionsManager, locationManager) {
                locationManager.positionOnMapEnabled = true
            }
        } else {
            locationManager.positionOnMapEnabled = false
        }
    }

    private var poiDetailsView: UiObject? = null

    init {
        mapInteractionManager.addOnMapClickListener(this)
        component.positionOnMapEnabledObservable.observeForever(positionOnMapEnabledObserver)
    }

    override fun onStart(owner: LifecycleOwner) {
        if (component.positionOnMapEnabled) {
            locationManager.setSdkPositionUpdatingEnabled(true)
        }
    }

    override fun onMapObjectsRequestStarted() {
        component.mapDataModel.removeOnClickMapMarker()
    }

    override fun onMapObjectsReceived(viewObjects: List<ViewObject>) {
        var firstViewObject = viewObjects.first()
        poiDetailsView?.let {
            component.mapDataModel.removeMapObject(it)
            poiDetailsView = null
            if (firstViewObject !is MapMarker) {
                return
            }
        }

        when (component.mapSelectionMode) {
            MapSelectionMode.NONE -> {
                logWarning("NONE")
            }
            MapSelectionMode.MARKERS_ONLY -> {
                if (firstViewObject !is MapMarker) {
                    return
                }

                getPoiDataAndNotifyObservers(firstViewObject)
            }
            MapSelectionMode.FULL -> {
                if (firstViewObject !is MapMarker && component.onMapClickListener == null) {
                    firstViewObject = MapMarker(firstViewObject)
                    component.mapDataModel.addOnClickMapMarker(firstViewObject)
                }

                getPoiDataAndNotifyObservers(firstViewObject)
            }
        }
    }

    private fun logWarning(mode: String) {
        component.onMapClickListener?.let { Log.w("OnMapClickListener", "The listener is set, but map selection mode is $mode.") }
    }

    private fun getPoiDataAndNotifyObservers(viewObject: ViewObject) {
        poiDataManager.getPoiData(viewObject, object : PoiDataManager.Callback() {
            override fun onDataLoaded(poiData: PoiData) {
                component.onMapClickListener?.let {
                    if (it.onMapClick(poiData)) {
                        return
                    }
                }

                component.detailsViewFactory?.let { factory ->
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
                        component.mapDataModel.addMapObject(it)
                    }
                } ?: run {
                    poiDataObservable.asSingleEvent().value = poiData
                }
            }
        })
    }

    override fun onStop(owner: LifecycleOwner) {
        locationManager.setSdkPositionUpdatingEnabled(false)
    }

    override fun onCleared() {
        super.onCleared()
        mapInteractionManager.removeOnMapClickListener(this)
        component.positionOnMapEnabledObservable.removeObserver(positionOnMapEnabledObserver)
    }
}
