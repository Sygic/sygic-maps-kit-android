package com.sygic.samples

import android.os.Bundle
import com.sygic.modules.browsemap.BrowseMapFragment
import com.sygic.sdk.map.`object`.ClusterLayer
import com.sygic.sdk.map.factory.DrawableFactory
import com.sygic.ui.common.sdk.mapobject.MapMarker

class BrowseMapClustersActivity : CommonSampleActivity() {

    override val wikiModulePath: String = "Module-Browse-Map#browse-map---clusters"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_browsemap_clusters)

        val browseMapFragment = supportFragmentManager.findFragmentById(R.id.browseMapFragment) as BrowseMapFragment

        val sunny = listOf(
            MapMarker(48.143689, 17.150260, DrawableFactory(R.drawable.ic_sunny, R.color.school_bus_yellow)),
            MapMarker(48.143111, 17.150434, DrawableFactory(R.drawable.ic_sunny, R.color.school_bus_yellow)),
            MapMarker(48.143503, 17.150512, DrawableFactory(R.drawable.ic_sunny, R.color.school_bus_yellow)),
            MapMarker(48.143218, 17.150854, DrawableFactory(R.drawable.ic_sunny, R.color.school_bus_yellow))
        )

        val cloudy = listOf(
            MapMarker(48.143331, 17.150599, DrawableFactory(R.drawable.ic_cloudy, R.color.azure_radiance)),
            MapMarker(48.143589, 17.150476, DrawableFactory(R.drawable.ic_cloudy, R.color.azure_radiance)),
            MapMarker(48.143643, 17.150791, DrawableFactory(R.drawable.ic_cloudy, R.color.azure_radiance)),
            MapMarker(48.143148, 17.150232, DrawableFactory(R.drawable.ic_cloudy, R.color.azure_radiance))
        )

        browseMapFragment.addCluster(ClusterLayer(cloudy, 1, false))
        browseMapFragment.addCluster(ClusterLayer(sunny, 2, false))
    }
}
