package com.sygic.samples.app.activities

import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.sygic.maps.uikit.views.common.extensions.openUrl
import com.sygic.samples.BuildConfig
import com.sygic.samples.R

abstract class CommonSampleActivity : AppCompatActivity() {

    abstract val wikiModulePath: String

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.source_code) {
            openUrl(BuildConfig.GITHUB_WIKI + wikiModulePath)
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
