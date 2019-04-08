package com.sygic.samples.browsemap

import android.os.Bundle
import com.sygic.samples.R
import com.sygic.samples.app.activities.CommonSampleActivity

class BrowseMapDefaultActivity : CommonSampleActivity() {

    override val wikiModulePath: String = "Module-Browse-Map#browse-map---default"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_browsemap_default)
    }
}
