package com.sygic.samples.detail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.sygic.modules.common.detail.DetailsViewFactory
import com.sygic.samples.R
import com.sygic.sdk.map.`object`.payload.Payload
import com.sygic.ui.common.sdk.data.PoiDataPayload
import kotlinx.android.parcel.Parcelize

@Parcelize
class CustomDetailsViewFactory : DetailsViewFactory() {

    override fun getDetailsView(inflater: LayoutInflater, container: ViewGroup, data: Payload): View {
        val root = inflater.inflate(R.layout.layout_info_window, container, false)

        //fill layout with data from marker's Data
        when (data) {
            is PoiDataPayload -> {
                val addressComponent = data.addressComponent
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
            }
            else -> {
                root.findViewById<TextView>(R.id.title).text = data.title
                root.findViewById<TextView>(R.id.snippet).text = data.description
            }
        }

        return root
    }

    override fun getYOffset() = 10f
}