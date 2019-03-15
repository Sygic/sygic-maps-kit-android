package com.sygic.samples.detail

import android.os.Parcel
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.sygic.modules.common.detail.DetailsViewFactory
import com.sygic.samples.R
import com.sygic.sdk.map.`object`.payload.Payload
import com.sygic.ui.common.sdk.data.BasicPayload
import com.sygic.ui.common.sdk.extension.getFormattedLocation

class CustomDetailsViewFactory : DetailsViewFactory() {

    override fun getDetailsView(inflater: LayoutInflater, container: ViewGroup, data: Payload): View {
        val root = inflater.inflate(R.layout.layout_info_window, container, false)

        //fill layout with data from marker's Data
        when (data) {
            is BasicPayload -> {
                data.title.let {
                    if (it.isEmpty()) {
                        root.findViewById<TextView>(R.id.title).visibility = View.GONE
                    } else {
                        root.findViewById<TextView>(R.id.title).text = it
                    }
                }
                data.description.let {
                    if (it.isEmpty()) {
                        root.findViewById<TextView>(R.id.snippet).visibility = View.GONE
                    } else {
                        root.findViewById<TextView>(R.id.snippet).text = it
                    }
                }
            }
            else -> {
                root.findViewById<TextView>(R.id.title).text = data.position.getFormattedLocation()
            }
        }

        return root
    }

    override fun getYOffset() = 10f

    override fun writeToParcel(parcel: Parcel, flags: Int) {}

    override fun describeContents() = 0

    companion object CREATOR : Parcelable.Creator<CustomDetailsViewFactory> {
        override fun createFromParcel(parcel: Parcel): CustomDetailsViewFactory {
            return CustomDetailsViewFactory()
        }

        override fun newArray(size: Int): Array<CustomDetailsViewFactory?> {
            return arrayOfNulls(size)
        }
    }
}