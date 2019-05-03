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

package com.sygic.samples.base.idling

import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.sygic.maps.uikit.views.poidetail.PoiDetailBottomDialogFragment
import com.sygic.samples.app.activities.CommonSampleActivity

class PoiDetailVisibilityIdlingResource(
    activity: CommonSampleActivity,
    @BottomSheetBehavior.State private val expectedBottomSheetState: Int
) : BaseIdlingResource(activity) {

    private val poiDetailBottomDialogFragment
        get() = activity.supportFragmentManager?.findFragmentByTag(PoiDetailBottomDialogFragment.TAG)

    override fun getName(): String = "PoiDetailVisibilityIdlingResource"

    override fun isIdle(): Boolean {
        when (expectedBottomSheetState) {
            BottomSheetBehavior.STATE_HIDDEN -> return poiDetailBottomDialogFragment == null
            else -> poiDetailBottomDialogFragment?.let { fragment ->
                return ((fragment as PoiDetailBottomDialogFragment).currentState == expectedBottomSheetState)
            }
        }

        return false
    }
}