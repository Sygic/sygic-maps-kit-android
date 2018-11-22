package com.sygic.ui.common.sdk.model

import androidx.annotation.RestrictTo
import androidx.lifecycle.MutableLiveData
import com.sygic.sdk.map.`object`.MapMarker
import com.sygic.sdk.map.data.SimpleMapDataModel

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
class ExtendedMapDataModel : SimpleMapDataModel() {

    private var currentOnClickMapMarker: MapMarker? = null

    val mapMarkerClickObservable: MutableLiveData<MapMarker> = MutableLiveData()

    fun addOnClickMapMarker(onClickMapMarker: MapMarker) {
        currentOnClickMapMarker = onClickMapMarker
        addMapObject(onClickMapMarker)
        notifyMapMarkerClick(onClickMapMarker)
    }

    fun removeOnClickMapMarker() {
        currentOnClickMapMarker?.let { removeMapObject(it) }
    }

    fun notifyMapMarkerClick(mapMarker: MapMarker) {
        mapMarkerClickObservable.value = mapMarker
    }
}