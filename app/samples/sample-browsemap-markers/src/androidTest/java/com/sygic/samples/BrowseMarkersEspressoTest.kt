package com.sygic.samples

import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.GeneralLocation
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.sygic.samples.utils.MapMarkers
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BrowseMarkersEspressoTest : BaseMapTest(BrowseMapMarkersActivity::class.java) {

    @Test
    fun browseMapDisplayed() {
        browseMap(activity) {
            isPositionLockFabDisplayed()
            isZoomControlsMenuDisplayed()
        }
    }

    @Test
    fun mapMarkers() {
        browseMap(activity) {
            clickOnMapToLocation(GeneralLocation.CENTER_RIGHT)
            isPoiDetailHidden()
            clickOnMapToLocation(GeneralLocation.CENTER_LEFT)
            isPoiDetailHidden()
            clickOnMapToLocation(GeneralLocation.BOTTOM_CENTER)
            isPoiDetailHidden()

            clickOnMapMarker(MapMarkers.testMarkerOne)
            isPoiDetailVisible()
            pressBack()

            clickOnMapMarker(MapMarkers.testMarkerTwo)
            isPoiDetailVisible()
            pressBack()

            clickOnMapMarker(MapMarkers.testMarkerThree)
            isPoiDetailVisible()
            pressBack()

            clickOnMapMarker(MapMarkers.testMarkerFour)
            isPoiDetailVisible()
            pressBack()

            clickOnMapMarker(MapMarkers.testMarkerFive)
            isPoiDetailVisible()
            pressBack()

            clickOnMapMarker(MapMarkers.testMarkerSix)
            isPoiDetailVisible()
            pressBack()

            isPoiDetailHidden()
        }
    }
}