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

package com.sygic.samples.app.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.sygic.maps.uikit.views.common.extensions.openActivity
import com.sygic.samples.R
import com.sygic.samples.app.models.Sample
import com.sygic.samples.app.viewmodels.SamplesListViewModel

abstract class BaseSamplesListFragment : Fragment() {

    @get:StringRes
    protected abstract val title: Int
    protected abstract val items: List<Sample>

    private lateinit var samplesListViewModel: SamplesListViewModel
    private val toolbar: Toolbar? by lazy { activity?.findViewById<Toolbar>(R.id.toolbar) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        toolbar?.setTitle(title)
        samplesListViewModel =
                ViewModelProviders.of(this, SamplesListViewModel.Factory(items)).get(SamplesListViewModel::class.java).apply {
                    this.startActivityObservable.observe(
                        this@BaseSamplesListFragment,
                        Observer<Class<out AppCompatActivity>> { requireContext().openActivity(it) })
                }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding: LayoutSamplesListBinding = LayoutSamplesListBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.samplesListViewModel = samplesListViewModel
        return binding.root
    }
}