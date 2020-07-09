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

package com.sygic.maps.offlinemaps.loader

import androidx.lifecycle.MutableLiveData
import com.sygic.maps.uikit.views.common.livedata.SingleLiveEvent
import com.sygic.maps.uikit.views.common.utils.logError
import com.sygic.maps.uikit.views.common.utils.logWarning
import com.sygic.sdk.map.MapLoader
import com.sygic.sdk.map.listeners.MapInstallProgressListener
import com.sygic.sdk.map.listeners.MapResumedInstallDoneListener
import kotlinx.coroutines.*
import java.util.concurrent.Executors
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

object MapLoaderGlobal {
    private val mapLoaderDispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
    private val scope = CoroutineScope(mapLoaderDispatcher + SupervisorJob())

    val continentsObservable = MutableLiveData<MutableMap<String, MutableList<String>>>()
    val countriesObservable = MutableLiveData<MutableMap<String, CountryHolder>>()
    val regionsObservable = MutableLiveData<MutableMap<String, RegionHolder>>()

    val installedCountriesObservable = MutableLiveData<MutableMap<String, CountryHolder>>()
    val installedRegionsObservable = MutableLiveData<MutableMap<String, RegionHolder>>()
    val updatesAvailableObservable = MutableLiveData<List<String>>()
    val detectedCountryObservable = SingleLiveEvent<CountryHolder>()

    val notifyMapChangedObservable = SingleLiveEvent<Pair<String, MapLoader.MapStatus>>()
    val mapInstallProgressObservable = MutableLiveData<Pair<String, Int>>()

    val mapLoaderExceptionObservable = SingleLiveEvent<String>()

    private val mapInstallProgressListener = MapInstallProgressListener { iso, downloadedBytes, totalSize ->
        val progress = downloadedBytes / totalSize.toFloat()
        val progressInt = (progress * 100).toInt()
        updateMapsProgress(iso, progressInt)
        mapInstallProgressObservable.value = iso to progressInt
    }

    private val mapResumedInstallDoneListener = MapResumedInstallDoneListener { iso, result ->
        if (result != MapLoader.LoadResult.Success) {
            logError("Failed resumed install done for $iso with result $result")
        }
        scope.launch {
            val status = MapLoaderWrapper.getMapStatus(iso)
            notifyMapChangedObservable.postValue(iso to status)
        }
    }

    init {
        scope.launch {
            MapLoaderWrapper.getMapLoader().addMapProgressInstallListener(mapInstallProgressListener)
            MapLoaderWrapper.getMapLoader().addMapResumedInstallDoneListener(mapResumedInstallDoneListener)
            MapLoaderWrapper.getMapLoader().resumePendingInstallations()
            loadInstalledMaps()
            loadAllMaps()
        }
    }

    fun launchInScope(
        context: CoroutineContext = EmptyCoroutineContext,
        start: CoroutineStart = CoroutineStart.DEFAULT,
        block: suspend CoroutineScope.() -> Unit
    ) = scope.launch(context, start, block)

    suspend fun loadAllMaps() {
        val continents = HashMap<String, MutableList<String>>()
        val countries = HashMap<String, CountryHolder>()
        val regions = HashMap<String, RegionHolder>()
        try {
            MapLoaderWrapper.getAvailableCountries(false).forEach { iso ->
                val details = MapLoaderWrapper.getCountryDetails(iso, false)
                if (!continents.containsKey(details.continentName)) {
                    continents[details.continentName] = mutableListOf()
                }
                continents[details.continentName]!! += iso
                countries[iso] = CountryHolder(Country(iso, details), MapLoaderWrapper.getMapStatus(iso))
                details.regions.forEach {
                    regions[it] = RegionHolder(
                        Region(it, MapLoaderWrapper.getRegionDetails(it, false)),
                        MapLoaderWrapper.getMapStatus(it)
                    )
                }
            }
        } catch (exception: MapLoadResultException) {
            logMapLoaderError("Cannot get available countries: ${exception.result}")
        }
        continentsObservable.postValue(continents)
        countriesObservable.postValue(countries)
        regionsObservable.postValue(regions)
    }

    suspend fun loadInstalledMaps() {
        val countries = HashMap<String, CountryHolder>()
        val regions = HashMap<String, RegionHolder>()
        try {
            MapLoaderWrapper.getAvailableCountries(true).forEach { iso ->
                val details = MapLoaderWrapper.getCountryDetails(iso, true)
                countries[iso] = CountryHolder(Country(iso, details), MapLoaderWrapper.getMapStatus(iso))
                details.regions.forEach {
                    regions[it] = RegionHolder(Region(iso, MapLoaderWrapper.getRegionDetails(it, true)), MapLoaderWrapper.getMapStatus(it))
                }
            }
        } catch (exception: MapLoadResultException) {
            logMapLoaderError("Cannot get installed countries: ${exception.result}")
        }
        installedCountriesObservable.postValue(countries)
        installedRegionsObservable.postValue(regions)
    }

    suspend fun handlePrimaryMapAction(iso: String) {
        when (val status = MapLoaderWrapper.getMapStatus(iso)) {
            MapLoader.MapStatus.NotInstalled, MapLoader.MapStatus.PartiallyInstalled -> {
                installMap(iso)
            }
            MapLoader.MapStatus.Installed, MapLoader.MapStatus.Loaded -> {
                uninstallMap(iso)
            }
            else -> logWarning("Primary button clicked but map is in state ($status)")
        }
        loadInstalledMaps()
        loadAllMaps()
    }

    suspend fun installMap(iso: String) {
        coroutineScope {
            val installStarted = CompletableDeferred<Unit>(coroutineContext[Job])
            launch {
                installStarted.await()
                updateStatus(iso)
            }
            try {
                MapLoaderWrapper.installMap(iso, installStarted)
            } catch (exception: MapLoadResultException) {
                logMapLoaderError("Cannot install map $iso: ${exception.result}")
            }
        }
        updateStatus(iso)
    }

    suspend fun uninstallMap(iso: String) {
        coroutineScope {
            val uninstallStarted = CompletableDeferred<Unit>(coroutineContext[Job])
            launch {
                uninstallStarted.await()
                updateStatus(iso)
            }
            try {
                MapLoaderWrapper.uninstallMap(iso, uninstallStarted)
            } catch (exception: MapLoadResultException) {
                logMapLoaderError("Cannot uninstall map $iso: ${exception.result}")
            }
        }
        updateStatus(iso)
    }

    suspend fun updateMap(iso: String) {
        coroutineScope {
            val updateStarted = CompletableDeferred<Unit>(coroutineContext[Job])
            launch {
                updateStarted.await()
                updateStatus(iso)
            }
            try {
                MapLoaderWrapper.updateMap(iso, updateStarted)
            } catch (exception: MapLoadResultException) {
                logMapLoaderError("Cannot update map $iso: ${exception.result}")
            }
        }
    }

    suspend fun checkForUpdates() {
        try {
            val isos = MapLoaderWrapper.checkForUpdates()
            isos.forEach {
                updateUpdateable(it)
            }
            updatesAvailableObservable.value = isos
        } catch (exception: MapLoadResultException) {
            logMapLoaderError("Cannot check for updates: ${exception.result}")
        }
    }

    suspend fun detectCountry(iso: String = "") {
        try {
            val detectedIso = MapLoaderWrapper.detectCountry(iso)
            detectedCountryObservable.postValue(CountryHolder(
                Country(detectedIso, MapLoaderWrapper.getCountryDetails(detectedIso, false)),
                MapLoaderWrapper.getMapStatus(detectedIso)
            ))
        } catch (exception: MapLoadResultException) {
            logMapLoaderError("Cannot detect country: ${exception.result}")
        }
    }

    suspend fun handleLoadAction(iso: String) {
        when (val status = MapLoaderWrapper.getMapStatus(iso)) {
            MapLoader.MapStatus.Installed -> {
                val result = MapLoaderWrapper.loadMap(iso)
                if (result != MapLoader.LoadResult.Success) {
                    logError("Cannot load map $iso: $result")
                }
            }
            MapLoader.MapStatus.Loaded -> {
                val result = MapLoaderWrapper.unloadMap(iso)
                if (result != MapLoader.LoadResult.Success) {
                    logError("Cannot load map $iso: $result")
                }
            }
            else -> logWarning("Cannot handle load action as map is in state ($status)")
        }
        updateStatus(iso)
        loadInstalledMaps()
        loadAllMaps()
    }

    private suspend fun updateStatus(iso: String): MapLoader.MapStatus {
        val status = MapLoaderWrapper.getMapStatus(iso)
        updateMapsStatus(iso, status)
        notifyMapChangedObservable.postValue(iso to status)
        return status
    }

    private fun updateMapsStatus(iso: String, status: MapLoader.MapStatus) {
        countriesObservable.value?.get(iso)?.status = status
        regionsObservable.value?.get(iso)?.status = status
        installedCountriesObservable.value?.get(iso)?.status = status
        installedRegionsObservable.value?.get(iso)?.status = status
    }

    private fun updateMapsProgress(iso: String, progress: Int) {
        countriesObservable.value?.get(iso)?.progress = progress
        regionsObservable.value?.get(iso)?.progress = progress
        installedCountriesObservable.value?.get(iso)?.progress = progress
        installedRegionsObservable.value?.get(iso)?.progress = progress
    }

    private fun updateUpdateable(iso: String) {
        installedCountriesObservable.value?.get(iso)?.updateAvailable = true
        installedRegionsObservable.value?.get(iso)?.updateAvailable = true
    }

    private fun logMapLoaderError(message: String) {
        logError(message)
        mapLoaderExceptionObservable.postValue(message)
    }
}
