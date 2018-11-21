package com.sygic.modules.browsemap.viewmodel

import android.content.res.TypedArray
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sygic.modules.browsemap.R
import com.sygic.modules.common.manager.MapInteractionManager
import com.sygic.sdk.map.MapView
import com.sygic.sdk.map.`object`.ViewObject
import com.sygic.ui.common.sdk.mapobject.DefaultMapMarker

class BrowseMapFragmentViewModel(attributesTypedArray: TypedArray?,
                                 private val mapDataModel: MapView.MapDataModel,
                                 private val mapInteractionManager: MapInteractionManager) : ViewModel(), MapInteractionManager.Listener {

    val compassEnabled: MutableLiveData<Boolean> = MutableLiveData()
    val compassHideIfNorthUp: MutableLiveData<Boolean> = MutableLiveData()
    val mapClickEnabled: MutableLiveData<Boolean> = MutableLiveData()
    val positionLockFabEnabled: MutableLiveData<Boolean> = MutableLiveData()
    val zoomControlsEnabled: MutableLiveData<Boolean> = MutableLiveData()

    private var currentMapMarker: DefaultMapMarker? = null

    init {
        attributesTypedArray?.let {
            compassEnabled.value = it.getBoolean(R.styleable.BrowseMapFragment_sygic_compassEnabled, false)
            compassHideIfNorthUp.value = it.getBoolean(R.styleable.BrowseMapFragment_sygic_compassHideIfNorthUp, false)
            mapClickEnabled.value = it.getBoolean(R.styleable.BrowseMapFragment_sygic_mapClickEnabled, false)
            positionLockFabEnabled.value = it.getBoolean(R.styleable.BrowseMapFragment_sygic_positionLockFabEnabled, false)
            zoomControlsEnabled.value = it.getBoolean(R.styleable.BrowseMapFragment_sygic_zoomControlsEnabled, false)
            it.recycle()
        }

        mapClickEnabled.value?.let { enabled ->
            if (enabled) {
                mapInteractionManager.addOnMapClickListener(this)
            }
        }
    }

    override fun onMapObjectRequestStarted() {
        currentMapMarker?.let { mapDataModel.removeMapObject(it) }
    }

    override fun onMapObjectReceived(firstViewObject: ViewObject) {
        currentMapMarker = DefaultMapMarker(firstViewObject).apply { mapDataModel.addMapObject(this) }
    }

    override fun onCleared() {
        mapInteractionManager.removeOnMapClickListener(this)
        super.onCleared()
    }

    internal class ViewModelFactory(private val attributesTypedArray: TypedArray?,
                                    private val mapDataModel: MapView.MapDataModel,
                                    private val mapInteractionManager: MapInteractionManager) :
        ViewModelProvider.NewInstanceFactory() {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return BrowseMapFragmentViewModel(attributesTypedArray, mapDataModel, mapInteractionManager) as T
        }
    }
}