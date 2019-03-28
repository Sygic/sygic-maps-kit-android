package com.sygic.samples

import android.os.Bundle
import com.sygic.modules.browsemap.BrowseMapFragment
import com.sygic.samples.detail.CustomDetailsViewFactory
import com.sygic.sdk.map.`object`.MapMarker
import com.sygic.ui.common.sdk.data.BasicData

class BrowseMapDetailsWindowActivity : CommonSampleActivity() {

    override val wikiModulePath: String = "Module-Browse-Map#browse-map---details-view"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_browsemap_details_window)

        val browseMapFragment = supportFragmentManager.findFragmentById(R.id.browseMapFragment) as BrowseMapFragment
        browseMapFragment.addMapMarkers(
            listOf(
                MapMarker.from(48.143489, 17.150560).build(),
                MapMarker.from(48.165561, 17.139550).build(),
                MapMarker.from(48.155028, 17.155674).build(),
                MapMarker.from(48.141797, 17.097001).build(),
                MapMarker.from(48.162805, 17.101621).payload(BasicData("My Marker 1")).build(),
                MapMarker.from(48.134756, 17.127729).payload(BasicData("My Marker 2")).build()
            )
        )

        browseMapFragment.setDetailsViewFactory(CustomDetailsViewFactory())
    }
}
