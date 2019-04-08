package com.sygic.samples.browsemap

import android.os.Bundle
import android.widget.Toast
import com.sygic.maps.module.browsemap.BrowseMapFragment
import com.sygic.samples.R
import com.sygic.samples.app.activities.CommonSampleActivity

class BrowseMapClickListenerActivity : CommonSampleActivity() {

    override val wikiModulePath: String = "Module-Browse-Map#browse-map---click-listener"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_browsemap_click_listener)

        val browseMapFragment = supportFragmentManager.findFragmentById(R.id.browseMapFragment) as BrowseMapFragment
        browseMapFragment.setOnMapClickListener {
            Toast.makeText(this, it.toString(), Toast.LENGTH_LONG).show()
            true
        }
    }
}
