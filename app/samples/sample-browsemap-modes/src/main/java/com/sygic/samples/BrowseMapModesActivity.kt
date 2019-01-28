package com.sygic.samples

import android.os.Bundle
import android.view.Gravity
import androidx.appcompat.widget.PopupMenu
import com.sygic.modules.browsemap.BrowseMapFragment
import com.sygic.modules.common.mapinteraction.MapSelectionMode
import com.sygic.ui.common.extensions.classPathToUrl
import com.sygic.ui.common.sdk.mapobject.MapMarker
import kotlinx.android.synthetic.main.activity_browsemap_modes.*

class BrowseMapModesActivity : CommonSampleActivity() {

    override val filePath: String =
        "sample-browsemap-modes/src/main/java/" + BrowseMapModesActivity::class.java.name.classPathToUrl() + ".kt"

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
                MapMarker.Builder()
                    .coordinates(48.143489, 17.150560)
                    .title("Marker 1")
                    .build(),
                MapMarker.Builder()
                    .coordinates(48.162805, 17.101621)
                    .title("Marker 2")
                    .build(),
                MapMarker.Builder()
                    .coordinates(48.165561, 17.139550)
                    .title("Marker 3")
                    .build(),
                MapMarker.Builder()
                    .coordinates(48.155028, 17.155674)
                    .title("Marker 4")
                    .build(),
                MapMarker.Builder()
                    .coordinates(48.141797, 17.097001)
                    .title("Marker 5")
                    .build(),
                MapMarker.Builder()
                    .coordinates(48.134756, 17.127729)
                    .title("Marker 6")
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
