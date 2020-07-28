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

import androidx.lifecycle.map
import com.sygic.maps.module.common.maploader.Country
import com.sygic.maps.module.common.maploader.MapItem
import com.sygic.maps.module.common.maploader.MapLoaderGlobal
import com.sygic.maps.offlinemaps.base.NavigationViewModel
import com.sygic.maps.offlinemaps.extensions.MapAdapterHandler
import com.sygic.maps.offlinemaps.extensions.toMb
import com.sygic.maps.uikit.views.common.livedata.SingleLiveEvent
import com.sygic.sdk.map.CountryDetails

class DownloadMapsViewModel : NavigationViewModel(), MapAdapterHandler {
    var continent = ""

    val navigateToCountryRegions = SingleLiveEvent<Country>()
    val continentsObservable = MapLoaderGlobal.continents.map { continents ->
        continents[continent]!!.map {
            val country = MapLoaderGlobal.getCountry(it)
            MapItem(it, country.country.details.name, mapDetailsText(country.country.details), country.data)
        }
    }
    val notifyMapChangedObservable = MapLoaderGlobal.notifyMapChangedObservable
    val mapInstallProgressObservable = MapLoaderGlobal.mapInstallProgress

    fun loadAllMaps() = MapLoaderGlobal.launchInScope {
        MapLoaderGlobal.loadAllMaps()
    }

    override fun onMapClicked(iso: String) {
        val country = MapLoaderGlobal.getCountry(iso).country
        if (country.details.regions.isNotEmpty()) {
            navigateToCountryRegions.value = country
        }
    }

    override fun onPrimaryButtonClicked(iso: String) {
        MapLoaderGlobal.launchInScope {
            MapLoaderGlobal.handlePrimaryMapAction(iso)
        }
    }

    override fun onLoadButtonClicked(iso: String) {
        MapLoaderGlobal.launchInScope {
            MapLoaderGlobal.handleLoadAction(iso)
        }
    }

    override fun onUpdateButtonClicked(iso: String) {
        // Not used
    }

    private fun mapDetailsText(details: CountryDetails) =
        if (details.regions.isEmpty()) {
            "${details.totalSize.toMb} MB"
        } else {
            "${details.totalSize.toMb} MB • Contains ${details.regions.size} regions"
        }
}
