package com.sygic.ui.common.sdk.model

import android.util.Log
import androidx.annotation.RestrictTo
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sygic.sdk.map.data.SimpleMapDataModel
import com.sygic.ui.common.sdk.data.PoiData
import com.sygic.ui.common.sdk.mapobject.MapMarker

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
class ExtendedMapDataModel : SimpleMapDataModel() {

    private var currentOnClickMapMarker: MapMarker? = null

    val poiDataObservable: LiveData<PoiData> = MutableLiveData()

    init {
        Log.d("Tomas", "ExtendedMapDataModel() called")
    }

    fun addOnClickMapMarker(onClickMapMarker: MapMarker) {
        currentOnClickMapMarker = onClickMapMarker
        addMapObject(onClickMapMarker)
    }

    fun removeOnClickMapMarker() {
        currentOnClickMapMarker?.let { removeMapObject(it) }
    }

    fun notifyPoiDataChanged(poiData: PoiData) {
        (poiDataObservable as MutableLiveData<PoiData>).value = poiData
    }
}