package com.sygic.samples

import android.os.Bundle

class BrowseMapDefaultActivity : CommonSampleActivity() {

    override val filePath: String = "sample-browsemap-def/src/main/res/layout/activity_browsemap_default.xml"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_browsemap_default)
    }
}
