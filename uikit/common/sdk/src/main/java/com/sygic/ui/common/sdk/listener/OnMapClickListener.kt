package com.sygic.ui.common.sdk.listener

import com.sygic.sdk.map.`object`.data.ViewObjectData

/**
 * Interface definition for a callback to be invoked when a click to the map has been made.
 */
@FunctionalInterface
interface OnMapClickListener {

    /**
     * Called when click to the map has been made.
     *
     * @param data [ViewObjectData] belonging to the click on the map.
     *
     * @return true if the callback consumed the click, false otherwise (click will be processed by the default behaviour).
     */
    fun onMapClick(data: ViewObjectData) : Boolean
}