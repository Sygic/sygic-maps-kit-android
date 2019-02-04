package com.sygic.samples.viewmodels

import android.view.MenuItem
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.google.android.material.navigation.NavigationView
import com.sygic.samples.R
import com.sygic.samples.fragments.BrowseMapSamplesListFragment
import com.sygic.ui.common.extensions.asSingleEvent
import com.sygic.ui.common.livedata.SingleLiveEvent

private const val GITHUB_SOURCE_CODE = "https://github.com/Sygic/sygic-maps-kit-android"
private const val GITHUB_WIKI = "$GITHUB_SOURCE_CODE/wiki"
private const val GITHUB_WIKI_COMPASS = "$GITHUB_WIKI/UiKit-Compass"
private const val GITHUB_WIKI_POI_DETAIL = "$GITHUB_WIKI/UiKit-PoiDetail"
private const val GITHUB_WIKI_POSITION_LOCK_FAB = "$GITHUB_WIKI/UiKit-PositionLockFab"
private const val GITHUB_WIKI_ZOOM_CONTROLS = "$GITHUB_WIKI/UiKit-ZoomControls"
private const val STACK_OVERFLOW = "https://stackoverflow.com/questions/tagged/android+sygic"

class SamplesActivityViewModel : ViewModel(), DefaultLifecycleObserver, NavigationView.OnNavigationItemSelectedListener {

    val closeDrawerLayoutObservable: LiveData<Any> = SingleLiveEvent()
    val samplesListFragmentsObservable: LiveData<Fragment> = SingleLiveEvent()
    val openLinkInBrowserObservable: LiveData<String> = SingleLiveEvent()

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
            R.id.nav_source_code -> openLinkInBrowserObservable.asSingleEvent().value = GITHUB_SOURCE_CODE
            R.id.nav_wiki -> openLinkInBrowserObservable.asSingleEvent().value = GITHUB_WIKI
            R.id.nav_qa -> openLinkInBrowserObservable.asSingleEvent().value = STACK_OVERFLOW
        }

        closeDrawerLayoutObservable.asSingleEvent().call()
        return true
    }
}