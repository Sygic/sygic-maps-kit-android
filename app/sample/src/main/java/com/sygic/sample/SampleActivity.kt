package com.sygic.sample

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.sygic.modules.browsemap.BrowseMapFragment
import com.sygic.sdk.map.factory.DrawableFactory
import com.sygic.ui.common.sdk.mapobject.MapMarker
import kotlinx.android.synthetic.main.activity_sample.*

class SampleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sample)

        navigation.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.bottom_nav_browse_map -> {
                    return@OnNavigationItemSelectedListener true
                }
                R.id.bottom_nav_search -> {
                    showNotImplementedToast()
                    return@OnNavigationItemSelectedListener false
                }
                R.id.bottom_nav_navigation -> {
                    showNotImplementedToast()
                    return@OnNavigationItemSelectedListener false
                }
            }
            false
        })

        val markerFromBuilder = MapMarker.Builder()
            .coordinates(48.130550, 17.173795)
            .title("Marker created by Builder")
            .build()

        val markerFromBuilderWithCustomIcon = MapMarker.Builder()
            .coordinates(48.127531, 17.076463)
            .title("Marker with custom icon created by Builder")
            .iconDrawable(R.drawable.ic_map_pin)
            .build()

        val browseMapFragment = supportFragmentManager.findFragmentById(R.id.browseMapFragment) as BrowseMapFragment
        browseMapFragment.addMapMarkers(listOf(
            MapMarker(48.143489, 17.150560),
            MapMarker(48.154687, 17.114679),
            MapMarker(48.155028, 17.155674),
            MapMarker(48.141797, 17.097001),
            MapMarker(48.134756, 17.127729),
            MapMarker(48.153943, 17.125282),
            markerFromBuilder,
            markerFromBuilderWithCustomIcon,
            MapMarker(48.144921, 17.114853, DrawableFactory(R.drawable.ic_map_pin))))

        /* Todo: use it in example APP
        browseMapFragment.addOnMapClickListener { poiData ->
            Log.d("SampleActivity", "onMapClickListener() called with: poiData = [$poiData]")
        }*/
    }

    private fun showNotImplementedToast() {
        Toast.makeText(this, "Not implemented yet", Toast.LENGTH_SHORT).show()
    }
}
