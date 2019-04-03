package com.sygic.samples.idling

import com.sygic.samples.CommonSampleActivity
import com.sygic.sdk.map.MapFragment

class MapReadyIdlingResource(activity: CommonSampleActivity) : BaseIdlingResource(activity) {

    override fun getName(): String = "MapReadyIdlingResource"

    override fun isIdle(): Boolean {
        activity.supportFragmentManager.fragments.forEach { fragment ->
            if (fragment is MapFragment) return fragment.mapView != null
        }

        return false
    }
}
