package com.sygic.modules.browsemap.viewmodel

import android.app.Application
import android.util.Log
import androidx.annotation.RestrictTo
import androidx.lifecycle.*
import com.sygic.modules.browsemap.detail.PoiDataDetailsFactory
import com.sygic.modules.browsemap.extensions.resolveAttributes
import com.sygic.modules.common.component.MapFragmentInitComponent
import com.sygic.modules.common.component.MAP_SELECTION_MODE_DEFAULT_VALUE
import com.sygic.modules.common.detail.DetailsViewFactory
import com.sygic.modules.common.mapinteraction.MapSelectionMode
import com.sygic.modules.common.mapinteraction.manager.MapInteractionManager
import com.sygic.modules.common.poi.manager.PoiDataManager
import com.sygic.sdk.map.`object`.MapMarker
import com.sygic.sdk.map.`object`.UiObject
import com.sygic.sdk.map.`object`.ViewObject
import com.sygic.sdk.map.`object`.payload.Payload
import com.sygic.tools.annotations.Assisted
import com.sygic.tools.annotations.AutoFactory
import com.sygic.ui.common.extensions.asSingleEvent
import com.sygic.ui.common.listeners.DialogFragmentListener
import com.sygic.ui.common.livedata.SingleLiveEvent
import com.sygic.ui.common.sdk.listener.OnMapClickListener
import com.sygic.ui.common.sdk.location.LocationManager
import com.sygic.ui.common.sdk.model.ExtendedMapDataModel
import com.sygic.ui.common.sdk.permission.PermissionsManager
import com.sygic.ui.common.sdk.utils.requestLocationAccess

@AutoFactory
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
class BrowseMapFragmentViewModel internal constructor(
    app: Application,
    @Assisted initComponent: MapFragmentInitComponent,
    private val mapDataModel: ExtendedMapDataModel,
    private val poiDataManager: PoiDataManager,
    private val mapInteractionManager: MapInteractionManager,
    private val locationManager: LocationManager,
    private val permissionsManager: PermissionsManager
) : AndroidViewModel(app), MapInteractionManager.Listener, DefaultLifecycleObserver {

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
    val zoomControlsEnabled: MutableLiveData<Boolean> = MutableLiveData()

    var onMapClickListener: OnMapClickListener? = null
    var detailsViewFactory: DetailsViewFactory? = null

    val dataPayloadObservable: LiveData<Payload> = SingleLiveEvent()

    val dialogFragmentListener: DialogFragmentListener = object : DialogFragmentListener {
        override fun onDismiss() {
            mapDataModel.removeOnClickMapMarker()
        }
    }

    private var poiDetailsView: UiObject? = null

    init {
        initComponent.resolveAttributes(app)
        mapSelectionMode = initComponent.mapSelectionMode
        positionOnMapEnabled = initComponent.positionOnMapEnabled
        compassEnabled.value = initComponent.compassEnabled
        compassHideIfNorthUp.value = initComponent.compassHideIfNorthUp
        positionLockFabEnabled.value = initComponent.positionLockFabEnabled
        zoomControlsEnabled.value = initComponent.zoomControlsEnabled
        onMapClickListener = initComponent.onMapClickListener
        detailsViewFactory = initComponent.detailsViewFactory
        initComponent.recycle()

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
                logWarning("NONE")
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

    private fun logWarning(mode: String) {
        onMapClickListener?.let { Log.w("OnMapClickListener", "The listener is set, but map selection mode is $mode.") }
    }

    private fun getPoiDataAndNotifyObservers(viewObject: ViewObject) {
        poiDataManager.getPayloadData(viewObject, object : PoiDataManager.Callback() {
            override fun onDataLoaded(data: Payload) {
                onMapClickListener?.let {
                    if (it.onMapClick(data)) {
                        return
                    }
                }

                detailsViewFactory?.let { factory ->
                    poiDetailsView = object : UiObject(data.position, PoiDataDetailsFactory(factory, data)) {
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
                    dataPayloadObservable.asSingleEvent().value = data
                }
            }
        })
    }

    override fun onStop(owner: LifecycleOwner) {
        locationManager.setSdkPositionUpdatingEnabled(false)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        onMapClickListener = null
        detailsViewFactory = null
    }

    override fun onCleared() {
        super.onCleared()
        mapInteractionManager.removeOnMapClickListener(this)
    }
}
