package com.sygic.samples

import android.os.Bundle
import com.sygic.modules.browsemap.BrowseMapFragment
import com.sygic.sdk.map.`object`.MapMarker
import com.sygic.sdk.map.factory.DrawableFactory
import com.sygic.sdk.position.GeoCoordinates
import com.sygic.ui.common.sdk.data.BasicMarkerData

class BrowseMapMarkersActivity : CommonSampleActivity() {

    override val wikiModulePath: String = "Module-Browse-Map#browse-map---markers"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_browsemap_markers)

        val markerFromBuilder = MapMarker.from(
            BasicMarkerData(
                "Marker created by Builder (default icon)",
                position = GeoCoordinates(48.130550, 17.173795)
            )
        ).build()

        val markerFromBuilderWithCustomIcon = MapMarker.from(
            BasicMarkerData(
                "Marker created by Builder (custom icon)",
                "And with stunning description :-D",
                GeoCoordinates(48.127531, 17.076463)
            )
        )
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
                MapMarker.from(48.144921, 17.114853)
                    .iconFactory(DrawableFactory(R.drawable.ic_favorite))
                    .build()
            )
        )
    }
}
