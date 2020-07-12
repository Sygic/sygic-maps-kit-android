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

package com.sygic.maps.offlinemaps.regions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.sygic.maps.offlinemaps.adapter.RegionListAdapter
import com.sygic.maps.offlinemaps.adapter.viewholder.CountryEntryViewHolder
import com.sygic.maps.offlinemaps.base.NavigationFragment
import com.sygic.maps.offlinemaps.databinding.FragmentRegionsBinding
import com.sygic.sdk.map.MapLoader
import kotlinx.android.synthetic.main.fragment_regions.*

class RegionsFragment : NavigationFragment<RegionsViewModel>() {
    override val viewModel by viewModels<RegionsViewModel>()

    private val regionsAdapter: RegionListAdapter
        get() = regionList.adapter as RegionListAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentRegionsBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.adapter = RegionListAdapter()
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initArguments(requireArguments())

        val countryViewHolder = CountryEntryViewHolder(countryLayout, false)
        countryViewHolder.setPrimaryActionClickListener {
            viewModel.onCountryPrimaryActionClicked()
        }

        viewModel.countryObservable.observe(viewLifecycleOwner, Observer { countries ->
            val countryHolder = countries[viewModel.country]!!
            viewModel.countryHolder = countryHolder
            countryViewHolder.bind(countryHolder)
            progressIndicator.visibility = View.GONE
        })

        viewModel.regionObservable.observe(viewLifecycleOwner, Observer { regions ->
            viewModel.countryHolder?.let { holder ->
                val regionList = holder.country.details.regions.map { regions[it]!! }.filter {
                    if (viewModel.installed) {
                        it.status == MapLoader.MapStatus.Loaded || it.status == MapLoader.MapStatus.Installed
                    } else {
                        true
                    }
                }
                regionsAdapter.submitList(regionList)
            }
        })

        viewModel.notifyMapChangedObservable.observe(viewLifecycleOwner, Observer {
            if (it.first == viewModel.country) {
                countryViewHolder.updateFromStatus(it.second, 0)
            } else {
                regionsAdapter.updateStatus(it.first, it.second)
            }
        })

        viewModel.mapInstallProgressObservable.observe(viewLifecycleOwner, Observer {
            if (it.first == viewModel.country) {
                countryViewHolder.updateProgress(it.second)
            } else {
                regionsAdapter.updateProgress(it.first, it.second)
            }
        })

        viewModel.loadMaps()
    }

    private fun initArguments(arguments: Bundle) {
        viewModel.country = arguments.getString(COUNTRY_KEY) ?: throw IllegalArgumentException("Must pass country ISO in arguments")
        viewModel.installed = arguments.getBoolean(INSTALLED_KEY, false)
    }

    companion object {
        const val COUNTRY_KEY = "country"
        const val INSTALLED_KEY = "installed"
    }
}
