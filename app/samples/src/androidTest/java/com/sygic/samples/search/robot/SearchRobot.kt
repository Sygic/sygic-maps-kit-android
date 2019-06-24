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

package com.sygic.samples.search.robot

import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.sygic.samples.R
import com.sygic.samples.app.activities.CommonSampleActivity
import com.sygic.samples.base.BaseRobot
import com.sygic.samples.base.idling.MapPinsIdlingResource
import com.sygic.samples.base.idling.SearchResultListDataReceivedIdlingResource

fun search(commonSampleActivity: CommonSampleActivity, func: SearchRobot.() -> Unit) =
    SearchRobot(commonSampleActivity).apply { func() }

@Suppress("MemberVisibilityCanBePrivate", "unused")
class SearchRobot(private val activity: CommonSampleActivity) : BaseRobot(activity, R.id.searchFragment) {

    fun enterText(text: String) = enterText(R.id.searchToolbarInputEditText, text)

    fun enterTextAndCloseKeyboard(text: String) = enterTextAndCloseKeyboard(R.id.searchToolbarInputEditText, text)

    fun pressSearchImeActionButton() = pressImeActionButton(R.id.searchToolbarInputEditText)

    fun clickOnFirstRecyclerViewItem() = clickOnFirstRecyclerViewItem(R.id.searchResultListRecyclerView)

    fun clickOnRecyclerViewItemAtPosition(position: Int) =
        clickOnRecyclerViewItemAtPosition(R.id.searchResultListRecyclerView, position)

    fun containsSearchResultListItemWithText(string: String) {
        SearchResultListDataReceivedIdlingResource(activity).let {
            IdlingRegistry.getInstance().register(it)
            Espresso.onView(ViewMatchers.withText(string)).check(matches(ViewMatchers.isDisplayed()))
            IdlingRegistry.getInstance().unregister(it)
        }
    }

    fun containsMapPins() {
        MapPinsIdlingResource(activity).let {
            IdlingRegistry.getInstance().register(it)
            onView(withId(R.id.browseMapFragment)).check(matches(ViewMatchers.isDisplayed()))
            IdlingRegistry.getInstance().unregister(it)
        }
    }
}