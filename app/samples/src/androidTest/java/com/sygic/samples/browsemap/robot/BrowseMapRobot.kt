/*
 * Copyright (c) 2019 Sygic a.s. All rights reserved.
 *
 * This project is licensed under the MIT License.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.sygic.samples.browsemap.robot

import android.graphics.Point
import android.view.InputDevice
import android.view.MotionEvent
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.*
import androidx.test.espresso.action.ViewActions.actionWithAssertions
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.sygic.samples.R
import com.sygic.samples.app.activities.CommonSampleActivity
import com.sygic.samples.base.BaseRobot
import com.sygic.samples.base.idling.PoiDetailVisibilityIdlingResource
import com.sygic.sdk.map.MapFragment
import com.sygic.sdk.map.`object`.MapMarker

fun browseMap(commonSampleActivity: CommonSampleActivity, func: BrowseMapRobot.() -> Unit) =
        BrowseMapRobot(commonSampleActivity).apply { func() }

@Suppress("MemberVisibilityCanBePrivate")
class BrowseMapRobot(private val activity: CommonSampleActivity) : BaseRobot(activity, R.id.browseMapFragment) {

    init {
        onView(withId(R.id.browseMapFragment)).check(matches(isDisplayed()))
    }

    fun clickOnMapToLocation(generalLocation: GeneralLocation) {
        onView(withId(R.id.browseMapFragment)).perform(
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
        getScreenPointFromMapMarker(mapMarker)?.let { clickOnMapAtPoint(it.x, it.y - 1) }
    }

    fun clickOnMapAtPoint(x: Int, y: Int) {
        onView(withId(R.id.browseMapFragment)).perform(
            actionWithAssertions(
                GeneralClickAction(
                    Tap.SINGLE,
                    CoordinatesProvider { view ->
                        val screenPos = IntArray(2)
                        view.getLocationOnScreen(screenPos)

                        val screenX = (screenPos[0] + x).toFloat()
                        val screenY = (screenPos[1] + y).toFloat()

                        floatArrayOf(screenX, screenY)
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
            onView(withId(R.id.poiDetailContainer)).check(doesNotExist())
            IdlingRegistry.getInstance().unregister(it)
        }
    }

    fun isPoiDetailVisible() {
        PoiDetailVisibilityIdlingResource(activity, BottomSheetBehavior.STATE_COLLAPSED).let {
            IdlingRegistry.getInstance().register(it)
            onView(withId(R.id.poiDetailContainer)).check(matches(isDisplayed()))
            IdlingRegistry.getInstance().unregister(it)
        }
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