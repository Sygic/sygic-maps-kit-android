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

package com.sygic.samples.browsemap

import android.os.Bundle
import com.sygic.maps.module.browsemap.BrowseMapFragment
import com.sygic.maps.module.common.listener.OnMapClickListener
import com.sygic.maps.uikit.views.common.extensions.longToast
import com.sygic.samples.R
import com.sygic.samples.app.activities.CommonSampleActivity
import com.sygic.samples.browsemap.payload.CustomDataPayload
import com.sygic.sdk.map.`object`.MapMarker
import com.sygic.sdk.map.`object`.data.ViewObjectData

class BrowseMapClickListenerActivity : CommonSampleActivity() {

    override val wikiModulePath = "Module-Browse-Map#browse-map---click-listener"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_browsemap_click_listener)

        val markerFromBuilder = MapMarker
            .at(48.146514, 17.124175)
            .withPayload(CustomDataPayload("This is my custom payload"))
            .build()

        val browseMapFragment = supportFragmentManager.findFragmentById(R.id.browseMapFragment) as BrowseMapFragment
        browseMapFragment.addMapMarker(markerFromBuilder)
        browseMapFragment.setOnMapClickListener(object : OnMapClickListener {
            override fun showDetailsView(): Boolean = false
            override fun onMapDataReceived(data: ViewObjectData) {
                data.payload.let { payload ->
                    when (payload) {
                        is CustomDataPayload -> {
                            // Note: This is my custom payload
                            longToast(payload.customString)
                        }
                        else -> longToast(payload.toString())
                    }
                }
            }
        })
    }
}
