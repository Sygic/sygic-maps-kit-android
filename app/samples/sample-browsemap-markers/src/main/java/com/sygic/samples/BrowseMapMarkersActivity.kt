package com.sygic.samples

import android.os.Bundle
import com.sygic.modules.browsemap.BrowseMapFragment
import com.sygic.sdk.map.`object`.MapMarker
import com.sygic.sdk.map.factory.DrawableFactory
import com.sygic.ui.common.sdk.data.BasicData

class BrowseMapMarkersActivity : CommonSampleActivity() { //todo: wiki update

    override val wikiModulePath: String = "Module-Browse-Map#browse-map---markers"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_browsemap_markers)

        val markerFromBuilder = MapMarker.from(48.130550, 17.173795)
            .payload(BasicData("Marker created by Builder (default icon)"))
            .build()

        val markerFromBuilderWithCustomIcon = MapMarker.from(48.127531, 17.076463)
            .payload(BasicData("Marker created by Builder (custom icon)", "And with stunning description :-D"))
            .iconDrawable(R.drawable.ic_android).build()

        val browseMapFragment = supportFragmentManager.findFragmentById(R.id.browseMapFragment) as BrowseMapFragment
        browseMapFragment.addMapMarkers(
            listOf(
                MapMarker.from(48.143489, 17.150560).build(),
                MapMarker.from(48.162805, 17.101621).build(),
                MapMarker.from(48.165561, 17.139550).build(),
                MapMarker.from(48.155028, 17.155674).build(),
                MapMarker.from(48.141797, 17.097001).build(),
                MapMarker.from(48.134756, 17.127729).build(),
                markerFromBuilder,
                markerFromBuilderWithCustomIcon,
                MapMarker.from(48.144921, 17.114853)
                    .iconFactory(DrawableFactory(R.drawable.ic_favorite))
                    .build()
            )
        )
    }
}
