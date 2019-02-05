package com.sygic.samples

import android.os.Bundle
import android.widget.Toast
import com.sygic.modules.browsemap.BrowseMapFragment

class BrowseMapClickListenerActivity : CommonSampleActivity() {

    override val wikiModulePath: String = "Module-Browse-Map#browse-map---click-listener"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_browsemap_click_listener)

        val browseMapFragment = supportFragmentManager.findFragmentById(R.id.browseMapFragment) as BrowseMapFragment
        browseMapFragment.setOnMapClickListener {
            Toast.makeText(this, it.toString(), Toast.LENGTH_LONG).show()
        }
    }
}
