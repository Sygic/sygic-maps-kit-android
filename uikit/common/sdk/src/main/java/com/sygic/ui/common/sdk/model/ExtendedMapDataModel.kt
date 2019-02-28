package com.sygic.ui.common.sdk.model

import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.sygic.sdk.map.data.SimpleMapDataModel
import com.sygic.ui.common.sdk.mapobject.MapMarker

object ExtendedMapDataModel : SimpleMapDataModel(), DefaultLifecycleObserver {

    private var currentOnClickMapMarker: MapMarker? = null

    fun addOnClickMapMarker(onClickMapMarker: MapMarker) {
        currentOnClickMapMarker = onClickMapMarker
        addMapObject(onClickMapMarker)
    }

    fun removeOnClickMapMarker() {
        currentOnClickMapMarker?.let { removeMapObject(it) }
    }

    override fun onDestroy(owner: LifecycleOwner) {
        if (owner is Fragment) owner.activity?.run { if (isFinishing) clear() }
    }
}