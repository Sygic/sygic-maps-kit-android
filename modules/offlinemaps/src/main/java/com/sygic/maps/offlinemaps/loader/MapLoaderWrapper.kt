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
import com.sygic.maps.uikit.views.common.extensions.observeOnce
import com.sygic.maps.uikit.views.common.utils.logError
import com.sygic.sdk.context.CoreInitCallback
import com.sygic.sdk.context.CoreInitException
import com.sygic.sdk.map.*
import com.sygic.sdk.map.listeners.MapListResultListener
import com.sygic.sdk.map.listeners.MapResultListener
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resumeWithException

class MapLoadResultException(val result: MapLoader.LoadResult) : RuntimeException("Error loading: $result")

object MapLoaderWrapper {
    val mapLoaderObservable = object : MutableLiveData<MapLoader>() {
        init {
            MapLoaderProvider.getInstance(object : CoreInitCallback<MapLoader> {
                override fun onInstance(instance: MapLoader) {
                    value = instance
                }

                override fun onError(exception: CoreInitException) {
                    logError("Cannot get map loader: $exception")
                }
            })
        }
    }

    suspend fun getMapLoader(): MapLoader = withContext(Dispatchers.Main) {
        suspendCancellableCoroutine<MapLoader> { cont ->
            mapLoaderObservable.observeOnce {
                cont.resumeWith(Result.success(it))
            }
        }
    }

    suspend fun getAvailableCountries(installed: Boolean): List<String> {
        val mapLoader = getMapLoader()
        return suspendCancellableCoroutine { cont ->
            val task = mapLoader.getAvailableCountries(installed, object : MapListResultListener {
                override fun onMapListResultSuccess(isos: List<String>) {
                    cont.resumeWith(Result.success(isos))
                }

                override fun onMapListResultError(result: MapLoader.LoadResult) {
                    cont.resumeWithException(MapLoadResultException(result))
                }
            })
            cont.invokeOnCancellation {
                task.cancel()
            }
        }
    }

    suspend fun getMapStatus(iso: String): MapLoader.MapStatus {
        return getMapLoader().getMapStatus(iso)
    }

    suspend fun getCountryDetails(iso: String, installed: Boolean): CountryDetails {
        return getMapLoader().getCountryDetails(iso, installed).getOrThrow()
    }

    suspend fun getRegionDetails(iso: String, installed: Boolean): RegionDetails {
        return getMapLoader().getRegionDetails(iso, installed).getOrThrow()
    }

    suspend fun installMap(iso: String, installStarted: CompletableDeferred<Unit>? = null): String {
        val mapLoader = getMapLoader()
        return suspendCancellableCoroutine { cont ->
            val task = mapLoader.installMap(iso, object : MapResultListener {
                override fun onMapResultSuccess(iso: String) {
                    cont.resumeWith(Result.success(iso))
                }

                override fun onMapResultError(result: MapLoader.LoadResult) {
                    cont.resumeWithException(MapLoadResultException(result))
                }
            })
            cont.invokeOnCancellation {
                task.cancel()
            }
            installStarted?.complete(Unit)
        }
    }

    suspend fun uninstallMap(iso: String, uninstallStarted: CompletableDeferred<Unit>? = null): String {
        val mapLoader = getMapLoader()
        return suspendCancellableCoroutine { cont ->
            val task = mapLoader.uninstallMap(iso, object : MapResultListener {
                override fun onMapResultSuccess(iso: String) {
                    cont.resumeWith(Result.success(iso))
                }

                override fun onMapResultError(result: MapLoader.LoadResult) {
                    cont.resumeWithException(MapLoadResultException(result))
                }
            })
            cont.invokeOnCancellation {
                task.cancel()
            }
            uninstallStarted?.complete(Unit)
        }
    }

    suspend fun updateMap(iso: String, updateStarted: CompletableDeferred<Unit>? = null): String {
        val mapLoader = getMapLoader()
        return suspendCancellableCoroutine { cont ->
            val task = mapLoader.updateMap(iso, object : MapResultListener {
                override fun onMapResultSuccess(iso: String) {
                    cont.resumeWith(Result.success(iso))
                }

                override fun onMapResultError(result: MapLoader.LoadResult) {
                    cont.resumeWithException(MapLoadResultException(result))
                }
            })
            cont.invokeOnCancellation {
                task.cancel()
            }
            updateStarted?.complete(Unit)
        }
    }

    suspend fun checkForUpdates(): List<String> {
        val mapLoader = getMapLoader()
        return suspendCancellableCoroutine { cont ->
            val task = mapLoader.checkForUpdates(object : MapListResultListener {
                override fun onMapListResultSuccess(isos: List<String>) {
                    cont.resumeWith(Result.success(isos))
                }

                override fun onMapListResultError(result: MapLoader.LoadResult) {
                    cont.resumeWithException(MapLoadResultException(result))
                }
            })
            cont.invokeOnCancellation {
                task.cancel()
            }
        }
    }

    suspend fun loadMap(iso: String): MapLoader.LoadResult {
        return getMapLoader().loadMap(iso)
    }

    suspend fun unloadMap(iso: String): MapLoader.LoadResult {
        return getMapLoader().unloadMap(iso)
    }

    suspend fun detectCountry(iso: String = ""): String {
        val mapLoader = getMapLoader()
        return suspendCancellableCoroutine { cont ->
            val task = mapLoader.detectCurrentCountry(iso, object : MapResultListener {
                override fun onMapResultSuccess(iso: String) {
                    cont.resumeWith(Result.success(iso))
                }

                override fun onMapResultError(result: MapLoader.LoadResult) {
                    cont.resumeWithException(MapLoadResultException(result))
                }
            })
            cont.invokeOnCancellation {
                task.cancel()
            }
        }
    }
}
