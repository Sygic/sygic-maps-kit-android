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

package com.sygic.maps.module.common.maploader

import androidx.lifecycle.LiveData
import com.sygic.maps.uikit.views.common.livedata.SingleLiveEvent
import com.sygic.sdk.map.MapLoader
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

interface ApplicationMapLoader {
    val dispatcher: CoroutineDispatcher

    val continents: LiveData<MutableMap<String, MutableList<String>>>
    val countries: LiveData<MutableMap<String, CountryHolder>>
    val regions: LiveData<MutableMap<String, RegionHolder>>

    val installedCountries: LiveData<MutableMap<String, CountryHolder>>
    val installedRegions: LiveData<MutableMap<String, RegionHolder>>

    val updatesAvailableObservable: SingleLiveEvent<List<String>>
    val detectedCountryObservable: SingleLiveEvent<CountryHolder>

    val notifyMapChangedObservable: SingleLiveEvent<Pair<String, MapLoader.MapStatus>>
    val mapInstallProgress: LiveData<Pair<String, Int>>

    val mapLoaderExceptionObservable: SingleLiveEvent<String>

    fun getCountry(iso: String): CountryHolder
    fun getRegion(iso: String): RegionHolder
    fun getInstalledCountry(iso: String): CountryHolder
    fun forEachRegionInCountry(iso: String, action: (String) -> Unit)
    fun launchInScope(
        context: CoroutineContext = EmptyCoroutineContext,
        start: CoroutineStart = CoroutineStart.DEFAULT,
        block: suspend CoroutineScope.() -> Unit
    ): Job

    suspend fun loadAllMaps()
    suspend fun loadInstalledMaps()
    suspend fun handlePrimaryMapAction(iso: String)
    suspend fun cancelInstallation(iso: String)
    suspend fun installMap(iso: String)
    suspend fun uninstallMap(iso: String)
    suspend fun updateMap(iso: String)
    suspend fun checkForUpdates()
    suspend fun detectCountry(iso: String = "")
    suspend fun handleLoadAction(iso: String)
}
