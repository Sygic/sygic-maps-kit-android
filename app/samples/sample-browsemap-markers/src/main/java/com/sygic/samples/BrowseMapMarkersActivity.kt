package com.sygic.samples

import android.os.Bundle
import com.sygic.modules.browsemap.BrowseMapFragment
import com.sygic.samples.utils.MapMarkers
import com.sygic.sdk.map.`object`.MapMarker
import com.sygic.sdk.map.factory.DrawableFactory
import com.sygic.ui.common.sdk.data.BasicData

class BrowseMapMarkersActivity : CommonSampleActivity() {

    override val wikiModulePath: String = "Module-Browse-Map#browse-map---markers"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_browsemap_markers)

        val markerFromBuilder = MapMarker.from(48.130550, 17.173795)
            .withPayload(BasicData("Marker created by Builder (default icon)"))
            .build()

        val markerFromBuilderWithCustomIcon = MapMarker.from(48.127531, 17.076463)
            .withPayload(BasicData("Marker created by Builder (custom icon)", "And with stunning description :-D"))
            .withIcon(R.drawable.ic_android).build()

        val browseMapFragment = supportFragmentManager.findFragmentById(R.id.browseMapFragment) as BrowseMapFragment
        browseMapFragment.addMapMarkers(
            listOf(
                MapMarker.from(48.143489, 17.150560).build(),
                MapMarker.from(48.144921, 17.114853).withIcon(DrawableFactory(R.drawable.ic_favorite)).build(),
                markerFromBuilder,
                markerFromBuilderWithCustomIcon,
                MapMarkers.testMarkerOne,
                MapMarkers.testMarkerTwo,
                MapMarkers.testMarkerThree,
                MapMarkers.testMarkerFour,
                MapMarkers.testMarkerFive,
                MapMarkers.testMarkerSix
            )
        )
    }
}
