package com.sygic.samples

import android.os.Bundle

class BrowseMapDefaultActivity : CommonSampleActivity() {

    override val wikiModulePath: String = "Module-Browse-Map#browse-map---default"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_browsemap_default)
    }
}
