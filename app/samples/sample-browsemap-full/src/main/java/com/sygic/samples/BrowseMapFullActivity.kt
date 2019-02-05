package com.sygic.samples

import android.os.Bundle

class BrowseMapFullActivity : CommonSampleActivity() {

    override val wikiModulePath: String = "Module-Browse-Map#browse-map---full"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_browsemap_full)
    }
}
