package com.sygic.sample.activities

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.google.android.material.navigation.NavigationView
import com.sygic.sample.R
import kotlinx.android.synthetic.main.activity_sample.*

class SamplesActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_sample)
        setSupportActionBar(toolbar)
        initDrawer()
    }

    private fun initDrawer() {
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navView.setNavigationItemSelectedListener(this)
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean { //todo: move to VM
        when (item.itemId) {
            R.id.nav_browse_map_module -> {

            }
            R.id.nav_ui_kit_compass, R.id.nav_ui_kit_position_lock_fab,
            R.id.nav_ui_kit_zoom_controls, R.id.nav_ui_kit_poi_detail -> {

                return false
            }
            R.id.nav_source_code -> {

            }
            R.id.nav_wiki -> {

            }
            R.id.nav_qa -> {

            }
        }

        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}
