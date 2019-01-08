package com.sygic.modules.browsemap.viewmodel

import android.content.res.TypedArray
import android.util.Log
import androidx.lifecycle.*
import com.sygic.modules.browsemap.R
import com.sygic.modules.common.mapinteraction.MapSelectionMode
import com.sygic.modules.common.mapinteraction.manager.MapInteractionManager
import com.sygic.tools.annotations.Assisted
import com.sygic.tools.annotations.AutoFactory
import com.sygic.modules.common.poi.manager.PoiDataManager
import com.sygic.ui.common.livedata.SingleLiveEvent
import com.sygic.sdk.map.`object`.ViewObject
import com.sygic.ui.common.extensions.asSingleEvent
import com.sygic.ui.common.listeners.DialogFragmentListener
import com.sygic.ui.common.sdk.data.PoiData
import com.sygic.ui.common.sdk.listener.OnMapClickListener
import com.sygic.ui.common.sdk.mapobject.MapMarker
import com.sygic.ui.common.sdk.model.ExtendedMapDataModel
import java.util.LinkedHashSet

@AutoFactory
class BrowseMapFragmentViewModel internal constructor(
    @Assisted attributesTypedArray: TypedArray?,
    private val poiDataManager: PoiDataManager,
    private val extendedMapDataModel: ExtendedMapDataModel,
    private val mapInteractionManager: MapInteractionManager
) : ViewModel(), MapInteractionManager.Listener, DefaultLifecycleObserver {

    @MapSelectionMode
    val mapSelectionMode: MutableLiveData<Int> = MutableLiveData()
    val compassEnabled: MutableLiveData<Boolean> = MutableLiveData()
    val compassHideIfNorthUp: MutableLiveData<Boolean> = MutableLiveData()
    val positionLockFabEnabled: MutableLiveData<Boolean> = MutableLiveData()
    val zoomControlsEnabled: MutableLiveData<Boolean> = MutableLiveData()

    val poiDataObservable: LiveData<PoiData> = SingleLiveEvent()

    val dialogFragmentListener: DialogFragmentListener = object : DialogFragmentListener {
        override fun onDismiss() {
            extendedMapDataModel.removeOnClickMapMarker()
        }
    }

    private val onMapClickListeners = LinkedHashSet<OnMapClickListener>()

    init {
        attributesTypedArray?.let {
            compassEnabled.value = it.getBoolean(R.styleable.BrowseMapFragment_sygic_compass_enabled, false)
            compassHideIfNorthUp.value = it.getBoolean(R.styleable.BrowseMapFragment_sygic_compass_hideIfNorthUp, false)
            mapSelectionMode.value =
                    it.getInt(R.styleable.BrowseMapFragment_sygic_map_selectionMode, MapSelectionMode.MARKERS_ONLY)
            positionLockFabEnabled.value =
                    it.getBoolean(R.styleable.BrowseMapFragment_sygic_positionLockFab_enabled, false)
            zoomControlsEnabled.value = it.getBoolean(R.styleable.BrowseMapFragment_sygic_zoomControls_enabled, false)
            it.recycle()
        }

        mapSelectionMode.observeForever { mode ->
            when (mode) {
                MapSelectionMode.FULL, MapSelectionMode.MARKERS_ONLY ->
                    mapInteractionManager.addOnMapClickListener(this)
                MapSelectionMode.NONE ->
                    mapInteractionManager.removeOnMapClickListener(this)
            }
        }
    }

    override fun onMapObjectsRequestStarted() {
        extendedMapDataModel.removeOnClickMapMarker()
    }

    override fun onMapObjectsReceived(viewObjects: List<ViewObject>) {
        mapSelectionMode.value?.let { mode ->
            when (mode) {
                MapSelectionMode.NONE -> {
                    logWarning("NONE")
                }
                MapSelectionMode.MARKERS_ONLY -> {
                    val firstViewObject = viewObjects.first()
                    if (firstViewObject !is MapMarker) {
                        logWarning("MARKERS_ONLY")
                        return
                    }

                    getPoiDataAndNotifyObservers(firstViewObject)
                }
                MapSelectionMode.FULL -> {
                    val firstViewObject = viewObjects.first()
                    if (firstViewObject !is MapMarker && !isOnMapClickListenerSet()) {
                        extendedMapDataModel.addOnClickMapMarker(MapMarker(firstViewObject))
                    }

                    getPoiDataAndNotifyObservers(firstViewObject)
                }
            }
        }
    }

    private fun logWarning(mode: String) {
        if (isOnMapClickListenerSet()) {
            Log.w("OnMapClickListener", "The listener is set, but map selection mode is $mode.")
        }
    }

    private fun getPoiDataAndNotifyObservers(firstViewObject: ViewObject) {
        poiDataManager.getPoiData(firstViewObject, object : PoiDataManager.Callback() {
            override fun onDataLoaded(poiData: PoiData) {
                if (isOnMapClickListenerSet()) {
                    onMapClickListeners.forEach { it.onMapClick(poiData) }
                    return
                }

                poiDataObservable.asSingleEvent().value = poiData
            }
        })
    }

    private fun isOnMapClickListenerSet() = !onMapClickListeners.isEmpty()

    fun addOnMapClickListener(onMapClickListener: OnMapClickListener) {
        onMapClickListeners.add(onMapClickListener)
    }

    fun removeOnMapClickListener(onMapClickListener: OnMapClickListener) {
        onMapClickListeners.remove(onMapClickListener)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        onMapClickListeners.clear()
    }

    override fun onCleared() {
        super.onCleared()
        mapInteractionManager.removeOnMapClickListener(this)
    }
}