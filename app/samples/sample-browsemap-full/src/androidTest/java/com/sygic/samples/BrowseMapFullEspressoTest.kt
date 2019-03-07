package com.sygic.samples

import androidx.test.espresso.action.GeneralLocation
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BrowseMapFullEspressoTest : BaseMapTest(BrowseMapFullActivity::class.java) {

    @Test
    fun browseMapDisplayed() {
        browseMap(activity) {
            isCompassDisplayed()
            isPositionLockFabDisplayed()
            isZoomControlsMenuDisplayed()
        }
    }

    @Test
    fun clickOnMap_poiDetailVisible() {
        browseMap(activity) {
            isPoiDetailHidden()
            clickOnMapToLocation(GeneralLocation.CENTER)
            isPoiDetailVisible()
        }
    }
}