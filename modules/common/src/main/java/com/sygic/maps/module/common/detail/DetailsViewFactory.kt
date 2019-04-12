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

package com.sygic.maps.module.common.detail

import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import com.sygic.sdk.map.`object`.data.ViewObjectData

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
     * @param inflater The [LayoutInflater] object that can be used to inflate
     * any views in the details window,
     * @param container This is the parent [ViewGroup] that the details view
     * will be attached to. You should not add the view itself,
     * this can only be used to generate the [LayoutParams] of the view.
     * @param data [ViewObjectData] associated with selected point / marker.
     * which can be used to enrich the layout with information.
     *
     * @return the view which will be used as an details view for selected points.
     */
    abstract fun getDetailsView(inflater: LayoutInflater, container: ViewGroup, data: ViewObjectData): View

    /**
     * Define the X offset for the details window.
     *
     * @return offset in pixels you want to move the window.
     */
    open fun getXOffset() = 0f

    /**
     * Define the Y offset for the details window.
     *
     * @return offset in pixels you want to move the window.
     */
    open fun getYOffset() = 0f
}
