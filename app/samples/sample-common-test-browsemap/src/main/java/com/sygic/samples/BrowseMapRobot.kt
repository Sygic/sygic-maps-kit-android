package com.sygic.samples

import android.view.InputDevice
import android.view.MotionEvent
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.GeneralClickAction
import androidx.test.espresso.action.GeneralLocation
import androidx.test.espresso.action.Press
import androidx.test.espresso.action.Tap
import androidx.test.espresso.action.ViewActions.actionWithAssertions
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.sygic.samples.idling.PoiDetailVisibilityIdlingResource
import org.hamcrest.core.AllOf.allOf

fun browseMap(commonSampleActivity: CommonSampleActivity, func: BrowseMapRobot.() -> Unit) =
    BrowseMapRobot(commonSampleActivity).apply { func() }

class BrowseMapRobot(private val activity: CommonSampleActivity) {

    private val browseMapFragmentId = com.sygic.modules.browsemap.R.id.browseMapFragment
    private val compassViewId = com.sygic.ui.view.compass.R.id.compassView
    private val positionLockFabId = com.sygic.ui.view.positionlockfab.R.id.positionLockFab
    private val zoomControlsMenuId = com.sygic.ui.view.zoomcontrols.R.id.zoomControlsMenu
    private val poiDetailContainerId = com.sygic.ui.view.poidetail.R.id.poiDetailContainer

    init {
        onView(withId(browseMapFragmentId)).check(matches(isDisplayed()))
    }

    fun isCompassDisplayed() {
        onView(withId(compassViewId)).check(matches(isDisplayed()))
    }

    fun isPositionLockFabDisplayed() {
        onView(withId(positionLockFabId)).check(matches(isDisplayed()))
    }

    fun isZoomControlsMenuDisplayed() {
        onView(allOf(withId(zoomControlsMenuId), withParent(withId(browseMapFragmentId)))).check(matches(isDisplayed()))
    }

    fun clickOnMap() {
        onView(withId(browseMapFragmentId)).perform(
            actionWithAssertions(
                GeneralClickAction(
                    Tap.SINGLE,
                    GeneralLocation.CENTER,
                    Press.FINGER,
                    InputDevice.SOURCE_TOUCHSCREEN,
                    MotionEvent.BUTTON_PRIMARY
                )
            )
        )
    }

    fun isPoiDetailHidden() {
        PoiDetailVisibilityIdlingResource(activity, BottomSheetBehavior.STATE_HIDDEN).let {
            IdlingRegistry.getInstance().register(it)

            onView(withId(poiDetailContainerId)).check(doesNotExist())

            IdlingRegistry.getInstance().unregister(it)
        }
    }

    fun isPoiDetailVisible() {
        PoiDetailVisibilityIdlingResource(activity, BottomSheetBehavior.STATE_COLLAPSED).let {
            IdlingRegistry.getInstance().register(it)

            onView(withId(poiDetailContainerId)).check(matches(isDisplayed()))

            IdlingRegistry.getInstance().unregister(it)
        }
    }
}