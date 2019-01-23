package com.sygic.samples

import android.os.Bundle

class BrowseMapFullActivity : CommonSampleActivity() {

    override val filePath: String = "sample-browsemap-full/src/main/res/layout/activity_browsemap_full.xml"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_browsemap_full)
    }
}
