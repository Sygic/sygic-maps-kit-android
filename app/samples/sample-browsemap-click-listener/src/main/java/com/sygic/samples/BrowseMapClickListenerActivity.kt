package com.sygic.samples

import android.os.Bundle
import android.widget.Toast
import com.sygic.modules.browsemap.BrowseMapFragment
import com.sygic.samples.payload.CustomDataPayload
import com.sygic.sdk.map.`object`.MapMarker

class BrowseMapClickListenerActivity : CommonSampleActivity() {

    override val wikiModulePath: String = "Module-Browse-Map#browse-map---click-listener"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_browsemap_click_listener)

        val markerFromBuilder = MapMarker
            .from(48.146514, 17.124175)
            .payload(CustomDataPayload("This is my custom payload"))
            .build()

        val browseMapFragment = supportFragmentManager.findFragmentById(R.id.browseMapFragment) as BrowseMapFragment
        browseMapFragment.addMapMarker(markerFromBuilder)
        browseMapFragment.setOnMapClickListener { data ->
            data.payload.let { payload ->
                when (payload) {
                    is CustomDataPayload -> {
                        // Note: This is my custom payload
                        Toast.makeText(this, payload.customString, Toast.LENGTH_LONG).show()
                    }
                    else -> Toast.makeText(this, payload.toString(), Toast.LENGTH_LONG).show()
                }
            }
            true
        }
    }
}
