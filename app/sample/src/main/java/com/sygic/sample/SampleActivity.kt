package com.sygic.sample

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.sygic.modules.browsemap.BrowseMapFragment
import com.sygic.ui.common.sdk.mapobject.DefaultMapMarker
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
                    Toast.makeText(this, "Not implemented yet", Toast.LENGTH_SHORT).show()
                    return@OnNavigationItemSelectedListener true
                }
                R.id.bottom_nav_navigation -> {
                    Toast.makeText(this, "Not implemented yet", Toast.LENGTH_SHORT).show()
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        })

        val browseMapFragment = supportFragmentManager.findFragmentById(R.id.browseMapFragment) as BrowseMapFragment
        browseMapFragment.addMapMarkers(listOf(
            DefaultMapMarker(48.144921, 17.114853),
            DefaultMapMarker(48.143489, 17.150560),
            DefaultMapMarker(48.154687, 17.114679),
            DefaultMapMarker(48.155028, 17.155674),
            DefaultMapMarker(48.141797, 17.097001),
            DefaultMapMarker(48.134756, 17.127729),
            DefaultMapMarker(48.153943, 17.125282)))
    }
}
