package com.sygic.modules.browsemap.viewmodel

import android.content.res.TypedArray
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sygic.modules.browsemap.R
import com.sygic.modules.common.mapinteraction.MapInteractionMode
import com.sygic.modules.common.mapinteraction.manager.MapInteractionManager
import com.sygic.tools.annotations.Assisted
import com.sygic.tools.annotations.AutoFactory
import com.sygic.modules.common.poi.manager.PoiDataManager
import com.sygic.sdk.map.`object`.ViewObject
import com.sygic.ui.common.sdk.data.PoiData
import com.sygic.ui.common.sdk.mapobject.MapMarker
import com.sygic.ui.common.sdk.model.ExtendedMapDataModel

@AutoFactory
class BrowseMapFragmentViewModel internal constructor(
    @Assisted attributesTypedArray: TypedArray?,
    private val poiDataManager: PoiDataManager,
    private val extendedMapDataModel: ExtendedMapDataModel,
    private val mapInteractionManager: MapInteractionManager
) : ViewModel(), MapInteractionManager.Listener {

    @MapInteractionMode
    val mapInteractionMode: MutableLiveData<Int> = MutableLiveData()
    val compassEnabled: MutableLiveData<Boolean> = MutableLiveData()
    val compassHideIfNorthUp: MutableLiveData<Boolean> = MutableLiveData()
    val positionLockFabEnabled: MutableLiveData<Boolean> = MutableLiveData()
    val zoomControlsEnabled: MutableLiveData<Boolean> = MutableLiveData()

    init {
        attributesTypedArray?.let {
            compassEnabled.value = it.getBoolean(R.styleable.BrowseMapFragment_sygic_compassEnabled, false)
            compassHideIfNorthUp.value = it.getBoolean(R.styleable.BrowseMapFragment_sygic_compassHideIfNorthUp, false)
            mapInteractionMode.value =
                    it.getInt(R.styleable.BrowseMapFragment_sygic_mapInteractionMode, MapInteractionMode.NONE)
            positionLockFabEnabled.value =
                    it.getBoolean(R.styleable.BrowseMapFragment_sygic_positionLockFabEnabled, false)
            zoomControlsEnabled.value = it.getBoolean(R.styleable.BrowseMapFragment_sygic_zoomControlsEnabled, false)
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

                    extendedMapDataModel.notifyPoiDataChanged(PoiData())
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
                extendedMapDataModel.notifyPoiDataChanged(poiData)
            }
        })
    }

    override fun onCleared() {
        super.onCleared()
        mapInteractionManager.removeOnMapClickListener(this)
    }
}