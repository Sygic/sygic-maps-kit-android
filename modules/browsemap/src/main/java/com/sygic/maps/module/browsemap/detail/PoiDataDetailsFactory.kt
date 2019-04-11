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

package com.sygic.maps.module.browsemap.detail

import android.os.Parcel
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sygic.maps.module.common.detail.DetailsViewFactory
import com.sygic.maps.uikit.viewmodels.common.data.PoiData
import com.sygic.sdk.map.`object`.UiObject

internal class PoiDataDetailsFactory(
    private val factory: DetailsViewFactory,
    private val poiData: PoiData
) : UiObject.ViewFactory {

    constructor(parcel: Parcel) : this(
        parcel.readParcelable(DetailsViewFactory::class.java.classLoader)!!,
        parcel.readParcelable(PoiData::class.java.classLoader)!!
    )

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeParcelable(factory, flags)
        dest.writeParcelable(poiData, flags)
    }

    override fun describeContents() = 0

    override fun createView(inflater: LayoutInflater, container: ViewGroup): View {
        return factory.getDetailsView(inflater, container, poiData)
    }

    companion object CREATOR : Parcelable.Creator<PoiDataDetailsFactory> {
        override fun createFromParcel(parcel: Parcel): PoiDataDetailsFactory {
            return PoiDataDetailsFactory(parcel)
        }

        override fun newArray(size: Int): Array<PoiDataDetailsFactory?> {
            return arrayOfNulls(size)
        }
    }
}