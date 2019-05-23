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
import com.sygic.maps.module.common.detail.DetailsViewFactory
import com.sygic.sdk.map.MapView
import com.sygic.sdk.map.`object`.MapMarker
import com.sygic.sdk.map.`object`.UiObject
import com.sygic.sdk.map.`object`.ViewObject
import com.sygic.sdk.map.`object`.data.ObjectCreator
import com.sygic.sdk.map.`object`.data.UiObjectData
import com.sygic.sdk.map.`object`.data.ViewObjectData

internal class PoiDetailsObject : UiObject {

    private var xOffset: Int? = null
    private var yOffset: Int? = null
    private val factory: DetailsViewFactory
    private val viewObject: ViewObject<*>

    private constructor(data: UiObjectData, factory: DetailsViewFactory, viewObject: ViewObject<*>) : super(data) {
        this.factory = factory
        this.viewObject = viewObject
    }

    companion object {
        internal fun create(
            data: ViewObjectData,
            factory: DetailsViewFactory,
            viewObject: ViewObject<*>
        ): PoiDetailsObject {
            return UiObjectData.Builder(data.position, PoiDataDetailsFactory(factory, data),
                ObjectCreator { uiData ->
                    PoiDetailsObject(
                        uiData,
                        factory,
                        viewObject
                    )
                })
                .setAnchor(0.5f, 1f)
                .build() as PoiDetailsObject
        }

        @JvmField
        val CREATOR: Parcelable.Creator<PoiDetailsObject> = object : Parcelable.Creator<PoiDetailsObject> {
            override fun createFromParcel(parcel: Parcel): PoiDetailsObject {
                return PoiDetailsObject(parcel)
            }

            override fun newArray(size: Int): Array<PoiDetailsObject?> {
                return arrayOfNulls(size)
            }
        }
    }

    override fun onScreenPointsReady(mapView: MapView, x: Int, y: Int) {
        val localXOffset = xOffset
        val localYOffset = yOffset

        if (localXOffset == null || localYOffset == null) {
            val markerHeight = if (viewObject is MapMarker) {
                mapView.view?.let {
                    viewObject.data.getBitmap(it.context)?.height
                } ?: 0
            } else 0

            super.onScreenPointsReady(mapView,
                x + factory.getXOffset().toInt().also { xOffset = it },
                y - (markerHeight + factory.getYOffset()).toInt().also { yOffset = it })
        } else {
            super.onScreenPointsReady(mapView, x + localXOffset, y - localYOffset)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as PoiDetailsObject

        if (factory != other.factory) return false
        if (viewObject != other.viewObject) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + factory.hashCode()
        result = 31 * result + viewObject.hashCode()
        return result
    }

    private constructor(parcel: Parcel) : super(parcel) {
        factory = parcel.readParcelable(DetailsViewFactory::class.java.classLoader)!!
        viewObject = parcel.readParcelable(ViewObject::class.java.classLoader)!!
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        super.writeToParcel(dest, flags)

        dest.writeParcelable(factory, flags)
        dest.writeParcelable(viewObject, flags)
    }
}