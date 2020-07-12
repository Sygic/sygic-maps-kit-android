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

import com.sygic.maps.offlinemaps.base.NavigationViewModel
import com.sygic.maps.offlinemaps.extensions.RegionAdapterHandler
import com.sygic.maps.offlinemaps.loader.CountryHolder
import com.sygic.maps.offlinemaps.loader.MapLoaderGlobal
import com.sygic.maps.offlinemaps.loader.RegionHolder

class RegionsViewModel : NavigationViewModel(), RegionAdapterHandler {
    var country = ""
    var installed = false

    var countryHolder: CountryHolder? = null

    val countryObservable by lazy(LazyThreadSafetyMode.NONE) {
        if (installed) { MapLoaderGlobal.installedCountriesObservable } else { MapLoaderGlobal.countriesObservable }
    }
    val regionObservable by lazy(LazyThreadSafetyMode.NONE) {
        if (installed) {
            MapLoaderGlobal.installedRegionsObservable
        } else {
            MapLoaderGlobal.regionsObservable
        }
    }
    val notifyMapChangedObservable = MapLoaderGlobal.notifyMapChangedObservable
    val mapInstallProgressObservable = MapLoaderGlobal.mapInstallProgressObservable

    fun loadMaps() = MapLoaderGlobal.launchInScope {
        if (installed) {
            MapLoaderGlobal.loadInstalledMaps()
        } else {
            MapLoaderGlobal.loadAllMaps()
        }
    }

    fun onCountryPrimaryActionClicked() {
        MapLoaderGlobal.launchInScope {
            MapLoaderGlobal.handlePrimaryMapAction(MapLoaderGlobal.countriesObservable.value!![country]!!.iso)
        }
    }

    override fun onPrimaryButtonClicked(region: RegionHolder) {
        MapLoaderGlobal.launchInScope {
            MapLoaderGlobal.handlePrimaryMapAction(region.iso)
        }
    }

    override fun onRegionClicked(region: RegionHolder) {}
    override fun onUpdateButtonClicked(region: RegionHolder) {}
    override fun onLoadButtonClicked(region: RegionHolder) {}
}
