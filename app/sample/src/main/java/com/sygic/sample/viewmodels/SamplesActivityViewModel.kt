package com.sygic.sample.viewmodels

import android.view.MenuItem
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.google.android.material.navigation.NavigationView
import com.sygic.sample.R
import com.sygic.ui.common.extensions.asSingleEvent
import com.sygic.ui.common.livedata.SingleLiveEvent

private const val GITHUB_SOURCE_CODE = "https://github.com/Sygic/sygic-maps-kit-android"
private const val GITHUB_WIKI = "https://github.com/Sygic/sygic-maps-kit-android/wiki"
private const val STACK_OVERFLOW = "https://stackoverflow.com/questions/tagged/android+sygic"

class SamplesActivityViewModel : ViewModel(), DefaultLifecycleObserver, NavigationView.OnNavigationItemSelectedListener {

    val navigationItemListenerObservable: LiveData<Any> = SingleLiveEvent()
    val closeDrawerLayoutObservable: LiveData<Any> = SingleLiveEvent()
    val notImplementedToastObservable: LiveData<Any> = SingleLiveEvent()
    val openLinkInBrowserObservable: LiveData<String> = SingleLiveEvent()

    override fun onCreate(owner: LifecycleOwner) {
        navigationItemListenerObservable.asSingleEvent().call()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_browse_map_module -> {
                //todo
            }
            R.id.nav_ui_kit_compass, R.id.nav_ui_kit_position_lock_fab,
            R.id.nav_ui_kit_zoom_controls, R.id.nav_ui_kit_poi_detail -> {
                notImplementedToastObservable.asSingleEvent().call()
                return false
            }
            R.id.nav_source_code -> openLinkInBrowserObservable.asSingleEvent().value = GITHUB_SOURCE_CODE
            R.id.nav_wiki -> openLinkInBrowserObservable.asSingleEvent().value = GITHUB_WIKI
            R.id.nav_qa -> openLinkInBrowserObservable.asSingleEvent().value = STACK_OVERFLOW
        }

        closeDrawerLayoutObservable.asSingleEvent().call()
        return true
    }
}