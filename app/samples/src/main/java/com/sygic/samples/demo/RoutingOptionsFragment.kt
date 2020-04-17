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

package com.sygic.samples.demo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.sygic.maps.uikit.views.common.extensions.*
import com.sygic.maps.uikit.views.recyclerview.ItemView
import com.sygic.samples.R
import com.sygic.samples.databinding.LayoutRoutingOptionsBinding
import com.sygic.samples.demo.viewmodels.ComplexDemoActivityViewModel
import com.sygic.samples.demo.viewmodels.RoutingOptionsViewModel
import com.sygic.samples.utils.toPersistableString
import com.sygic.samples.utils.toRestriction
import kotlinx.android.synthetic.main.layout_routing_options.*

const val ROUTING_OPTIONS_FRAGMENT_TAG = "routing_options_fragment_tag"

class RoutingOptionsFragment : Fragment() {
    private lateinit var binding: LayoutRoutingOptionsBinding
    private lateinit var viewModel: RoutingOptionsViewModel
    private lateinit var activityViewModel: ComplexDemoActivityViewModel
    private var initialized = false

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = LayoutRoutingOptionsBinding.inflate(inflater)
        viewModel = ViewModelProviders.of(this)[RoutingOptionsViewModel::class.java]
        activityViewModel = ViewModelProviders.of(requireActivity())[ComplexDemoActivityViewModel::class.java]
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (activity as? AppCompatActivity)?.setSupportActionBar(view.findViewById(R.id.toolbar))
        (activity as? AppCompatActivity)?.supportActionBar?.setDisplayHomeAsUpEnabled(true)

        viewModel.routingOptionsChanged.observe(this, Observer {
            initializeFromRoutingOptions()
        })

        viewModel.dimensionalRestrictionsSelection.observe(this, Observer {
            onRestrictionSelected(it)
        })

        addRestrictionButton.setOnClickListener {
            restrictionsDropDown.dropDownTextView.selectedValue?.let {
                restrictionsRecyclerView.itemAdapter.addItem(ItemView(it, 0))
                addRestrictionButton.disable()
            }
        }

        restrictionsRecyclerView.itemAdapter.setOnItemRemoved {
            if (restrictionsDropDown.dropDownTextView.selectedValue == it.name) {
                addRestrictionButton.enable()
            }
        }

        initialized = false
    }

    override fun onResume() {
        super.onResume()
        if (!initialized) {
            initialized = true
            initializeFromRoutingOptions()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> fragmentManager?.popBackStack()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        persistRoutingOptions()
        activityViewModel.routingOptions = viewModel.persistentRoutingOptions.createRoutingOptions()
    }

    private fun onRestrictionSelected(position: Int) {
        val selectedName = restrictionsDropDown.dropDownTextView.getItem(position)
        if (restrictionsRecyclerView.itemAdapter.items.any { it.name == selectedName }) {
            addRestrictionButton.disable()
        } else {
            addRestrictionButton.enable()
        }
    }

    private fun initializeFromRoutingOptions() {
        viewModel.persistentRoutingOptions.apply {
            tollRoadAvoidedSwitch.isChecked = isTollRoadAvoided
            highwayAvoidedSwitch.isChecked = isHighwayAvoided
            routingServiceDropDown.dropDownTextView.selectedIndex = routingService
            transportModeDropDown.dropDownTextView.selectedIndex = transportMode
            hazardousMaterialClassDropDown.dropDownTextView.selectedIndex = hazardousMaterialClass
            routingTypeDropDown.dropDownTextView.selectedIndex = routingType
            tunnelRestrictionDropDown.dropDownTextView.selectedIndex = tunnelRestriction
            vehicleFuelTypeDropDown.dropDownTextView.selectedIndex = vehicleFuelType
            restrictionsRecyclerView.itemAdapter.items = dimensionalRestrictions.toItemViews()
        }

        onRestrictionSelected(restrictionsDropDown.dropDownTextView.selectedPosition)
    }

    private fun persistRoutingOptions() {
        viewModel.persistentRoutingOptions.apply {
            isTollRoadAvoided = tollRoadAvoidedSwitch.isChecked
            isHighwayAvoided = highwayAvoidedSwitch.isChecked
            routingService = routingServiceDropDown.dropDownTextView.selectedIndex
            transportMode = transportModeDropDown.dropDownTextView.selectedIndex
            hazardousMaterialClass = hazardousMaterialClassDropDown.dropDownTextView.selectedIndex
            routingType = routingTypeDropDown.dropDownTextView.selectedIndex
            tunnelRestriction = tunnelRestrictionDropDown.dropDownTextView.selectedIndex
            vehicleFuelType = vehicleFuelTypeDropDown.dropDownTextView.selectedIndex
            dimensionalRestrictions = restrictionsRecyclerView.itemAdapter.items.toStringSet()
        }
    }

    private fun Set<String>.toItemViews(): List<ItemView> = map { value ->
        val restriction = value.toRestriction()
        val name = restrictionsDropDown.dropDownTextView.getItem(restriction.first)
        ItemView(name, restriction.second)
    }

    private fun List<ItemView>.toStringSet(): Set<String> = map {
        val index = restrictionsDropDown.dropDownTextView.findIndexForName(it.name)
        (index to it.value).toPersistableString()
    }.toSet()
}
