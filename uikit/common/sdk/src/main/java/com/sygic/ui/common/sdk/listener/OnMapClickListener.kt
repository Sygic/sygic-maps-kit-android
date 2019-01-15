package com.sygic.ui.common.sdk.listener

import com.sygic.ui.common.sdk.data.PoiData

@FunctionalInterface
interface OnMapClickListener {
    fun onMapClick(poiData: PoiData) //ToDO: MS-4711 Custom payload
}