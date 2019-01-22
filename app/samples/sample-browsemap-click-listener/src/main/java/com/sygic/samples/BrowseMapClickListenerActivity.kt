package com.sygic.samples

import android.os.Bundle
import android.widget.Toast
import com.sygic.modules.browsemap.BrowseMapFragment
import com.sygic.ui.common.extensions.classPathToUrl

class BrowseMapClickListenerActivity : CommonSampleActivity() {

    override val filePath: String =
        "sample-browsemap-click-listener/src/main/java/" + BrowseMapClickListenerActivity::class.java.name.classPathToUrl() + ".kt"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_browsemap_click_listener)

        val browseMapFragment = supportFragmentManager.findFragmentById(R.id.browseMapFragment) as BrowseMapFragment
        browseMapFragment.setOnMapClickListener {
            Toast.makeText(this, it.toString(), Toast.LENGTH_LONG).show()
        }
    }
}
