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

package com.sygic.maps.uikit.viewmodels.navigation

import androidx.lifecycle.MutableLiveData
import com.sygic.maps.tools.annotations.AutoFactory
import com.sygic.maps.uikit.viewmodels.common.extensions.pictogramDrawableRes
import com.sygic.maps.uikit.viewmodels.common.extensions.roadSigns
import com.sygic.maps.uikit.viewmodels.common.regional.RegionalManager
import com.sygic.maps.uikit.views.navigation.roadsign.data.RoadSignData
import com.sygic.sdk.navigation.NavigationManager
import com.sygic.sdk.navigation.warnings.NaviSignInfo

/**
 * A [FullSignpostViewModel] TODO
 */
@AutoFactory
@Suppress("unused", "MemberVisibilityCanBePrivate")
open class FullSignpostViewModel internal constructor(
    regionalManager: RegionalManager,
    navigationManager: NavigationManager
) : BaseSignpostViewModel(regionalManager, navigationManager) {

    val pictogram: MutableLiveData<Int> = MutableLiveData(0)
    val roadSigns: MutableLiveData<List<RoadSignData>> = MutableLiveData(listOf())

    override fun onNaviSignInfoOnRouteChanged(naviSignInfo: NaviSignInfo?) {
        super.onNaviSignInfoOnRouteChanged(naviSignInfo)

        roadSigns.value = naviSignInfo?.roadSigns() ?: listOf()
        pictogram.value = naviSignInfo?.pictogramDrawableRes() ?: 0
    }
}