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

package com.sygic.samples

import com.sygic.samples.browsemap.*
import com.sygic.samples.app.models.Sample
import com.sygic.samples.navigation.NavigationDefaultActivity
import com.sygic.samples.navigation.NavigationInfobarCustomClickListenerActivity
import com.sygic.samples.navigation.NavigationPreviewEnabledActivity
import com.sygic.samples.navigation.NavigationSimplifiedSignpostActivity
import com.sygic.samples.search.SearchDefaultActivity
import com.sygic.samples.search.SearchFromBrowseMapActivity
import com.sygic.samples.search.SearchFromBrowseMapWithPinsActivity
import com.sygic.samples.search.SearchPreFilledInputActivity

object Samples {

    val browseMapSampleList: List<Sample> = listOf(
        Sample(
            BrowseMapDefaultActivity::class.java, R.drawable.preview_browsemap_default,
            R.string.browse_map_default, R.string.browse_map_default_summary
        ),
        Sample(
            BrowseMapFullActivity::class.java, R.drawable.preview_browsemap_full,
            R.string.browse_map_full, R.string.browse_map_full_summary
        ),
        Sample(
            BrowseMapModesActivity::class.java, R.drawable.preview_browsemap_modes,
            R.string.browse_map_modes, R.string.browse_map_modes_summary
        ),
        Sample(
            BrowseMapMarkersActivity::class.java, R.drawable.preview_browsemap_markers,
            R.string.browse_map_markers, R.string.browse_map_markers_summary
        ),
        Sample(
            BrowseMapDetailsWindowActivity::class.java, R.drawable.preview_browsemap_details_window,
            R.string.browse_map_details_window, R.string.browse_map_details_window_summary
        ),
        Sample(
            BrowseMapClickListenerActivity::class.java, R.drawable.preview_browsemap_click_listener,
            R.string.browse_map_click_listener, R.string.browse_map_click_listener_summary
        ),
        Sample(
            BrowseMapThemesActivity::class.java, R.drawable.preview_browsemap_themes,
            R.string.browse_map_themes, R.string.browse_map_themes_summary
        )
    )

    val searchSampleList: List<Sample> = listOf(
        Sample(
            SearchDefaultActivity::class.java, R.drawable.preview_search_default,
            R.string.search_default, R.string.search_default_summary
        ),
        Sample(
            SearchFromBrowseMapActivity::class.java, R.drawable.preview_search_from_browse_map,
            R.string.search_from_browse_map, R.string.search_from_browse_map_summary
        ),
        Sample(
            SearchFromBrowseMapWithPinsActivity::class.java, R.drawable.preview_search_from_browse_map_pins,
            R.string.search_from_browse_map_pins, R.string.search_from_browse_map_pins_summary
        ),
        Sample(
            SearchPreFilledInputActivity::class.java, R.drawable.preview_search_pre_filled_input,
            R.string.search_pre_filled_input, R.string.search_pre_filled_input_summary
        )
    )

    val navigationSampleList: List<Sample> = listOf(
        Sample(
            NavigationDefaultActivity::class.java, R.drawable.preview_navigation_default,
            R.string.navigation_default, R.string.navigation_default_summary
        ),
        Sample(
            NavigationPreviewEnabledActivity::class.java, R.drawable.preview_navigation_preview_enabled,
            R.string.navigation_preview_enabled, R.string.navigation_preview_enabled_summary
        ),
        Sample(
            NavigationSimplifiedSignpostActivity::class.java, R.drawable.preview_navigation_simplified_signpost,
            R.string.navigation_simplified_signpost, R.string.navigation_simplified_signpost_summary
        ),
        Sample(
            NavigationInfobarCustomClickListenerActivity::class.java, R.drawable.preview_navigation_infobar_custom_click_listener,
            R.string.navigation_infobar_custom_click_listener, R.string.navigation_infobar_custom_click_listener_summary
        )
    )
}
