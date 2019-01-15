package com.sygic.sample.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.sygic.sample.R
import com.sygic.sample.viewmodels.SamplesActivityViewModel
import com.sygic.ui.common.extensions.openUrl
import kotlinx.android.synthetic.main.activity_sample.*

class SamplesActivity : AppCompatActivity() {

    private lateinit var samplesActivityViewModel: SamplesActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_sample)
        setSupportActionBar(toolbar)

        samplesActivityViewModel = ViewModelProviders.of(this).get(SamplesActivityViewModel::class.java).apply {
            this.navigationItemListenerObservable.observe(
                this@SamplesActivity,
                Observer<Any> { navView.setNavigationItemSelectedListener(this) })
            this.closeDrawerLayoutObservable.observe(
                this@SamplesActivity,
                Observer<Any> { drawerLayout.closeDrawer(GravityCompat.START) })
            this.notImplementedToastObservable.observe(
                this@SamplesActivity,
                Observer<Any> { Toast.makeText(this@SamplesActivity, R.string.not_implemented_yet, Toast.LENGTH_SHORT).show() })
            this.openLinkInBrowserObservable.observe(
                this@SamplesActivity,
                Observer<String> { openUrl(it) })
        }
        lifecycle.addObserver(samplesActivityViewModel)

        initDrawerToggle()
    }

    private fun initDrawerToggle() {
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        lifecycle.removeObserver(samplesActivityViewModel)
    }
}
