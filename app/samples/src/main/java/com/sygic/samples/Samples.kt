package com.sygic.samples

import com.sygic.samples.models.Sample

object Samples {

    val browseMapSampleList: List<Sample> = listOf(
        Sample(
            BrowseMapDefaultActivity::class.java, R.drawable.preview_browsemap_default,
            R.string.browse_map_default, R.string.browse_map_default_summary
        ),
        Sample(
            BrowseMapFullActivity::class.java, R.drawable.preview_browsemap_full,
            R.string.browse_map_full, R.string.browse_map_full_summary
        ),
        Sample(
            BrowseMapModesActivity::class.java, R.drawable.preview_browsemap_modes,
            R.string.browse_map_modes, R.string.browse_map_modes_summary
        ),
        Sample(
            BrowseMapMarkersActivity::class.java, R.drawable.preview_browsemap_markers,
            R.string.browse_map_markers, R.string.browse_map_markers_summary
        ),
        Sample(
            BrowseMapDetailsWindowActivity::class.java, R.drawable.preview_browsemap_details_window,
            R.string.browse_map_details_window, R.string.browse_map_details_window_summary
        ),
        Sample(
            BrowseMapClickListenerActivity::class.java, R.drawable.preview_browsemap_click_listener,
            R.string.browse_map_click_listener, R.string.browse_map_click_listener_summary
        ),
        Sample(
            BrowseMapSplitActivity::class.java, R.drawable.preview_browsemap_split,
            R.string.browse_map_split, R.string.browse_map_split_summary
        ),
        Sample(
            BrowseMapThemesActivity::class.java, R.drawable.preview_browsemap_themes,
            R.string.browse_map_themes, R.string.browse_map_themes_summary
        )
    )
}
