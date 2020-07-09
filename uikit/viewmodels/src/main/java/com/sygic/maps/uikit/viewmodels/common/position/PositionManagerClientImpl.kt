/*
 * Copyright (c) 2019 Sygic a.s. All rights reserved.
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

package com.sygic.maps.uikit.viewmodels.common.position

import androidx.annotation.RestrictTo
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.Transformations
import com.sygic.maps.uikit.viewmodels.common.initialization.InitializationCallback
import com.sygic.maps.uikit.viewmodels.common.navigation.preview.RouteDemonstrationManagerClient
import com.sygic.maps.uikit.views.common.extensions.observeOnce
import com.sygic.maps.uikit.views.common.utils.SingletonHolder
import com.sygic.sdk.position.GeoCourse
import com.sygic.sdk.position.GeoPosition
import com.sygic.sdk.position.PositionManager
import com.sygic.sdk.position.PositionManagerProvider
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
class PositionManagerClientImpl private constructor(
    private val routeDemonstrationManagerClient: RouteDemonstrationManagerClient
) : PositionManagerClient {

    companion object : SingletonHolder<PositionManagerClientImpl>() {
        @JvmStatic
        fun getInstance(managerClient: RouteDemonstrationManagerClient) = getInstance { PositionManagerClientImpl(managerClient) }
    }

    private fun positionChangedListener(positionChanged: (GeoPosition) -> Unit) =
        object : PositionManager.PositionChangeListener {
            override fun onPositionChanged(geoPosition: GeoPosition) {
                positionChanged(geoPosition)
            }
            override fun onCourseChanged(geoCourse: GeoCourse) {}
        }

    private val managerProvider: LiveData<PositionManager> = object : MutableLiveData<PositionManager>() {
        init { PositionManagerProvider.getInstance(InitializationCallback<PositionManager> { value = it }) }
    }

    override val currentPosition by lazy {
        Transformations.switchMap<PositionManager, GeoPosition>(managerProvider) { manager ->
            object : LiveData<GeoPosition>() {

                private val positionChangeListener by lazy { positionChangedListener { value = it } }
                private val demonstrationPositionObserver by lazy { Observer<GeoPosition> { value = it } } //ToDo: remove it when CI-531 is done

                override fun onActive() {
                    manager.addPositionChangeListener(positionChangeListener)
                    routeDemonstrationManagerClient.currentPosition.observeForever(demonstrationPositionObserver)
                }

                override fun onInactive() {
                    manager.removePositionChangeListener(positionChangeListener)
                    routeDemonstrationManagerClient.currentPosition.removeObserver(demonstrationPositionObserver)
                }
            }
        }
    }

    override val lastKnownPosition by lazy {
        Transformations.switchMap<PositionManager, GeoPosition>(managerProvider) { manager ->
            object : LiveData<GeoPosition>() {

                private var fetchJob: Job? = null

                override fun onActive() {
                    fetchJob = GlobalScope.launch {
                        while (true) {
                            manager.lastKnownPosition.let { if (value != it) postValue(it) }
                            delay(1000)
                        }
                    }
                }

                override fun onInactive() {
                    fetchJob?.cancel()
                    fetchJob = null
                }
            }
        }
    }

    override val sdkPositionUpdatingEnabled by lazy { MutableLiveData<Boolean>(false) }

    override val remotePositioningServiceEnabled by lazy { MutableLiveData<Boolean>(false) }

    init {
        sdkPositionUpdatingEnabled.observeForever { enabled ->
            managerProvider.observeOnce { it.run { if (enabled) startPositionUpdating() else stopPositionUpdating() } }
        }

        remotePositioningServiceEnabled.observeForever { enabled ->
            managerProvider.observeOnce { it.run { if (enabled) enableRemotePositioningService() else disableRemotePositioningService() } }
        }
    }
}
