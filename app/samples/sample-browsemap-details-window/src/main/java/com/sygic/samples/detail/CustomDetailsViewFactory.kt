package com.sygic.samples.detail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.sygic.modules.common.detail.DetailsViewFactory
import com.sygic.samples.R
import com.sygic.sdk.map.`object`.data.ViewObjectData
import com.sygic.ui.common.sdk.data.BasicData
import com.sygic.ui.common.sdk.extension.getFormattedLocation
import kotlinx.android.parcel.Parcelize

class CustomDetailsViewFactory() : DetailsViewFactory() {

    override fun getDetailsView(inflater: LayoutInflater, container: ViewGroup, data: ViewObjectData): View {
        val root = inflater.inflate(R.layout.layout_info_window, container, false)

        // fill layout with data from marker's Data
        data.payload.let { payload ->
            when (payload) {
                is BasicData -> {
                    payload.title.let {
                        root.findViewById<TextView>(R.id.title).text =
                            if (!it.isEmpty()) it else data.position.getFormattedLocation()
                    }
                    payload.description.let { description ->
                        if (description.isEmpty()) {
                            root.findViewById<TextView>(R.id.snippet).visibility = View.GONE
                        } else {
                            root.findViewById<TextView>(R.id.snippet).text = description
                        }
                    }
                }
                else -> root.findViewById<TextView>(R.id.title).text = data.position.getFormattedLocation()
            }
        }

        return root
    }

    override fun getYOffset() = 10f
}