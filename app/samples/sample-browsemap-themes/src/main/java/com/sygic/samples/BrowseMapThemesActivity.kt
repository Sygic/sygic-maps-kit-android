package com.sygic.samples

import android.os.Bundle
import com.sygic.modules.browsemap.BrowseMapFragment
import com.sygic.ui.common.extensions.classPathToUrl
import com.sygic.ui.common.sdk.mapobject.MapMarker

class BrowseMapThemesActivity : CommonSampleActivity() {

    override val filePath: String =
        "sample-browsemap-themes/src/main/java/" + BrowseMapThemesActivity::class.java.name.classPathToUrl() + ".kt"

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_Sygic_Colored)
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_browsemap_themes)

        val browseMapFragment = supportFragmentManager.findFragmentById(R.id.browseMapFragment) as BrowseMapFragment
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
}
