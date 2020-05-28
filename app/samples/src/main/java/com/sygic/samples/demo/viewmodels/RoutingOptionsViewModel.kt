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

package com.sygic.samples.demo.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.sygic.maps.uikit.views.common.extensions.asMutable
import com.sygic.maps.uikit.views.recyclerview.ItemView
import com.sygic.maps.uikit.views.recyclerview.SimpleEditAdapter
import com.sygic.samples.R
import com.sygic.samples.demo.RoutingOptionsManager
import com.sygic.samples.utils.toRestriction

class RoutingOptionsViewModel(
    application: Application,
    private val routingOptionsManager: RoutingOptionsManager
) : AndroidViewModel(application) {
    val addRestrictionButtonEnabled: LiveData<Boolean> = MutableLiveData(true)

    val tollRoadAvoided: MutableLiveData<Boolean> = MutableLiveData(routingOptionsManager.isTollRoadAvoided)
    val highwayAvoided: MutableLiveData<Boolean> = MutableLiveData(routingOptionsManager.isHighwayAvoided)
    val routingService: MutableLiveData<Int> = MutableLiveData(routingOptionsManager.routingService)
    val transportMode: MutableLiveData<Int> = MutableLiveData(routingOptionsManager.transportMode)
    val hazardousMaterialClass: MutableLiveData<Int> = MutableLiveData(routingOptionsManager.hazardousMaterialClass)
    val routingType: MutableLiveData<Int> = MutableLiveData(routingOptionsManager.routingType)
    val tunnelRestriction: MutableLiveData<Int> = MutableLiveData(routingOptionsManager.tunnelRestriction)
    val vehicleFuelType: MutableLiveData<Int> = MutableLiveData(routingOptionsManager.vehicleFuelType)
    val restriction: MutableLiveData<Int> = MutableLiveData(0)

    val restrictionDropDownAdapter = MutableLiveData<Array<String>>(application.resources.getStringArray(R.array.dimensional_restriction_values))

    val restrictionsAdapter = SimpleEditAdapter()

    private val onRestrictionSelected = Observer<Int> { position ->
        val selectedName = restrictionDropDownAdapter.value!![position]
        addRestrictionButtonEnabled.asMutable().value = !restrictionsAdapter.items.any {
            it.name == selectedName
        }
    }

    init {
        initializeFromRoutingOptionsManager()
        restrictionsAdapter.setOnItemRemoved {
            if (restrictionDropDownAdapter.value!![restriction.value!!] == it.name) {
                addRestrictionButtonEnabled.asMutable().value = true
            }
        }
        restriction.observeForever(onRestrictionSelected)
    }

    override fun onCleared() {
        restriction.removeObserver(onRestrictionSelected)
        persistRoutingOptions()
    }

    fun resetToDefaults() {
        routingOptionsManager.resetToDefaults()
        initializeFromRoutingOptionsManager()
    }

    fun onAddRestrictionButtonClicked() {
        restrictionsAdapter.addItem(ItemView(restrictionDropDownAdapter.value!![restriction.value!!], 0))
        addRestrictionButtonEnabled.asMutable().value = false
    }

    private fun initializeFromRoutingOptionsManager() {
        tollRoadAvoided.value = routingOptionsManager.isTollRoadAvoided
        highwayAvoided.value = routingOptionsManager.isHighwayAvoided
        routingService.value = routingOptionsManager.routingService
        transportMode.value = routingOptionsManager.transportMode
        hazardousMaterialClass.value = routingOptionsManager.hazardousMaterialClass
        routingType.value = routingOptionsManager.routingType
        tunnelRestriction.value = routingOptionsManager.tunnelRestriction
        vehicleFuelType.value = routingOptionsManager.vehicleFuelType
        restrictionsAdapter.items = routingOptionsManager.dimensionalRestrictions.toItemViews()

        onRestrictionSelected.onChanged(restriction.value)
    }

    private fun persistRoutingOptions() {
        routingOptionsManager.isTollRoadAvoided = tollRoadAvoided.value!!
        routingOptionsManager.isHighwayAvoided = highwayAvoided.value!!
        routingOptionsManager.routingService = routingService.value!!
        routingOptionsManager.transportMode = transportMode.value!!
        routingOptionsManager.hazardousMaterialClass = hazardousMaterialClass.value!!
        routingOptionsManager.routingType = routingType.value!!
        routingOptionsManager.tunnelRestriction = tunnelRestriction.value!!
        routingOptionsManager.vehicleFuelType = vehicleFuelType.value!!
        routingOptionsManager.dimensionalRestrictions = restrictionsAdapter.items.toStringSet()
    }

    private fun Set<String>.toItemViews(): List<ItemView> = map { value ->
        val restriction = value.toRestriction()
        val name = restrictionDropDownAdapter.value!![restriction.first]
        ItemView(name, restriction.second)
    }

    private fun List<ItemView>.toStringSet(): Set<String> = map { itemView ->
        val index = restrictionDropDownAdapter.value!!.indexOfFirst { it == itemView.name }
        (index to itemView.value).toPersistableString()
    }.toSet()

    private fun Pair<Int, Int>.toPersistableString() = "$first:$second"
}