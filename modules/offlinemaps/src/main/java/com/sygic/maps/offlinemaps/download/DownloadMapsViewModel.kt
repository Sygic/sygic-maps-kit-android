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

import androidx.lifecycle.Transformations
import com.sygic.maps.offlinemaps.base.NavigationViewModel
import com.sygic.maps.offlinemaps.extensions.CountryAdapterHandler
import com.sygic.maps.offlinemaps.loader.Country
import com.sygic.maps.offlinemaps.loader.CountryHolder
import com.sygic.maps.offlinemaps.loader.MapLoaderGlobal
import com.sygic.maps.uikit.views.common.livedata.SingleLiveEvent

class DownloadMapsViewModel : NavigationViewModel(), CountryAdapterHandler {
    var continent = ""

    val navigateToCountryRegions = SingleLiveEvent<Country>()
    val continentsObservable = Transformations.map(MapLoaderGlobal.continentsObservable) { continents -> continents[continent]!!.map { MapLoaderGlobal.countriesObservable.value!![it]!! } }
    val notifyMapChangedObservable = MapLoaderGlobal.notifyMapChangedObservable
    val mapInstallProgressObservable = MapLoaderGlobal.mapInstallProgressObservable

    fun loadAllMaps() = MapLoaderGlobal.launchInScope {
        MapLoaderGlobal.loadAllMaps()
    }

    override fun onCountryClicked(country: CountryHolder) {
        if (country.country.details.regions.isNotEmpty()) {
            navigateToCountryRegions.value = country.country
        }
    }

    override fun onPrimaryButtonClicked(country: CountryHolder) {
        MapLoaderGlobal.launchInScope {
            MapLoaderGlobal.handlePrimaryMapAction(country.iso)
        }
    }

    override fun onLoadButtonClicked(country: CountryHolder) {
        MapLoaderGlobal.launchInScope {
            MapLoaderGlobal.handleLoadAction(country.iso)
        }
    }

    override fun onUpdateButtonClicked(country: CountryHolder) {
        // Not used
    }
}
