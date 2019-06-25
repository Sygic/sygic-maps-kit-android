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

package com.sygic.maps.module.navigation

import android.os.Bundle
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sygic.maps.module.common.MapFragmentWrapper
import com.sygic.maps.module.navigation.databinding.LayoutNavigationBinding
import com.sygic.maps.module.navigation.di.DaggerNavigationComponent
import com.sygic.maps.module.navigation.di.NavigationComponent
import com.sygic.maps.module.navigation.viewmodel.NavigationFragmentViewModel
import com.sygic.maps.uikit.views.common.extensions.getParcelableValue
import com.sygic.sdk.route.RouteInfo

const val NAVIGATION_FRAGMENT_TAG = "navigation_map_fragment_tag"
internal const val KEY_ROUTE_INFO = "route_info"

/**
 * A *[NavigationFragment]* TODO
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
class NavigationFragment : MapFragmentWrapper<NavigationFragmentViewModel>() {

    override lateinit var fragmentViewModel: NavigationFragmentViewModel

    override fun executeInjector() =
        injector<NavigationComponent, NavigationComponent.Builder>(DaggerNavigationComponent.builder()) { it.inject(this) }

    /**
     * A *[routeInfo]* TODO
     */
    var routeInfo: RouteInfo?
        get() = if (::fragmentViewModel.isInitialized) {
            fragmentViewModel.routeInfo.value
        } else arguments.getParcelableValue(KEY_ROUTE_INFO)
        set(value) {
            arguments = Bundle(arguments).apply { putParcelable(KEY_ROUTE_INFO, value) }
            if (::fragmentViewModel.isInitialized) {
                fragmentViewModel.routeInfo.value = value
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fragmentViewModel = viewModelOf(NavigationFragmentViewModel::class.java, arguments).apply {
            //todo: Observables here :)
        }

        lifecycle.addObserver(fragmentViewModel)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = LayoutNavigationBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.navigationFragmentViewModel = fragmentViewModel
        val root = binding.root as ViewGroup
        super.onCreateView(inflater, root, savedInstanceState)?.let {
            root.addView(it, 0)
        }
        return root
    }

    override fun onDestroy() {
        super.onDestroy()

        lifecycle.removeObserver(fragmentViewModel)
    }

    override fun resolveAttributes(attributes: AttributeSet) {
        with(requireContext().obtainStyledAttributes(attributes, R.styleable.NavigationFragment)) { //ToDo: resolve attributes here
            /*if (hasValue(R.styleable.BrowseMapFragment_sygic_map_selectionMode)) {
                mapSelectionMode =
                    getInt(
                        R.styleable.BrowseMapFragment_sygic_map_selectionMode,
                        MAP_SELECTION_MODE_DEFAULT_VALUE
                    )
            }*/

            recycle()
        }
    }
}
