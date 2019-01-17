package com.sygic.samples

import com.sygic.samples.activities.SamplesActivity
import com.sygic.samples.models.Sample

object Samples {

    val browseMapSampleList: List<Sample> = listOf(
        Sample(
            com.sygic.samples.BrowseMapDefaultActivity::class.java, R.drawable.preview_browsemap_default,
            R.string.browse_map_default, R.string.browse_map_default_summary
        ),
        Sample(
            com.sygic.samples.BrowseMapFullActivity::class.java, R.drawable.preview_browsemap_full,
            R.string.browse_map_full, R.string.browse_map_full_summary
        ),
        Sample(
            com.sygic.samples.BrowseMapClickListenerActivity::class.java, R.drawable.preview_browsemap_click_listener,
            R.string.browse_map_click_listener, R.string.browse_map_click_listener_summary
        ),
        //todo
        Sample(
            SamplesActivity::class.java, R.drawable.sygic_logo_dark,
            R.string.app_name, R.string.not_implemented_yet
        ),
        Sample(
            SamplesActivity::class.java, R.drawable.sygic_logo_dark,
            R.string.app_name, R.string.not_implemented_yet
        )
    )
}
