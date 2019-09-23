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
import com.sygic.maps.module.browsemap.BROWSE_MAP_FRAGMENT_TAG
import com.sygic.maps.module.browsemap.BrowseMapFragment
import com.sygic.maps.module.common.mapinteraction.MapSelectionMode
import com.sygic.samples.R
import com.sygic.samples.app.activities.CommonSampleActivity
import com.sygic.sdk.map.`object`.MapMarker
import com.sygic.sdk.position.GeoCoordinates

class BrowseMapSplitActivity : CommonSampleActivity() {

    override val wikiModulePath: String = "Module-Browse-Map#browse-map---split"

    private lateinit var browseMapFragment: BrowseMapFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_browsemap_split)

        if (savedInstanceState == null) {
            browseMapFragment = BrowseMapFragment().apply {
                cameraDataModel.zoomLevel = 14F
                cameraDataModel.position = GeoCoordinates(48.264514, 17.248591)
                positionOnMapEnabled = true
                mapSelectionMode = MapSelectionMode.FULL
                addMapMarker(MapMarker.at(48.264514, 17.248591).build())
            }.also {
                supportFragmentManager
                    ?.beginTransaction()
                    ?.replace(R.id.fragmentContainer, it, BROWSE_MAP_FRAGMENT_TAG)
                    ?.commit()
            }
        } else {
            browseMapFragment = supportFragmentManager.findFragmentByTag(BROWSE_MAP_FRAGMENT_TAG) as BrowseMapFragment
        }
    }
}
