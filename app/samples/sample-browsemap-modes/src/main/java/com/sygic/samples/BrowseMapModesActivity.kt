package com.sygic.samples

import android.os.Bundle
import android.view.Gravity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.PopupMenu
import com.sygic.modules.browsemap.BrowseMapFragment
import com.sygic.modules.common.mapinteraction.MapSelectionMode
import com.sygic.sdk.map.`object`.MapMarker

class BrowseMapModesActivity : CommonSampleActivity() {

    private val selectionModeButton: AppCompatButton? by lazy { findViewById<AppCompatButton>(R.id.selectionModeButton) }

    override val wikiModulePath: String = "Module-Browse-Map#browse-map---modes"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_browsemap_modes)

        val browseMapFragment = supportFragmentManager.findFragmentById(R.id.browseMapFragment) as BrowseMapFragment
        selectionModeButton?.let {
            val selectionModeMenu = PopupMenu(this, it, Gravity.END).apply {
                menuInflater.inflate(R.menu.selection_modes_menu, menu)
                menu.findItem(getItemId(browseMapFragment.mapSelectionMode)).let { menuItem ->
                    menuItem.isChecked = true
                    setSelectionModeText(menuItem.title)
                }
                setOnMenuItemClickListener { item ->
                    browseMapFragment.mapSelectionMode = getMapSelectionMode(item.itemId)
                    item.isChecked = true
                    setSelectionModeText(item.title)
                    true
                }
            }

            it.setOnClickListener { selectionModeMenu.show() }
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
        selectionModeButton?.text = "${getString(R.string.selection_mode_is)}: $mode"
    }
}
