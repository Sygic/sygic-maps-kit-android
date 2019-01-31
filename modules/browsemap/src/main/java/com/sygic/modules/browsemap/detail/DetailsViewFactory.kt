package com.sygic.modules.browsemap.detail

import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sygic.modules.browsemap.BrowseMapFragment
import com.sygic.ui.common.sdk.data.PoiData


/**
 * Factory class that can be used to change the default behavior of showing
 * details about selected map points.
 *
 * Use it with [BrowseMapFragment.setDetailsViewFactory] method.
 */
abstract class DetailsViewFactory : Parcelable {

    /**
     * Called when the map wants to show a details window for a selected map point.
     *
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the details window,
     * @param container This is the parent view that the details view
     * will be attached to. You should not add the view itself,
     * this can only be used to generate the LayoutParams of the view.
     * @param poiData Data associated with selected point / marker
     * which can be used to enrich the layout with information.
     *
     * @return Return the view which will be used as an details view for selected points.
     */
    abstract fun getDetailsView(inflater: LayoutInflater, container: ViewGroup, poiData: PoiData): View

    /**
     * Define the X offset for the details window.
     *
     * @return Return offset in pixels you want to move the window
     */
    open fun getXOffset() = 0f

    /**
     * Define the Y offset for the details window.
     *
     * @return Return offset in pixels you want to move the window
     */
    open fun getYOffset() = 0f
}