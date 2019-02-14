package com.sygic.ui.common.sdk.model

import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.sygic.sdk.map.data.SimpleMapDataModel
import com.sygic.ui.common.sdk.mapobject.MapMarker

object ExtendedMapDataModel : SimpleMapDataModel(), DefaultLifecycleObserver {

    sealed class SkinLayer(val position: Int) {
        object DayNight : SkinLayer(0)
        object Vehicle : SkinLayer(1)
    }

    private var currentOnClickMapMarker: MapMarker? = null

    fun addOnClickMapMarker(onClickMapMarker: MapMarker) {
        currentOnClickMapMarker = onClickMapMarker
        addMapObject(onClickMapMarker)
    }

    fun removeOnClickMapMarker() {
        currentOnClickMapMarker?.let { removeMapObject(it) }
    }

    fun setSkinAtLayer(skinLayer: SkinLayer, desiredSkin: String) {
        val skins: MutableList<String> = ArrayList(skin)
        skins[skinLayer.position] = desiredSkin
        skin = skins
    }

    override fun onDestroy(owner: LifecycleOwner) {
        if (owner is Fragment) owner.activity?.run { if (isFinishing) clear() }
    }
}