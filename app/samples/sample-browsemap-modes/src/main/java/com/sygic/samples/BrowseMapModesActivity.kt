package com.sygic.samples

import android.os.Bundle
import android.view.Gravity
import androidx.appcompat.widget.PopupMenu
import com.sygic.modules.browsemap.BrowseMapFragment
import com.sygic.modules.common.mapinteraction.MapSelectionMode
import com.sygic.sdk.map.`object`.MapMarker
import com.sygic.ui.common.sdk.data.BasicMarkerData
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
                MapMarker.from(BasicMarkerData("Marker 1", latitude = 48.143489, longitude = 17.150560))
                    .build(),
                MapMarker.from(BasicMarkerData("Marker 2", latitude = 48.162805, longitude = 17.101621))
                    .build(),
                MapMarker.from(BasicMarkerData("Marker 3", latitude = 48.165561, longitude = 17.139550))
                    .build(),
                MapMarker.from(BasicMarkerData("Marker 4", latitude = 48.155028, longitude = 17.155674))
                    .build(),
                MapMarker.from(BasicMarkerData("Marker 5", latitude = 48.141797, longitude = 17.097001))
                    .build(),
                MapMarker.from(BasicMarkerData("Marker 6", latitude = 48.134756, longitude = 17.127729))
                    .build()
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
