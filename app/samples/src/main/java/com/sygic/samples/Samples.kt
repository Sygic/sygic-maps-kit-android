package com.sygic.samples

import com.sygic.samples.activities.SamplesActivity
import com.sygic.samples.models.Sample

object Samples {

    val browseMapSampleList: List<Sample> = listOf(
        Sample(
            com.sygic.samples.BrowseMapDefaultActivity::class.java, R.drawable.preview_browsemap_simple,
            R.string.browse_map_default, R.string.browse_map_default_summary
        ),
        //todo
        Sample(
            SamplesActivity::class.java, R.drawable.sygic_logo_dark,
            R.string.app_name, R.string.not_implemented_yet
        ),
        Sample(
            SamplesActivity::class.java, R.drawable.sygic_logo_dark,
            R.string.app_name, R.string.not_implemented_yet
        ),
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
