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

package com.sygic.maps.uikit.viewmodels.searchtoolbar.component

import androidx.annotation.RestrictTo
import com.sygic.maps.uikit.viewmodels.common.search.MAX_RESULTS_COUNT_DEFAULT_VALUE
import com.sygic.maps.uikit.views.common.extensions.EMPTY_STRING
import com.sygic.sdk.position.GeoCoordinates

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
class SearchToolbarInitComponent {

    var initialSearchInput: String = EMPTY_STRING
    var initialSearchLocation: GeoCoordinates? = null
    var maxResultsCount: Int = MAX_RESULTS_COUNT_DEFAULT_VALUE

    fun recycle() {
        initialSearchInput = EMPTY_STRING
        initialSearchLocation = null
        maxResultsCount = MAX_RESULTS_COUNT_DEFAULT_VALUE
    }
}