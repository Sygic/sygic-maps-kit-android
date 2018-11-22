package com.sygic.ui.common.sdk.model

import androidx.annotation.RestrictTo
import androidx.lifecycle.MutableLiveData
import com.sygic.sdk.map.data.SimpleMapDataModel
import com.sygic.ui.common.sdk.data.PoiData
import com.sygic.ui.common.sdk.mapobject.OnClickMapMarker

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
class ExtendedMapDataModel : SimpleMapDataModel() {

    private var currentOnClickMapMarker: OnClickMapMarker? = null

    val poiDataObservable: MutableLiveData<PoiData> = MutableLiveData()

    fun addOnClickMapMarker(onClickMapMarker: OnClickMapMarker) {
        currentOnClickMapMarker = onClickMapMarker
        addMapObject(onClickMapMarker)
    }

    fun removeOnClickMapMarker() {
        currentOnClickMapMarker?.let { removeMapObject(it) }
    }

    fun notifyPoiDataChanged(poiData: PoiData) {
        poiDataObservable.value = poiData
    }
}