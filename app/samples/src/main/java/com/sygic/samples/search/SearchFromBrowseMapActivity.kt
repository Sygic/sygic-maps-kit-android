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

package com.sygic.samples.search

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.sygic.maps.module.browsemap.BROWSE_MAP_FRAGMENT_TAG
import com.sygic.maps.module.browsemap.BrowseMapFragment
import com.sygic.maps.module.common.provider.ModuleConnectionProvider
import com.sygic.maps.uikit.views.common.extensions.longToast
import com.sygic.samples.R
import com.sygic.samples.app.activities.CommonSampleActivity
import com.sygic.samples.search.components.BrowseMapFragmentInitComponent
import com.sygic.samples.search.viewmodels.SearchFromBrowseMapActivityViewModel

class SearchFromBrowseMapActivity : CommonSampleActivity() {

    override val wikiModulePath: String = "Module-Search#search---from-browse-map"

    private lateinit var viewModel: SearchFromBrowseMapActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_search_from_browse_map)

        viewModel = ViewModelProviders.of(this).get(SearchFromBrowseMapActivityViewModel::class.java).apply {
            this.placeBrowseMapFragmentObservable.observe(
                this@SearchFromBrowseMapActivity,
                Observer<BrowseMapFragmentInitComponent> { placeBrowseMapFragment(it) })
            this.moduleConnectionObservable.observe(
                this@SearchFromBrowseMapActivity,
                Observer<ModuleConnectionProvider> { setFragmentModuleConnection(it) })
            this.showToastObservable.observe(
                this@SearchFromBrowseMapActivity,
                Observer<String> { longToast(it) })
        }
        lifecycle.addObserver(viewModel)
    }

    // Note: You can also create this Fragment just like in other examples directly in an XML layout file, but
    // performance or other issues may occur (https://stackoverflow.com/a/14810676/3796931).
    private fun placeBrowseMapFragment(component: BrowseMapFragmentInitComponent) = supportFragmentManager
        ?.beginTransaction()
        ?.replace(R.id.fragmentContainer, BrowseMapFragment().apply {
            cameraDataModel.zoomLevel = component.zoomLevel
            cameraDataModel.position = component.position
        }, BROWSE_MAP_FRAGMENT_TAG)
        ?.commitNow() //ToDo: MS-5686

    //ToDo: MS-5686
    private fun setFragmentModuleConnection(moduleConnectionProvider: ModuleConnectionProvider) =
        (supportFragmentManager.findFragmentByTag(BROWSE_MAP_FRAGMENT_TAG) as BrowseMapFragment)
            .setSearchConnectionProvider(moduleConnectionProvider)

    override fun onDestroy() {
        super.onDestroy()

        lifecycle.removeObserver(viewModel)
    }
}
