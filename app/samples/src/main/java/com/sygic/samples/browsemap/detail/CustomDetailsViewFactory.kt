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

package com.sygic.samples.browsemap.detail

import android.os.Parcel
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.sygic.maps.module.common.detail.DetailsViewFactory
import com.sygic.maps.uikit.viewmodels.common.data.PoiData
import com.sygic.samples.R

class CustomDetailsViewFactory() : DetailsViewFactory() {

    override fun getDetailsView(inflater: LayoutInflater, container: ViewGroup, poiData: PoiData): View {
        val root = inflater.inflate(R.layout.layout_info_window, container, false)

        //fill layout with data from marker's PoiData
        val addressComponent = poiData.getAddressComponent()
        addressComponent.formattedTitle.let {
            if (it.isEmpty()) {
                root.findViewById<TextView>(R.id.title).visibility = View.GONE
            } else {
                root.findViewById<TextView>(R.id.title).text = it
            }
        }
        addressComponent.formattedSubtitle.let {
            if (it.isEmpty()) {
                root.findViewById<TextView>(R.id.snippet).visibility = View.GONE
            } else {
                root.findViewById<TextView>(R.id.snippet).text = it
            }
        }
        return root
    }

    override fun getYOffset() = 10f

    private constructor(parcel: Parcel) : this()

    override fun writeToParcel(parcel: Parcel, flags: Int) {}

    override fun describeContents() = 0

    companion object CREATOR : Parcelable.Creator<CustomDetailsViewFactory> {
        override fun createFromParcel(parcel: Parcel): CustomDetailsViewFactory {
            return CustomDetailsViewFactory(parcel)
        }

        override fun newArray(size: Int): Array<CustomDetailsViewFactory?> {
            return arrayOfNulls(size)
        }
    }
}