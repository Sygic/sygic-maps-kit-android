package com.sygic.samples.browsemap

import android.os.Bundle
import com.sygic.maps.module.browsemap.BrowseMapFragment
import com.sygic.samples.R
import com.sygic.samples.app.activities.CommonSampleActivity
import com.sygic.sdk.map.`object`.ClusterLayer
import com.sygic.sdk.map.`object`.MapMarker
import com.sygic.sdk.map.factory.DrawableFactory

class BrowseMapClustersActivity : CommonSampleActivity() {

    override val wikiModulePath: String = "Module-Browse-Map#browse-map---clusters"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_browsemap_clusters)

        val browseMapFragment = supportFragmentManager.findFragmentById(R.id.browseMapFragment) as BrowseMapFragment

        val sunny = listOf(
            MapMarker.at(48.143689, 17.150260).withIcon(DrawableFactory(R.drawable.ic_sunny, R.color.school_bus_yellow)).build(),
            MapMarker.at(48.143111, 17.150434).withIcon(DrawableFactory(R.drawable.ic_sunny, R.color.school_bus_yellow)).build(),
            MapMarker.at(48.143503, 17.150512).withIcon(DrawableFactory(R.drawable.ic_sunny, R.color.school_bus_yellow)).build(),
            MapMarker.at(48.143218, 17.150854).withIcon(DrawableFactory(R.drawable.ic_sunny, R.color.school_bus_yellow)).build()
        )

        val cloudy = listOf(
            MapMarker.at(48.143331, 17.150599).withIcon(DrawableFactory(R.drawable.ic_cloudy, R.color.azure_radiance)).build(),
            MapMarker.at(48.143589, 17.150476).withIcon(DrawableFactory(R.drawable.ic_cloudy, R.color.azure_radiance)).build(),
            MapMarker.at(48.143643, 17.150791).withIcon(DrawableFactory(R.drawable.ic_cloudy, R.color.azure_radiance)).build(),
            MapMarker.at(48.143148, 17.150232).withIcon(DrawableFactory(R.drawable.ic_cloudy, R.color.azure_radiance)).build()
        )

        browseMapFragment.addCluster(ClusterLayer(cloudy, 1, false))
        browseMapFragment.addCluster(ClusterLayer(sunny, 2, false))
    }
}
