package com.sygic.samples.idling

import androidx.test.espresso.IdlingResource
import com.sygic.samples.CommonSampleActivity
import com.sygic.sdk.map.MapFragment

class MapReadyIdlingResource(private val activity: CommonSampleActivity) : IdlingResource {

    private var callback: IdlingResource.ResourceCallback? = null

    override fun getName(): String = "MapReadyIdlingResource"

    override fun isIdleNow(): Boolean {
        activity.supportFragmentManager.fragments.forEach { fragment ->
            if (fragment is MapFragment) {
                fragment.mapView?.let {
                    callback?.onTransitionToIdle()
                    return true
                }
            }
        }

        return false
    }

    override fun registerIdleTransitionCallback(callback: IdlingResource.ResourceCallback?) {
        this.callback = callback
    }
}
