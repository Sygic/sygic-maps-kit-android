package com.sygic.samples

import android.graphics.Point
import android.view.InputDevice
import android.view.MotionEvent
import android.view.View
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.CoordinatesProvider
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
import com.sygic.sdk.map.MapFragment
import com.sygic.sdk.map.`object`.MapMarker
import org.hamcrest.core.AllOf.allOf
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.RootMatchers.withDecorView
import org.hamcrest.Matchers.not

fun browseMap(commonSampleActivity: CommonSampleActivity, func: BrowseMapRobot.() -> Unit) =
    BrowseMapRobot(commonSampleActivity).apply { func() }

@Suppress("MemberVisibilityCanBePrivate")
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

    fun clickOnMapToLocation(generalLocation: GeneralLocation) {
        onView(withId(browseMapFragmentId)).perform(
            actionWithAssertions(
                GeneralClickAction(
                    Tap.SINGLE,
                    generalLocation,
                    Press.FINGER,
                    InputDevice.SOURCE_TOUCHSCREEN,
                    MotionEvent.BUTTON_PRIMARY
                )
            )
        )
    }

    fun clickOnMapMarker(mapMarker: MapMarker) {
        // Small offset in Y axis need to be applied, because the SDK does not have any click tolerance
        getScreenPointFromMapMarker(mapMarker)?.let { clickOnMapToPoint(it.x, it.y - 1) }
    }

    fun clickOnMapToPoint(x: Int, y: Int) {
        onView(withId(browseMapFragmentId)).perform(
            actionWithAssertions(
                GeneralClickAction(
                    Tap.SINGLE,
                    object : CoordinatesProvider {
                        override fun calculateCoordinates(view: View): FloatArray {
                            val screenPos = IntArray(2)
                            view.getLocationOnScreen(screenPos)

                            val screenX = (screenPos[0] + x).toFloat()
                            val screenY = (screenPos[1] + y).toFloat()

                            return floatArrayOf(screenX, screenY)
                        }
                    },
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

    fun isToastVisible() {
        onView(withId(android.R.id.message)).inRoot(withDecorView(not(activity.window.decorView)))
            .check(matches(isDisplayed()))
    }

    private fun getScreenPointFromMapMarker(mapMarker: MapMarker): Point? {
        activity.supportFragmentManager.fragments.forEach { fragment ->
            if (fragment is MapFragment) {
                fragment.mapView?.let {
                    return it.screenPointsFromGeoCoordinates(listOf(mapMarker.position)).first()
                }
            }
        }

        return null
    }

}