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

package com.sygic.samples.base

import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.RootMatchers.withDecorView
import androidx.test.espresso.matcher.ViewMatchers.*
import com.sygic.samples.app.activities.CommonSampleActivity
import com.sygic.samples.base.matchers.withDrawable
import org.hamcrest.Matchers.instanceOf
import org.hamcrest.Matchers.not
import org.hamcrest.core.AllOf.allOf
import org.junit.Assert.assertEquals

@Suppress("MemberVisibilityCanBePrivate", "unused")
abstract class BaseRobot(private val activity: CommonSampleActivity, @IdRes private val parentViewId: Int) {

    fun clickOnView(@IdRes viewId: Int) {
        onView(withId(viewId)).perform(click())
    }

    fun clickOnFirstRecyclerViewItem(@IdRes recyclerViewId: Int) = clickOnRecyclerViewItemAtPosition(recyclerViewId, 0)

    fun clickOnRecyclerViewItemAtPosition(@IdRes recyclerViewId: Int, position: Int) {
        onView(withId(recyclerViewId)).perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(position, click()))
    }

    fun isViewDisplayed(@IdRes viewId: Int) {
        onView(allOf(withId(viewId), withParent(withId(parentViewId)))).check(matches(isDisplayed()))
    }

    fun isViewDisplayed(viewClass: Class<*>) {
        onView(allOf(instanceOf(viewClass), withParent(withId(parentViewId)))).check(matches(isDisplayed()))
    }

    fun isViewNotDisplayed(@IdRes viewId: Int) {
        onView(allOf(withId(viewId), withParent(withId(parentViewId)))).check(matches(not(isDisplayed())))
    }

    fun isViewNotDisplayed(viewClass: Class<*>) {
        onView(allOf(instanceOf(viewClass), withParent(withId(parentViewId)))).check(matches(not(isDisplayed())))
    }

    fun isToastVisible() {
        onView(withId(android.R.id.message)).inRoot(withDecorView(not(activity.window.decorView)))
            .check(matches(isDisplayed()))
    }

    fun viewContainsText(@IdRes viewId: Int, @StringRes text: Int) {
        viewContainsText(viewId, activity.getString(text))
    }

    fun viewContainsText(@IdRes viewId: Int, text: String) {
        onView(withId(viewId)).check(matches(withText(text)))
    }

    fun enterText(@IdRes viewId: Int, text: String) {
        onView(withId(viewId)).perform(typeText(text))
    }

    fun enterTextAndCloseKeyboard(@IdRes viewId: Int, text: String) {
        onView(withId(viewId)).perform(typeText(text), closeSoftKeyboard())
    }

    fun pressImeActionButton(@IdRes viewId: Int) {
        onView(withId(viewId)).perform(pressImeActionButton())
    }

    fun imageViewContainsDrawable(@IdRes viewId: Int, @DrawableRes drawableId: Int, @ColorInt tintColor: Int? = null) {
        onView(withId(viewId)).check(matches(withDrawable(drawableId, tintColor)))
    }

    fun isActivityFinishing() {
        assertEquals(true, activity.isFinishing)
    }
}