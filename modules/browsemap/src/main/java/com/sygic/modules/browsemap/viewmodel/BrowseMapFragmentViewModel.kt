package com.sygic.modules.browsemap.viewmodel

import android.content.res.TypedArray
import android.view.MotionEvent
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sygic.modules.browsemap.R
import com.sygic.sdk.map.MapView
import com.sygic.sdk.map.mapgesturesdetector.listener.MapGestureAdapter
import com.sygic.ui.common.sdk.mapobject.DefaultMapMarker

class BrowseMapFragmentViewModel(attributesTypedArray: TypedArray?) : ViewModel() {

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
            mapClickEnabled.value = it.getBoolean(R.styleable.BrowseMapFragment_sygic_mapClickEnabled, true)
            positionLockFabEnabled.value = it.getBoolean(R.styleable.BrowseMapFragment_sygic_positionLockFabEnabled, false)
            zoomControlsEnabled.value = it.getBoolean(R.styleable.BrowseMapFragment_sygic_zoomControlsEnabled, false)
            it.recycle()
        }
    }

    fun onMapReady(mapView: MapView, mapDataModel: MapView.MapDataModel) {
        if (!mapClickEnabled.value!!) {
            return
        }

        mapView.addMapGestureListener(object : MapGestureAdapter() {
            override fun onMapClicked(motionEvent: MotionEvent, isTwoFingers: Boolean): Boolean {
                if (isTwoFingers) {
                    return false
                }

                currentMapMarker?.let { mapDataModel.removeMapObject(it) }
                mapView.requestObjectsAtPoint(motionEvent.x, motionEvent.y) { list, x, y, id ->
                    val firstViewObject = list.first()
                    currentMapMarker = DefaultMapMarker(firstViewObject).apply { mapDataModel.addMapObject(this) }
                }
                return super.onMapClicked(motionEvent, false)
            }
        })
    }

    internal class ViewModelFactory(private val attributesTypedArray: TypedArray?) :
        ViewModelProvider.NewInstanceFactory() {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return BrowseMapFragmentViewModel(attributesTypedArray) as T
        }
    }
}