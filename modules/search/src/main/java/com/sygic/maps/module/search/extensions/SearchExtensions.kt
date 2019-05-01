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

package com.sygic.maps.module.search.extensions

import android.annotation.SuppressLint
import android.util.AttributeSet
import com.sygic.maps.module.search.R
import com.sygic.maps.module.search.SearchFragment
import com.sygic.maps.uikit.viewmodels.common.search.MAX_RESULTS_COUNT_DEFAULT_VALUE
import com.sygic.maps.uikit.viewmodels.searchtoolbar.component.SearchToolbarInitComponent
import com.sygic.maps.uikit.views.common.extensions.EMPTY_STRING
import com.sygic.sdk.position.GeoCoordinates

@SuppressLint("Recycle")
fun SearchFragment.resolveAttributes(
    attributes: AttributeSet?,
    searchToolbarInitComponent: SearchToolbarInitComponent
) {
    activity?.application?.obtainStyledAttributes(attributes, R.styleable.SearchFragment)?.let { typedArray ->
        if (typedArray.hasValue(R.styleable.SearchFragment_sygic_initial_search_input)) {
            searchToolbarInitComponent.initialSearchInput =
                typedArray.getString(R.styleable.SearchFragment_sygic_initial_search_input).let { it } ?: EMPTY_STRING
        }
        if (typedArray.hasValue(R.styleable.SearchFragment_sygic_initial_latitude)
            && typedArray.hasValue(R.styleable.SearchFragment_sygic_initial_longitude)
        ) {
            searchToolbarInitComponent.initialSearchLocation = GeoCoordinates(
                typedArray.getFloat(R.styleable.SearchFragment_sygic_initial_latitude, Float.NaN).toDouble(),
                typedArray.getFloat(R.styleable.SearchFragment_sygic_initial_longitude, Float.NaN).toDouble()
            )
        }
        if (typedArray.hasValue(R.styleable.SearchFragment_sygic_max_results_count)) {
            searchToolbarInitComponent.maxResultsCount =
                typedArray.getInt(R.styleable.SearchFragment_sygic_max_results_count, MAX_RESULTS_COUNT_DEFAULT_VALUE)
        }

        typedArray.recycle()
    }
}