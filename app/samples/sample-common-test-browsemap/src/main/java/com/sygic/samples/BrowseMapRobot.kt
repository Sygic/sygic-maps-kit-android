package com.sygic.samples

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId

fun browseMap(commonSampleActivity: CommonSampleActivity, func: BrowseMapRobot.() -> Unit) =
    BrowseMapRobot(commonSampleActivity).apply { func() }

class BrowseMapRobot(val activity: CommonSampleActivity) {

    init {
        onView(withId(com.sygic.modules.browsemap.R.id.browseMapFragment)).check(matches(isDisplayed()))
    }

    fun isCompassDisplayed() {
        onView(withId(com.sygic.ui.view.compass.R.id.compassView)).check(matches(isDisplayed()))
    }

    fun isPositionLockFabDisplayed() {
        onView(withId(com.sygic.ui.view.positionlockfab.R.id.positionLockFab)).check(matches(isDisplayed()))
    }

    fun isZoomControlsMenuDisplayed() {
        onView(withId(com.sygic.ui.view.zoomcontrols.R.id.zoomControlsMenu)).check(matches(isDisplayed()))
    }
}