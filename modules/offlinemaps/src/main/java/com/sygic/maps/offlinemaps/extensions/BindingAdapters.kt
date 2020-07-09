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

package com.sygic.maps.offlinemaps.extensions

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sygic.maps.offlinemaps.adapter.ContinentsAdapter
import com.sygic.maps.offlinemaps.adapter.CountryListAdapter
import com.sygic.maps.offlinemaps.adapter.RegionListAdapter
import com.sygic.maps.offlinemaps.loader.Continent
import com.sygic.maps.offlinemaps.loader.CountryHolder
import com.sygic.maps.offlinemaps.loader.RegionHolder

interface CountryAdapterHandler {
    fun onCountryClicked(country: CountryHolder)
    fun onPrimaryButtonClicked(country: CountryHolder)
    fun onUpdateButtonClicked(country: CountryHolder)
    fun onLoadButtonClicked(country: CountryHolder)
}

interface RegionAdapterHandler {
    fun onRegionClicked(region: RegionHolder)
    fun onPrimaryButtonClicked(region: RegionHolder)
    fun onUpdateButtonClicked(region: RegionHolder)
    fun onLoadButtonClicked(region: RegionHolder)
}

interface ContinentsAdapterHandler {
    fun onContinentSelected(continent: Continent)
}

@BindingAdapter("countryAdapter", "countryHandler", requireAll = false)
fun setCountryAdapterHandler(view: RecyclerView, adapter: CountryListAdapter, handler: CountryAdapterHandler) {
    view.adapter = adapter
    adapter.setOnItemClicked(handler::onCountryClicked)
    adapter.setOnPrimaryActionClicked(handler::onPrimaryButtonClicked)
    adapter.setOnUpdateButtonClicked(handler::onUpdateButtonClicked)
    adapter.setOnLoadButtonClicked(handler::onLoadButtonClicked)
}

@BindingAdapter("regionAdapter", "regionHandler", requireAll = false)
fun setRegionAdapterHandler(view: RecyclerView, adapter: RegionListAdapter, handler: RegionAdapterHandler) {
    view.adapter = adapter
    adapter.setOnItemClicked(handler::onRegionClicked)
    adapter.setOnPrimaryActionClicked(handler::onPrimaryButtonClicked)
    adapter.setOnUpdateButtonClicked(handler::onUpdateButtonClicked)
    adapter.setOnLoadButtonClicked(handler::onLoadButtonClicked)
}

@BindingAdapter("continentsAdapter", "continentsHandler", requireAll = false)
fun setContinentsAdapterHandler(view: RecyclerView, adapter: ContinentsAdapter, handler: ContinentsAdapterHandler) {
    view.adapter = adapter
    adapter.setOnContinentClickListener(handler::onContinentSelected)
}
