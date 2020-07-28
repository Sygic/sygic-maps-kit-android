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

package com.sygic.maps.offlinemaps.continents

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.sygic.maps.offlinemaps.adapter.ContinentsAdapter
import com.sygic.maps.offlinemaps.base.NavigationFragment
import com.sygic.maps.offlinemaps.databinding.FragmentContinentsBinding
import com.sygic.maps.offlinemaps.download.DownloadMapsFragment
import com.sygic.maps.offlinemaps.extensions.navigateTo
import kotlinx.android.synthetic.main.fragment_continents.*

class ContinentsFragment : NavigationFragment<ContinentsViewModel>() {
    override val viewModel: ContinentsViewModel by viewModels()

    private val continentsAdapter: ContinentsAdapter
        get() = continentList.adapter as ContinentsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentContinentsBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.adapter = ContinentsAdapter()
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.continentsObservable.observe(viewLifecycleOwner, Observer {
            continentsAdapter.submitList(it)
            progressIndicator.visibility = View.GONE
        })

        viewModel.continentSelected.observe(viewLifecycleOwner, Observer {
            navigateToDownloadMapsFragment(it)
        })
    }

    private fun navigateToDownloadMapsFragment(continent: String) {
        val args = Bundle().apply {
            putString(DownloadMapsFragment.CONTINENT_KEY, continent)
        }
        navigateTo<DownloadMapsFragment>(args)
    }

    companion object {
        fun newInstance() = ContinentsFragment()
    }
}
