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
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.google.android.material.navigation.NavigationView
import com.sygic.samples.BuildConfig
import com.sygic.samples.R
import com.sygic.samples.app.dialogs.AboutDialog
import com.sygic.samples.app.fragments.BrowseMapSamplesListFragment
import com.sygic.maps.uikit.views.common.extensions.asSingleEvent
import com.sygic.maps.uikit.views.common.livedata.SingleLiveEvent

private const val GITHUB_WIKI_COMPASS = "${BuildConfig.GITHUB_WIKI}UiKit-Compass"
private const val GITHUB_WIKI_POI_DETAIL = "${BuildConfig.GITHUB_WIKI}UiKit-PoiDetail"
private const val GITHUB_WIKI_POSITION_LOCK_FAB = "${BuildConfig.GITHUB_WIKI}UiKit-PositionLockFab"
private const val GITHUB_WIKI_ZOOM_CONTROLS = "${BuildConfig.GITHUB_WIKI}UiKit-ZoomControls"

class SamplesActivityViewModel : ViewModel(), DefaultLifecycleObserver, NavigationView.OnNavigationItemSelectedListener {

    val closeDrawerLayoutObservable: LiveData<Any> = SingleLiveEvent()
    val samplesListFragmentsObservable: LiveData<Fragment> = SingleLiveEvent()
    val openLinkInBrowserObservable: LiveData<String> = SingleLiveEvent()
    val openDialogFragmentObservable: LiveData<Class<out AppCompatDialogFragment>> = SingleLiveEvent()

    override fun onCreate(owner: LifecycleOwner) {
        samplesListFragmentsObservable.asSingleEvent().value = BrowseMapSamplesListFragment()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_browse_map_module -> {
                samplesListFragmentsObservable.asSingleEvent().value = BrowseMapSamplesListFragment()
            }
            R.id.nav_ui_kit_compass -> openLinkInBrowserObservable.asSingleEvent().value = GITHUB_WIKI_COMPASS
            R.id.nav_ui_kit_poi_detail -> openLinkInBrowserObservable.asSingleEvent().value = GITHUB_WIKI_POI_DETAIL
            R.id.nav_ui_kit_position_lock_fab -> openLinkInBrowserObservable.asSingleEvent().value = GITHUB_WIKI_POSITION_LOCK_FAB
            R.id.nav_ui_kit_zoom_controls -> openLinkInBrowserObservable.asSingleEvent().value = GITHUB_WIKI_ZOOM_CONTROLS
            R.id.nav_source_code -> openLinkInBrowserObservable.asSingleEvent().value = BuildConfig.GITHUB_REPO
            R.id.nav_wiki -> openLinkInBrowserObservable.asSingleEvent().value = BuildConfig.GITHUB_WIKI
            R.id.nav_qa -> openLinkInBrowserObservable.asSingleEvent().value = BuildConfig.STACK_OVERFLOW
        }

        closeDrawerLayoutObservable.asSingleEvent().call()
        return true
    }

    fun onOptionsItemSelected(menuItemId: Int) : Boolean {
        if (menuItemId == R.id.about) {
            openDialogFragmentObservable.asSingleEvent().value = AboutDialog::class.java
            return true
        }

        return false
    }
}