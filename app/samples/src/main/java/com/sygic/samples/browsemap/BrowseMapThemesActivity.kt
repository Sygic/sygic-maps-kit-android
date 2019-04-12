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
import com.sygic.modules.browsemap.BrowseMapFragment
import com.sygic.sdk.map.`object`.MapMarker
import com.sygic.ui.common.sdk.data.BasicData

class BrowseMapThemesActivity : CommonSampleActivity() {

    override val wikiModulePath: String = "Module-Browse-Map#browse-map---themes"

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_Sygic_Colored)
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_browsemap_themes)

        val browseMapFragment = supportFragmentManager.findFragmentById(R.id.browseMapFragment) as BrowseMapFragment
        browseMapFragment.addMapMarkers(
            listOf(
                MapMarker.from(48.143489, 17.150560).withPayload(BasicData("Marker 1")).build(),
                MapMarker.from(48.162805, 17.101621).withPayload(BasicData("Marker 2")).build(),
                MapMarker.from(48.165561, 17.139550).withPayload(BasicData("Marker 3")).build(),
                MapMarker.from(48.155028, 17.155674).withPayload(BasicData("Marker 4")).build(),
                MapMarker.from(48.141797, 17.097001).withPayload(BasicData("Marker 5")).build(),
                MapMarker.from(48.134756, 17.127729).withPayload(BasicData("Marker 6")).build()
            )
        )
    }
}
