/*
 * Copyright (c) 2019 Sygic a.s. All rights reserved.
 *
 * This project is licensed under the MIT License.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.sygic.maps.module.common.listener

import com.sygic.maps.module.common.detail.DetailsViewFactory
import com.sygic.maps.uikit.viewmodels.common.sdk.viewobject.SelectionType
import com.sygic.maps.uikit.views.poidetail.PoiDetailBottomDialogFragment
import com.sygic.sdk.map.`object`.MapMarker
import com.sygic.sdk.map.`object`.data.ViewObjectData

/**
 * Interface definition for a callback to be invoked when a click to the map has been made.
 */
interface OnMapClickListener {

    /**
     * Modifies the map click [MapMarker] default behavior. You can override this method to use your own map
     * click [MapMarker] or return null for no marker. Calling super will create default [MapMarker].
     *
     * @return your [MapMarker] to display or null if no [MapMarker] should be displayed.
     */
    fun getClickMapMarker(latitude: Double, longitude: Double): MapMarker? = MapMarker.at(latitude, longitude).build()

    /**
     * Modifies the details view ([PoiDetailBottomDialogFragment] or [DetailsViewFactory] if set) default behavior. If true,
     * the details view will be shown when the [onMapDataReceived] is called.
     *
     * @return true to use the default behaviour, false otherwise. The default value is true.
     */
    fun showDetailsView(): Boolean = true

    /**
     * Called when a click to the map has been made.
     *
     * @param selectionType [SelectionType] describing the place clicked.
     * @param latitude [Double] coordinate of the place clicked.
     * @param longitude [Double] coordinate of the place clicked.
     *
     * @return true to continue to process clicked point's data, false otherwise.
     */
    fun onMapClick(@SelectionType selectionType: Int, latitude: Double, longitude: Double): Boolean = true

    /**
     * Called when the clicked point's data are received (it may take a while depending on user's connection). It will not be called, if the [onMapClick] method returns false.
     *
     * @param data [ViewObjectData] belonging to the click on the map.
     */
    fun onMapDataReceived(data: ViewObjectData)
}
