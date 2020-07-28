/*
 * Copyright (c) 2020 Sygic a.s. All rights reserved.
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

package com.sygic.maps.offlinemaps.download

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.sygic.maps.offlinemaps.adapter.MapListAdapter
import com.sygic.maps.offlinemaps.base.NavigationFragment
import com.sygic.maps.offlinemaps.databinding.FragmentDownloadMapsBinding
import com.sygic.maps.offlinemaps.extensions.navigateTo
import com.sygic.maps.offlinemaps.regions.RegionsFragment
import kotlinx.android.synthetic.main.fragment_download_maps.*

class DownloadMapsFragment : NavigationFragment<DownloadMapsViewModel>() {
    override val viewModel by viewModels<DownloadMapsViewModel>()

    private val mapListAdapter: MapListAdapter
        get() = mapList.adapter as MapListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentDownloadMapsBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.adapter = MapListAdapter()
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initArguments(requireArguments())

        viewModel.continentsObservable.observe(viewLifecycleOwner, Observer { countries ->
            mapListAdapter.submitList(countries)
            progressIndicator.visibility = View.GONE
        })

        viewModel.notifyMapChangedObservable.observe(viewLifecycleOwner, Observer {
            mapListAdapter.updateStatus(it.first, it.second)
        })

        viewModel.mapInstallProgressObservable.observe(viewLifecycleOwner, Observer {
            mapListAdapter.updateProgress(it.first, it.second)
        })

        viewModel.navigateToCountryRegions.observe(viewLifecycleOwner, Observer {
            navigateToRegionsFragment(it.iso)
        })

        viewModel.loadAllMaps()
    }

    private fun initArguments(arguments: Bundle) {
        viewModel.continent = arguments.getString(CONTINENT_KEY) ?: throw IllegalArgumentException("Must specify continent in arguments")
    }

    private fun navigateToRegionsFragment(countryIso: String) {
        val args = Bundle().apply {
            putString(RegionsFragment.COUNTRY_KEY, countryIso)
        }
        navigateTo<RegionsFragment>(args)
    }

    companion object {
        const val CONTINENT_KEY = "continent"
    }
}
