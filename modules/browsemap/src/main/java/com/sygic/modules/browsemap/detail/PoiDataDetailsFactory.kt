package com.sygic.modules.browsemap.detail

import android.os.Parcel
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sygic.modules.common.detail.DetailsViewFactory
import com.sygic.sdk.map.`object`.UiObject
import com.sygic.ui.common.sdk.data.PoiData

internal class PoiDataDetailsFactory(private val factory: DetailsViewFactory, private val poiData: PoiData) :
    UiObject.ViewFactory {
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