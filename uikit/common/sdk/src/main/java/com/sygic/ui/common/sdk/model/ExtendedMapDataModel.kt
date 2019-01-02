package com.sygic.ui.common.sdk.model

import androidx.annotation.RestrictTo
import com.sygic.sdk.map.data.SimpleMapDataModel
import com.sygic.ui.common.sdk.mapobject.MapMarker

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
class ExtendedMapDataModel : SimpleMapDataModel() {

    private var currentOnClickMapMarker: MapMarker? = null

    fun addOnClickMapMarker(onClickMapMarker: MapMarker) {
        currentOnClickMapMarker = onClickMapMarker
        addMapObject(onClickMapMarker)
    }

    fun removeOnClickMapMarker() {
        currentOnClickMapMarker?.let { removeMapObject(it) }
    }
}