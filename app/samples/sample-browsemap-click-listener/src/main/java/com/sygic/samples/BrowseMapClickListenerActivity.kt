package com.sygic.samples

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.sygic.modules.browsemap.BrowseMapFragment

class BrowseMapClickListenerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_browsemap_click_listener)

        val browseMapFragment = supportFragmentManager.findFragmentById(R.id.browseMapFragment) as BrowseMapFragment
        browseMapFragment.setOnMapClickListener {
            Toast.makeText(this, it.toString(), Toast.LENGTH_LONG).show()
        }
    }
}
