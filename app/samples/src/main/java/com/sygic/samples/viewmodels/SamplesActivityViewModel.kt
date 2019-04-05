package com.sygic.samples.viewmodels

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
import com.sygic.samples.dialogs.AboutDialog
import com.sygic.samples.fragments.BrowseMapSamplesListFragment
import com.sygic.ui.common.extensions.asSingleEvent
import com.sygic.ui.common.livedata.SingleLiveEvent

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