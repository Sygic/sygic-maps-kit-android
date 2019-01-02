package com.sygic.modules.browsemap.viewmodel

import android.content.res.TypedArray
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sygic.modules.browsemap.R
import com.sygic.modules.common.mapinteraction.MapInteractionMode
import com.sygic.modules.common.mapinteraction.manager.MapInteractionManager
import com.sygic.tools.annotations.Assisted
import com.sygic.tools.annotations.AutoFactory
import com.sygic.modules.common.poi.manager.PoiDataManager
import com.sygic.ui.common.livedata.SingleLiveEvent
import com.sygic.sdk.map.`object`.ViewObject
import com.sygic.ui.common.extensions.asSingleEvent
import com.sygic.ui.common.sdk.data.PoiData
import com.sygic.ui.common.sdk.mapobject.MapMarker
import com.sygic.ui.common.sdk.model.ExtendedMapDataModel
import com.sygic.ui.view.poidetail.PoiDetailBottomDialogFragment

@AutoFactory
class BrowseMapFragmentViewModel internal constructor(
    @Assisted attributesTypedArray: TypedArray?,
    private val poiDataManager: PoiDataManager,
    private val extendedMapDataModel: ExtendedMapDataModel,
    private val mapInteractionManager: MapInteractionManager
) : ViewModel(), MapInteractionManager.Listener, PoiDetailBottomDialogFragment.Listener {

    @MapInteractionMode
    val mapInteractionMode: MutableLiveData<Int> = MutableLiveData()
    val compassEnabled: MutableLiveData<Boolean> = MutableLiveData()
    val compassHideIfNorthUp: MutableLiveData<Boolean> = MutableLiveData()
    val positionLockFabEnabled: MutableLiveData<Boolean> = MutableLiveData()
    val zoomControlsEnabled: MutableLiveData<Boolean> = MutableLiveData()

    val poiDataObservable: LiveData<PoiData> = SingleLiveEvent()

    init {
        attributesTypedArray?.let {
            compassEnabled.value = it.getBoolean(R.styleable.BrowseMapFragment_sygic_compass_enabled, false)
            compassHideIfNorthUp.value = it.getBoolean(R.styleable.BrowseMapFragment_sygic_compass_hideIfNorthUp, false)
            mapInteractionMode.value =
                    it.getInt(R.styleable.BrowseMapFragment_sygic_map_interactionMode, MapInteractionMode.MARKERS_ONLY)
            positionLockFabEnabled.value =
                    it.getBoolean(R.styleable.BrowseMapFragment_sygic_positionLockFab_enabled, false)
            zoomControlsEnabled.value = it.getBoolean(R.styleable.BrowseMapFragment_sygic_zoomControls_enabled, false)
            it.recycle()
        }

        mapInteractionMode.value?.let { mode ->
            when (mode) {
                MapInteractionMode.FULL, MapInteractionMode.MARKERS_ONLY -> mapInteractionManager.addOnMapClickListener(
                    this
                )
            }
        }
    }

    override fun onMapObjectsRequestStarted() {
        extendedMapDataModel.removeOnClickMapMarker()
    }

    override fun onMapObjectsReceived(viewObjects: List<ViewObject>) {
        mapInteractionMode.value?.let { mode ->
            val firstViewObject = viewObjects.first()
            when (mode) {
                MapInteractionMode.NONE -> return
                MapInteractionMode.MARKERS_ONLY -> {
                    if (firstViewObject is MapMarker) {
                        getPoiData(firstViewObject)
                        return
                    }

                    notifyPoiDataChanged(PoiData.EMPTY)
                }
                MapInteractionMode.FULL -> {
                    getPoiData(firstViewObject)
                    if (firstViewObject is MapMarker) {
                        return
                    }

                    extendedMapDataModel.addOnClickMapMarker(MapMarker(firstViewObject))
                }
            }
        }
    }

    private fun getPoiData(viewObject: ViewObject) {
        poiDataManager.getPoiData(viewObject, object : PoiDataManager.Callback() {
            override fun onDataLoaded(poiData: PoiData) {
                notifyPoiDataChanged(poiData)
            }
        })
    }

    private fun notifyPoiDataChanged(poiData: PoiData) {
        poiDataObservable.asSingleEvent().value = poiData
        if (poiData.isEmpty()) extendedMapDataModel.removeOnClickMapMarker()
    }

    override fun onPoiDetailBottomDialogDismiss() {
        extendedMapDataModel.removeOnClickMapMarker()
    }

    override fun onCleared() {
        super.onCleared()
        mapInteractionManager.removeOnMapClickListener(this)
    }
}