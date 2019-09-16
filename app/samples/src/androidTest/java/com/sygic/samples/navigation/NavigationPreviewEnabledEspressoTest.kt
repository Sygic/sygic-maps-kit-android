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

package com.sygic.samples.navigation

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.sygic.maps.uikit.views.navigation.signpost.FullSignpostView
import com.sygic.samples.R
import com.sygic.samples.base.BaseTest
import com.sygic.samples.navigation.robot.navigation
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NavigationPreviewEnabledEspressoTest : BaseTest(NavigationPreviewEnabledActivity::class.java) {

    @Test
    fun navigationFragmentDisplayed() {
        navigation(activity) {
            isViewDisplayed(FullSignpostView::class.java)
            isViewDisplayed(R.id.infobar)
            isViewDisplayed(R.id.currentSpeedView)
            isViewDisplayed(R.id.routePreviewControls)
        }
    }

    @Test
    fun onRoutePreviewControlsClick() {
        navigation(activity) {
            isViewDisplayed(R.id.routePreviewControls)
            clickOnView(R.id.routePreviewControlsPlayPauseButton)
            clickOnView(R.id.routePreviewControlsSpeedButton)
            clickOnView(R.id.routePreviewControlsStopButton)
        }
    }

    @Test
    fun clickOnInfobar_rightButtonClick() {
        navigation(activity) {
            isViewDisplayed(R.id.infobar)
            clickOnView(R.id.infobarRightButton)
            isActivityFinishing()
        }
    }

    @Test
    fun clickOnInfobar_leftButtonClick() { //todo: waiting for PR: Feature - Navigation Part 4.2
        navigation(activity) {
            isViewDisplayed(R.id.infobar)
            clickOnView(R.id.infobarLeftButton)
        }
    }
}