package com.sygic.samples

import androidx.test.espresso.action.GeneralLocation
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BrowseMapCustomClickListenerEspressoTest : BaseMapTest(BrowseMapClickListenerActivity::class.java) {

    @Test
    fun browseMapDisplayed() {
        browseMap(activity) {
            isPositionLockFabDisplayed()
            isZoomControlsMenuDisplayed()
        }
    }

    @Test
    fun customClickListener() {
        browseMap(activity) {
            isPoiDetailHidden()

            clickOnMapToLocation(GeneralLocation.CENTER)
            isPoiDetailHidden()
            isToastVisible()

            clickOnMapToLocation(GeneralLocation.CENTER_LEFT)
            isPoiDetailHidden()
            isToastVisible()

            clickOnMapToLocation(GeneralLocation.CENTER_RIGHT)
            isPoiDetailHidden()
            isToastVisible()
        }
    }
}