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

package com.sygic.samples.navigation.robot

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.sygic.samples.R
import com.sygic.samples.app.activities.CommonSampleActivity
import com.sygic.samples.base.BaseRobot
import com.sygic.samples.base.idling.ActionMenuVisibilityIdlingResource

fun navigation(commonSampleActivity: CommonSampleActivity, func: NavigationRobot.() -> Unit) =
    NavigationRobot(commonSampleActivity).apply { func() }

@Suppress("MemberVisibilityCanBePrivate", "unused")
class NavigationRobot(private val activity: CommonSampleActivity) : BaseRobot(activity, R.id.navigationFragment) {

    fun isActionMenuHidden() {
        ActionMenuVisibilityIdlingResource(activity, BottomSheetBehavior.STATE_HIDDEN).let {
            IdlingRegistry.getInstance().register(it)
            onView(withId(R.id.actionMenuContainer)).check(doesNotExist())
            IdlingRegistry.getInstance().unregister(it)
        }
    }

    fun isActionMenuVisible() {
        ActionMenuVisibilityIdlingResource(activity, BottomSheetBehavior.STATE_EXPANDED).let {
            IdlingRegistry.getInstance().register(it)
            onView(withId(R.id.actionMenuContainer)).check(matches(isDisplayed()))
            IdlingRegistry.getInstance().unregister(it)
        }
    }
}