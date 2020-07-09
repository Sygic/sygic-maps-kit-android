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

package com.sygic.maps.offlinemaps.installed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.sygic.maps.offlinemaps.R
import com.sygic.maps.offlinemaps.adapter.CountryListAdapter
import com.sygic.maps.offlinemaps.base.NavigationFragment
import com.sygic.maps.offlinemaps.continents.ContinentsFragment
import com.sygic.maps.offlinemaps.databinding.FragmentInstalledMapsBinding
import com.sygic.maps.offlinemaps.extensions.navigateTo
import com.sygic.maps.offlinemaps.loader.Country
import com.sygic.maps.offlinemaps.loader.MapLoaderGlobal
import com.sygic.maps.offlinemaps.regions.RegionsFragment
import com.sygic.maps.uikit.views.common.extensions.longToast
import kotlinx.android.synthetic.main.fragment_installed_maps.*

class InstalledMapsFragment : NavigationFragment<InstalledMapsViewModel>() {
    object Flipper {
        const val MAP_LIST = 0
        const val NO_MAPS_VIEW = 1
    }

    override val viewModel by viewModels<InstalledMapsViewModel>()

    private val mapListAdapter: CountryListAdapter
        get() = installedMapList.adapter as CountryListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentInstalledMapsBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        binding.adapter = CountryListAdapter()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.navigateToMapsDownload.observe(viewLifecycleOwner, Observer {
            navigateToContinentsFragment()
        })

        viewModel.navigateToInstalledRegions.observe(viewLifecycleOwner, Observer {
            if (it.details.regions.isNotEmpty()) {
                navigateToInstalledRegions(it)
            }
        })

        viewModel.showDetectCurrentCountryDialog.observe(viewLifecycleOwner, Observer {
            showDetectCountryDialog()
        })

        viewModel.detectedCountryObservable.observe(viewLifecycleOwner, Observer {
            requireContext().longToast(getString(R.string.current_detected_country, it.country.details.name))
        })

        viewModel.installedCountriesObservable.observe(viewLifecycleOwner, Observer {
            mapListAdapter.submitList(it)
            progressIndicator.visibility = View.GONE

            if (it.isEmpty()) {
                listFlipper.displayedChild = Flipper.NO_MAPS_VIEW
            } else {
                listFlipper.displayedChild = Flipper.MAP_LIST
            }
        })

        viewModel.updatesAvailableObservable.observe(viewLifecycleOwner, Observer {
            mapListAdapter.refreshList()
            if (it.isNotEmpty()) {
                requireContext().longToast(getString(R.string.available_updates, it.size))
            } else {
                requireContext().longToast(getString(R.string.no_available_updates))
            }
        })

        viewModel.notifyMapChangedObservable.observe(viewLifecycleOwner, Observer {
            mapListAdapter.updateStatus(it.first, it.second)
        })

        viewModel.loadInstalledMaps()
    }

    private fun navigateToContinentsFragment() {
        navigateTo(ContinentsFragment.newInstance())
    }

    private fun navigateToInstalledRegions(country: Country) {
        val args = Bundle().apply {
            putString(RegionsFragment.COUNTRY_KEY, country.iso)
            putBoolean(RegionsFragment.INSTALLED_KEY, true)
        }
        navigateTo<RegionsFragment>(args)
    }

    private fun showDetectCountryDialog() {
        val inputTextView = LayoutInflater.from(requireContext()).inflate(R.layout.input_text_dialog, view as ViewGroup, false)
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.dialog_detect_country_title)
            .setMessage(R.string.dialog_detect_country_message)
            .setView(inputTextView)
            .setPositiveButton(R.string.dialog_detect_country_positive_button) { _, _ ->
                val editText = inputTextView.findViewById<EditText>(R.id.dialog_input_text)
                MapLoaderGlobal.launchInScope {
                    MapLoaderGlobal.detectCountry(editText.text.toString())
                }
            }.show()
    }

    companion object {
        fun newInstance() = InstalledMapsFragment()
    }
}
