package com.sygic.maps.module.common.listener

import com.sygic.maps.uikit.viewmodels.common.data.PoiData

/**
 * Interface definition for a callback to be invoked when a click to the map has been made.
 */
@FunctionalInterface
interface OnMapClickListener {

    /**
     * Called when click to the map has been made.
     *
     * @param poiData [PoiData] belonging to the click on the map.

     * @return true if the callback consumed the click, false otherwise (click will be processed by the default behaviour).
     */
    fun onMapClick(poiData: PoiData) : Boolean //ToDO: MS-4711 Custom payload
}