package com.sygic.samples.detail

import android.os.Parcel
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.sygic.modules.browsemap.detail.DetailsViewFactory
import com.sygic.samples.R
import com.sygic.ui.common.sdk.data.PoiData


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