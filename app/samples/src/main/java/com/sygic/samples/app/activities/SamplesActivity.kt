/*
 * Copyright (c) 2019 Sygic a.s. All rights reserved.
 *
 * This project is licensed under the MIT License.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.sygic.samples.app.activities

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.sygic.samples.R
import com.sygic.samples.databinding.ActivitySamplesBinding
import com.sygic.samples.app.viewmodels.SamplesActivityViewModel
import com.sygic.maps.uikit.views.common.extensions.openUrl
import com.sygic.samples.app.fragments.BaseSamplesListFragment

class SamplesActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySamplesBinding
    private lateinit var samplesActivityViewModel: SamplesActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_samples)
        setSupportActionBar(binding.toolbar)
        initDrawerToggle()

        samplesActivityViewModel = ViewModelProviders.of(this).get(SamplesActivityViewModel::class.java).apply {
            this.setDrawerLayoutOpenedObservable.observe(
                this@SamplesActivity,
                Observer<Boolean> { setDrawerLayoutOpened(it) })
            this.drawerItemCheckObservable.observe(
                this@SamplesActivity,
                Observer<Int> { binding.navigationView.setCheckedItem(it) })
            this.samplesListFragmentsObservable.observe(
                this@SamplesActivity,
                Observer<BaseSamplesListFragment> { placeFragment(it) })
            this.openLinkInBrowserObservable.observe(
                this@SamplesActivity,
                Observer<String> { openUrl(it) })
            this.openDialogFragmentObservable.observe(
                this@SamplesActivity,
                Observer<Class<out AppCompatDialogFragment>> { it.newInstance().show(supportFragmentManager, null) })
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

    private fun setDrawerLayoutOpened(opened: Boolean) {
        if (opened) {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        } else {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        }
    }

    private fun placeFragment(fragment: BaseSamplesListFragment) =
        supportFragmentManager.beginTransaction().replace(R.id.fragmentContainer, fragment).commit()

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
