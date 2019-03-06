package com.sygic.samples.idling

import com.sygic.samples.CommonSampleActivity
import com.sygic.sdk.map.MapFragment

class MapReadyIdlingResource(activity: CommonSampleActivity) : BaseIdlingResource(activity) {

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
}
