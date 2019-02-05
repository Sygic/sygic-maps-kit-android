package com.sygic.samples

import android.os.Bundle
import com.sygic.modules.browsemap.BrowseMapFragment
import com.sygic.samples.detail.CustomDetailsViewFactory
import com.sygic.ui.common.extensions.classPathToUrl
import com.sygic.ui.common.sdk.mapobject.MapMarker

class BrowseMapDetailsWindowActivity : CommonSampleActivity() {

    override val filePath: String =
        "sample-browsemap-details-window/src/main/java/${BrowseMapDetailsWindowActivity::class.java.name.classPathToUrl()}.kt"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_browsemap_details_window)

        val browseMapFragment = supportFragmentManager.findFragmentById(R.id.browseMapFragment) as BrowseMapFragment
        browseMapFragment.addMapMarkers(
            listOf(
                MapMarker(48.143489, 17.150560),
                MapMarker(48.162805, 17.101621),
                MapMarker(48.165561, 17.139550),
                MapMarker(48.155028, 17.155674),
                MapMarker(48.141797, 17.097001),
                MapMarker(48.134756, 17.127729)
            )
        )

        browseMapFragment.setDetailsViewFactory(CustomDetailsViewFactory())
    }
}
