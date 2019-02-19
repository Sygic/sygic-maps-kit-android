package com.sygic.samples

import android.os.Bundle
import android.widget.Toast
import com.sygic.modules.browsemap.BrowseMapFragment
import com.sygic.samples.payload.CustomPayload
import com.sygic.sdk.map.`object`.MapMarker
import com.sygic.sdk.position.GeoCoordinates

class BrowseMapClickListenerActivity : CommonSampleActivity() {

    override val wikiModulePath: String = "Module-Browse-Map#browse-map---click-listener"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_browsemap_click_listener)

        val markerFromBuilder = MapMarker.Builder()
            .payload(
                CustomPayload(
                    GeoCoordinates(48.146514, 17.124175),
                    "Custom Payload title",
                    "Custom Payload description",
                    "Custom Payload data member"
                )
            )
            .build()

        val browseMapFragment = supportFragmentManager.findFragmentById(R.id.browseMapFragment) as BrowseMapFragment
        browseMapFragment.addMapMarker(markerFromBuilder)
        browseMapFragment.setOnMapClickListener { data ->
            when (data) {
                is CustomPayload -> {
                    // Note: This is my custom payload
                    Toast.makeText(this, "CustomPayload\n${data.customString}", Toast.LENGTH_LONG).show()
                }
                else -> Toast.makeText(this, data.toString(), Toast.LENGTH_LONG).show()
            }
            true
        }
    }
}
