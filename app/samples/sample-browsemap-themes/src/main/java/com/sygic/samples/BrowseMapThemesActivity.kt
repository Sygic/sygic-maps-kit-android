package com.sygic.samples

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
