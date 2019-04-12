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

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.sygic.maps.module.common.detail.DetailsViewFactory
import com.sygic.samples.R
import com.sygic.sdk.map.`object`.data.ViewObjectData
import com.sygic.maps.uikit.viewmodels.common.data.BasicData
import com.sygic.maps.uikit.viewmodels.common.extensions.getFormattedLocation
import kotlinx.android.parcel.Parcelize

@Parcelize
class CustomDetailsViewFactory : DetailsViewFactory() {

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