package com.sygic.samples

import android.view.View
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith
import androidx.appcompat.widget.MenuPopupWindow
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.action.GeneralLocation
import org.hamcrest.CoreMatchers
import androidx.test.espresso.matcher.RootMatchers
import com.sygic.modules.common.mapinteraction.MapSelectionMode
import com.sygic.samples.utils.MapMarkers

@RunWith(AndroidJUnit4::class)
class BrowseMapModesEspressoTest : BaseMapTest(BrowseMapModesActivity::class.java) {

    private val selectionModeButtonId = com.sygic.samples.common.R.id.selectionModeButton

    @Test
    fun browseMapDisplayed() {
        browseMap(activity) {
            isPositionLockFabDisplayed()
            isZoomControlsMenuDisplayed()
        }
    }

    @Test
    fun selectionModes() {
        browseMap(activity) {
            onView(withId(selectionModeButtonId)).check(matches(isDisplayed()))
            clickOnMapToLocation(GeneralLocation.CENTER)
            isPoiDetailHidden()
            clickOnMapToLocation(GeneralLocation.CENTER_RIGHT)
            isPoiDetailHidden()
            clickOnMapMarker(MapMarkers.testMarkerOne)
            isPoiDetailHidden()

            onView(withId(selectionModeButtonId)).perform(click())
            onData(CoreMatchers.anything())
                .inRoot(RootMatchers.isPlatformPopup())
                .inAdapterView(CoreMatchers.instanceOf<View>(MenuPopupWindow.MenuDropDownListView::class.java))
                .atPosition(MapSelectionMode.MARKERS_ONLY)
                .perform(click())

            clickOnMapToLocation(GeneralLocation.CENTER_LEFT)
            isPoiDetailHidden()
            clickOnMapMarker(MapMarkers.testMarkerOne)
            isPoiDetailVisible()
            pressBack()
            isPoiDetailHidden()

            onView(withId(selectionModeButtonId)).perform(click())
            onData(CoreMatchers.anything())
                .inRoot(RootMatchers.isPlatformPopup())
                .inAdapterView(CoreMatchers.instanceOf<View>(MenuPopupWindow.MenuDropDownListView::class.java))
                .atPosition(MapSelectionMode.FULL)
                .perform(click())

            clickOnMapToLocation(GeneralLocation.CENTER_LEFT)
            isPoiDetailVisible()
            pressBack()
            clickOnMapMarker(MapMarkers.testMarkerOne)
            isPoiDetailVisible()
            pressBack()
            isPoiDetailHidden()
        }
    }
}