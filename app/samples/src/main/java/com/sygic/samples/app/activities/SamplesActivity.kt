package com.sygic.samples.app.activities

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.sygic.samples.R
import com.sygic.samples.databinding.ActivitySamplesBinding
import com.sygic.samples.app.viewmodels.SamplesActivityViewModel
import com.sygic.maps.uikit.views.common.extensions.openUrl

class SamplesActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySamplesBinding
    private lateinit var samplesActivityViewModel: SamplesActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_samples)
        setSupportActionBar(binding.toolbar)
        initDrawerToggle()

        samplesActivityViewModel = ViewModelProviders.of(this).get(SamplesActivityViewModel::class.java).apply {
            this.closeDrawerLayoutObservable.observe(
                this@SamplesActivity,
                Observer<Any> { binding.drawerLayout.closeDrawer(GravityCompat.START) })
            this.samplesListFragmentsObservable.observe(
                this@SamplesActivity,
                Observer<Fragment> {
                    supportFragmentManager.beginTransaction().replace(R.id.fragmentContainer, it).commit()
                })
            this.openLinkInBrowserObservable.observe(
                this@SamplesActivity,
                Observer<String> { openUrl(it) })
            this.openDialogFragmentObservable.observe(
                this@SamplesActivity,
                Observer<Class<out AppCompatDialogFragment>> {
                    it.newInstance().show(supportFragmentManager, null)
                })
        }
        binding.samplesActivityViewModel = samplesActivityViewModel
        lifecycle.addObserver(samplesActivityViewModel)
    }

    private fun initDrawerToggle() {
        val toggle = ActionBarDrawerToggle(
            this, binding.drawerLayout, binding.toolbar, R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.samples_toolbar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return samplesActivityViewModel.onOptionsItemSelected(item.itemId)
    }

    override fun onDestroy() {
        super.onDestroy()

        lifecycle.removeObserver(samplesActivityViewModel)
    }
}
