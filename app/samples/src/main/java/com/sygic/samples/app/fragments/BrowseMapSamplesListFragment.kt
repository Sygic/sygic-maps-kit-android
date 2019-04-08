package com.sygic.samples.app.fragments

import com.sygic.samples.R
import com.sygic.samples.Samples
import com.sygic.samples.app.models.Sample

class BrowseMapSamplesListFragment : BaseSamplesListFragment() {

    override val title: Int = R.string.browse_map_samples
    override val items: List<Sample> = Samples.browseMapSampleList
}