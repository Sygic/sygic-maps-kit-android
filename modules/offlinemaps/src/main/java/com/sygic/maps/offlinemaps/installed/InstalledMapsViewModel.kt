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

import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Transformations
import com.sygic.maps.offlinemaps.R
import com.sygic.maps.offlinemaps.base.NavigationViewModel
import com.sygic.maps.offlinemaps.extensions.CountryAdapterHandler
import com.sygic.maps.offlinemaps.loader.Country
import com.sygic.maps.offlinemaps.loader.CountryHolder
import com.sygic.maps.offlinemaps.loader.MapLoaderGlobal
import com.sygic.maps.uikit.views.common.livedata.SingleLiveEvent

class InstalledMapsViewModel : NavigationViewModel(), Toolbar.OnMenuItemClickListener, CountryAdapterHandler {
    val navigateToMapsDownload = SingleLiveEvent<Unit>()
    val navigateToInstalledRegions = SingleLiveEvent<Country>()
    val showDetectCurrentCountryDialog = SingleLiveEvent<Unit>()
    val installedCountriesObservable = Transformations.map(MapLoaderGlobal.installedCountriesObservable) { it.values.toList() }
    val updatesAvailableObservable = MapLoaderGlobal.updatesAvailableObservable
    val detectedCountryObservable = MapLoaderGlobal.detectedCountryObservable
    val notifyMapChangedObservable = MapLoaderGlobal.notifyMapChangedObservable

    fun loadInstalledMaps() = MapLoaderGlobal.launchInScope {
        MapLoaderGlobal.loadInstalledMaps()
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.detectCurrentCountry -> {
                showDetectCurrentCountryDialog.call()
                true
            }
            R.id.checkForUpdates -> {
                MapLoaderGlobal.launchInScope {
                    MapLoaderGlobal.checkForUpdates()
                }
                true
            }
            else -> false
        }
    }

    override fun onCountryClicked(country: CountryHolder) {
        navigateToInstalledRegions.value = country.country
    }

    override fun onPrimaryButtonClicked(country: CountryHolder) {
        MapLoaderGlobal.launchInScope {
            MapLoaderGlobal.handlePrimaryMapAction(country.iso)
        }
    }

    override fun onUpdateButtonClicked(country: CountryHolder) {
        MapLoaderGlobal.launchInScope {
            MapLoaderGlobal.updateMap(country.iso)
        }
    }

    override fun onLoadButtonClicked(country: CountryHolder) {
        MapLoaderGlobal.launchInScope {
            MapLoaderGlobal.handleLoadAction(country.iso)
        }
    }

    fun onDownloadMapsClicked() {
        navigateToMapsDownload.call()
    }
}
