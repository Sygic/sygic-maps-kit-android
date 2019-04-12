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
import android.view.Gravity
import androidx.appcompat.widget.PopupMenu
import com.sygic.maps.module.browsemap.BrowseMapFragment
import com.sygic.maps.module.common.mapinteraction.MapSelectionMode
import com.sygic.sdk.map.`object`.MapMarker
import com.sygic.maps.uikit.viewmodels.common.data.BasicData
import com.sygic.samples.R
import com.sygic.samples.app.activities.CommonSampleActivity
import kotlinx.android.synthetic.main.activity_browsemap_modes.*

class BrowseMapModesActivity : CommonSampleActivity() {

    override val wikiModulePath: String = "Module-Browse-Map#browse-map---modes"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_browsemap_modes)

        val browseMapFragment = supportFragmentManager.findFragmentById(R.id.browseMapFragment) as BrowseMapFragment
        val selectionModeMenu = PopupMenu(this, selectionModeButton, Gravity.END).apply {
            menuInflater.inflate(R.menu.selection_modes_menu, menu)
            menu.findItem(getItemId(browseMapFragment.mapSelectionMode)).let {
                it.isChecked = true
                setSelectionModeText(it.title)
            }
            setOnMenuItemClickListener { item ->
                browseMapFragment.mapSelectionMode = getMapSelectionMode(item.itemId)
                item.isChecked = true
                setSelectionModeText(item.title)
                true
            }
        }

        selectionModeButton.setOnClickListener {
            selectionModeMenu.show()
        }

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

    private fun getItemId(mapSelectionMode: Int): Int {
        return when (mapSelectionMode) {
            MapSelectionMode.NONE -> R.id.selection_mode_none
            MapSelectionMode.MARKERS_ONLY -> R.id.selection_mode_markers_only
            MapSelectionMode.FULL -> R.id.selection_mode_full
            else -> R.id.selection_mode_none
        }
    }

    @MapSelectionMode
    private fun getMapSelectionMode(itemId: Int): Int {
        return when (itemId) {
            R.id.selection_mode_none -> MapSelectionMode.NONE
            R.id.selection_mode_markers_only -> MapSelectionMode.MARKERS_ONLY
            R.id.selection_mode_full -> MapSelectionMode.FULL
            else -> MapSelectionMode.NONE
        }
    }

    private fun setSelectionModeText(mode: CharSequence) {
        selectionModeButton.text = "${getString(R.string.selection_mode_is)}: $mode"
    }
}
