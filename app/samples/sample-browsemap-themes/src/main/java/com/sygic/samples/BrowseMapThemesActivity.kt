package com.sygic.samples

import android.os.Bundle
import com.sygic.modules.browsemap.BrowseMapFragment
import com.sygic.sdk.map.`object`.MapMarker
import com.sygic.ui.common.sdk.data.BasicMarkerData

class BrowseMapThemesActivity : CommonSampleActivity() {

    override val wikiModulePath: String = "Module-Browse-Map#browse-map---themes"

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_Sygic_Colored)
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_browsemap_themes)

        val browseMapFragment = supportFragmentManager.findFragmentById(R.id.browseMapFragment) as BrowseMapFragment
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
}
