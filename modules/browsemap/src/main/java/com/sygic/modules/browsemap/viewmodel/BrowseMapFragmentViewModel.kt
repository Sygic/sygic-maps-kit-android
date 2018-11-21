package com.sygic.modules.browsemap.viewmodel

import android.content.res.TypedArray
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sygic.modules.browsemap.R
import com.sygic.modules.common.mapinteraction.MapInteractionMode
import com.sygic.modules.common.mapinteraction.manager.MapInteractionManager
import com.sygic.modules.common.model.ExtendedMapDataModel
import com.sygic.sdk.map.`object`.ViewObject
import com.sygic.ui.common.sdk.mapobject.DefaultMapMarker

class BrowseMapFragmentViewModel(attributesTypedArray: TypedArray?,
                                 private val extendedMapDataModel: ExtendedMapDataModel,
                                 private val mapInteractionManager: MapInteractionManager
) : ViewModel(), MapInteractionManager.Listener {

    @MapInteractionMode
    val mapInteractionMode: MutableLiveData<Int> = MutableLiveData()
    val compassEnabled: MutableLiveData<Boolean> = MutableLiveData()
    val compassHideIfNorthUp: MutableLiveData<Boolean> = MutableLiveData()
    val positionLockFabEnabled: MutableLiveData<Boolean> = MutableLiveData()
    val zoomControlsEnabled: MutableLiveData<Boolean> = MutableLiveData()

    private var currentMapMarker: DefaultMapMarker? = null

    init {
        attributesTypedArray?.let {
            compassEnabled.value = it.getBoolean(R.styleable.BrowseMapFragment_sygic_compassEnabled, false)
            compassHideIfNorthUp.value = it.getBoolean(R.styleable.BrowseMapFragment_sygic_compassHideIfNorthUp, false)
            mapInteractionMode.value = it.getInt(R.styleable.BrowseMapFragment_sygic_mapInteractionMode, MapInteractionMode.NONE)
            positionLockFabEnabled.value = it.getBoolean(R.styleable.BrowseMapFragment_sygic_positionLockFabEnabled, false)
            zoomControlsEnabled.value = it.getBoolean(R.styleable.BrowseMapFragment_sygic_zoomControlsEnabled, false)
            it.recycle()
        }

        mapInteractionMode.value?.let { mode ->
            when (mode) {
                MapInteractionMode.FULL, MapInteractionMode.MARKERS_ONLY -> mapInteractionManager.addOnMapClickListener(this)
            }
        }
    }

    override fun onMapObjectsRequestStarted() {
        currentMapMarker?.let { extendedMapDataModel.removeMapObject(it) }
    }

    override fun onMapObjectsReceived(viewObjects: List<ViewObject>) { //todo
        currentMapMarker = DefaultMapMarker(viewObjects.first()).apply { extendedMapDataModel.addMapObject(this) }
    }

    override fun onCleared() {
        mapInteractionManager.removeOnMapClickListener(this)
        super.onCleared()
    }

    internal class ViewModelFactory(private val attributesTypedArray: TypedArray?,
                                    private val extendedMapDataModel: ExtendedMapDataModel,
                                    private val mapInteractionManager: MapInteractionManager
    ) :
        ViewModelProvider.NewInstanceFactory() {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return BrowseMapFragmentViewModel(attributesTypedArray, extendedMapDataModel, mapInteractionManager) as T
        }
    }
}