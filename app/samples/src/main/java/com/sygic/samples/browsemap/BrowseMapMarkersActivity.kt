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
import com.sygic.sdk.map.factory.DrawableFactory
import com.sygic.maps.uikit.viewmodels.common.sdk.mapobject.MapMarker
import com.sygic.samples.R
import com.sygic.samples.app.activities.CommonSampleActivity

class BrowseMapMarkersActivity : CommonSampleActivity() {

    override val wikiModulePath: String = "Module-Browse-Map#browse-map---markers"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_browsemap_markers)

        val markerFromBuilder = MapMarker.Builder()
            .coordinates(48.130550, 17.173795)
            .title("Marker created by Builder")
            .build()

        val markerFromBuilderWithCustomIcon = MapMarker.Builder()
            .coordinates(48.127531, 17.076463)
            .title("Marker created by Builder (custom icon)")
            .iconDrawable(R.drawable.ic_android)
            .build()

        val browseMapFragment = supportFragmentManager.findFragmentById(R.id.browseMapFragment) as BrowseMapFragment
        browseMapFragment.addMapMarkers(
            listOf(
                MapMarker(48.143489, 17.150560),
                MapMarker(48.162805, 17.101621),
                MapMarker(48.165561, 17.139550),
                MapMarker(48.155028, 17.155674),
                MapMarker(48.141797, 17.097001),
                MapMarker(48.134756, 17.127729),
                markerFromBuilder,
                markerFromBuilderWithCustomIcon,
                MapMarker(48.144921, 17.114853, DrawableFactory(R.drawable.ic_favorite))
            )
        )
    }
}
