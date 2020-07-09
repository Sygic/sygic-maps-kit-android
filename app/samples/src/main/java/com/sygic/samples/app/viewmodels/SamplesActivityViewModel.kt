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

package com.sygic.samples.app.viewmodels

import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.google.android.material.navigation.NavigationView
import com.sygic.samples.offlinemaps.OfflineMapsActivity
import com.sygic.maps.uikit.views.common.extensions.asSingleEvent
import com.sygic.maps.uikit.views.common.livedata.SingleLiveEvent
import com.sygic.samples.BuildConfig
import com.sygic.samples.R
import com.sygic.samples.app.dialogs.AboutDialog
import com.sygic.samples.app.fragments.*

private const val GITHUB_WIKI_COMPASS = "${BuildConfig.GITHUB_WIKI}UiKit-Compass"
private const val GITHUB_WIKI_PLACE_DETAIL = "${BuildConfig.GITHUB_WIKI}UiKit-Place-Detail"
private const val GITHUB_WIKI_POSITION_LOCK_FAB = "${BuildConfig.GITHUB_WIKI}UiKit-Position-Lock-Fab"
private const val GITHUB_WIKI_ZOOM_CONTROLS = "${BuildConfig.GITHUB_WIKI}UiKit-Zoom-Controls"
private const val GITHUB_WIKI_SEARCH_TOOLBAR = "${BuildConfig.GITHUB_WIKI}UiKit-Search-Toolbar"
private const val GITHUB_WIKI_SEARCH_RESULT_LIST = "${BuildConfig.GITHUB_WIKI}UiKit-Search-Result-List"

class SamplesActivityViewModel : ViewModel(), DefaultLifecycleObserver, NavigationView.OnNavigationItemSelectedListener {

    val drawerOpenedObservable: LiveData<Boolean> = SingleLiveEvent()
    val drawerItemCheckObservable: LiveData<Int> = SingleLiveEvent()
    val samplesListFragmentsObservable: LiveData<BaseSamplesListFragment> = SingleLiveEvent()
    val openLinkInBrowserObservable: LiveData<String> = SingleLiveEvent()
    val openDialogFragmentObservable: LiveData<Class<out AppCompatDialogFragment>> = SingleLiveEvent()
    val startActivityObservable: LiveData<Class<out AppCompatActivity>> = SingleLiveEvent()

    init {
        drawerItemCheckObservable.asSingleEvent().value = R.id.nav_demos
        samplesListFragmentsObservable.asSingleEvent().value = DemoSampleListFragment()
        drawerOpenedObservable.asSingleEvent().value = true
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_manage_maps -> {
                startActivityObservable.asSingleEvent().value = OfflineMapsActivity::class.java
                drawerOpenedObservable.asSingleEvent().value = false
                return false
            }
            R.id.nav_demos -> samplesListFragmentsObservable.asSingleEvent().value = DemoSampleListFragment()
            R.id.nav_browse_map_module -> samplesListFragmentsObservable.asSingleEvent().value = BrowseMapSampleListFragment()
            R.id.nav_search_module -> samplesListFragmentsObservable.asSingleEvent().value = SearchSampleListFragment()
            R.id.nav_navigation_module -> samplesListFragmentsObservable.asSingleEvent().value = NavigationSampleListFragment()
            R.id.nav_ui_kit_compass -> openLinkInBrowserObservable.asSingleEvent().value = GITHUB_WIKI_COMPASS
            R.id.nav_ui_kit_place_detail -> openLinkInBrowserObservable.asSingleEvent().value = GITHUB_WIKI_PLACE_DETAIL
            R.id.nav_ui_kit_position_lock_fab -> openLinkInBrowserObservable.asSingleEvent().value = GITHUB_WIKI_POSITION_LOCK_FAB
            R.id.nav_ui_kit_zoom_controls -> openLinkInBrowserObservable.asSingleEvent().value = GITHUB_WIKI_ZOOM_CONTROLS
            R.id.nav_ui_kit_search_toolbar -> openLinkInBrowserObservable.asSingleEvent().value = GITHUB_WIKI_SEARCH_TOOLBAR
            R.id.nav_ui_kit_search_result_list -> openLinkInBrowserObservable.asSingleEvent().value = GITHUB_WIKI_SEARCH_RESULT_LIST
            R.id.nav_source_code -> openLinkInBrowserObservable.asSingleEvent().value = BuildConfig.GITHUB_REPO
            R.id.nav_wiki -> openLinkInBrowserObservable.asSingleEvent().value = BuildConfig.GITHUB_WIKI
            R.id.nav_qa -> openLinkInBrowserObservable.asSingleEvent().value = BuildConfig.STACK_OVERFLOW
        }

        drawerOpenedObservable.asSingleEvent().value = false
        return true
    }

    fun onOptionsItemSelected(menuItemId: Int): Boolean {
        if (menuItemId == R.id.about) {
            openDialogFragmentObservable.asSingleEvent().value = AboutDialog::class.java
            return true
        }

        return false
    }
}