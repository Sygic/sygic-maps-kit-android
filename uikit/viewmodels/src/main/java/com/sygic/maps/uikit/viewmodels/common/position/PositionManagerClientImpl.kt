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
import androidx.lifecycle.Observer
import com.sygic.maps.uikit.viewmodels.common.initialization.InitializationManager
import com.sygic.maps.uikit.viewmodels.common.initialization.InitializationState
import com.sygic.maps.uikit.viewmodels.common.navigation.preview.RouteDemonstrationManager
import com.sygic.maps.uikit.views.common.utils.SingletonHolder
import com.sygic.sdk.InitializationCallback
import com.sygic.sdk.context.SygicContext
import com.sygic.sdk.position.GeoPosition
import com.sygic.sdk.position.PositionManager
import com.sygic.sdk.position.PositionManagerProvider

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
class PositionManagerClientImpl private constructor(
    private val routeDemonstrationManager: RouteDemonstrationManager
) : PositionManagerClient {

    companion object : SingletonHolder<PositionManagerClientImpl>() {
        @JvmStatic
        fun getInstance(manager: RouteDemonstrationManager) = getInstance { PositionManagerClientImpl(manager) }
    }

    @InitializationState
    override var initializationState = InitializationState.INITIALIZATION_NOT_STARTED
    private val callbacks = LinkedHashSet<InitializationManager.Callback>()

    private lateinit var positionManager: PositionManager

    override fun initialize(callback: InitializationManager.Callback?) {
        synchronized(this) {
            if (initializationState == InitializationState.INITIALIZED) {
                callback?.onInitialized()
                return
            }

            callback?.let { callbacks.add(it) }

            if (initializationState == InitializationState.INITIALIZING) {
                return
            }

            initializationState = InitializationState.INITIALIZING
        }

        PositionManagerProvider.getInstance(object : InitializationCallback<PositionManager> {
            override fun onInstance(positionManager: PositionManager) {
                synchronized(this) {
                    this@PositionManagerClientImpl.positionManager = positionManager
                    initializationState = InitializationState.INITIALIZED
                }
                with(callbacks) {
                    forEach { it.onInitialized() }
                    clear()
                }
            }
            override fun onError(@SygicContext.OnInitListener.Result result: Int) {
                synchronized(this) { initializationState = InitializationState.ERROR }
                with(callbacks) {
                    forEach { it.onError(result) }
                    clear()
                }
            }
        })
    }

    override val currentPosition = object : LiveData<GeoPosition>() {

        private val positionChangeListener = PositionManager.PositionChangeListener { value = it }
        private val demonstrationPositionObserver = Observer<GeoPosition> { value = it } //ToDo: remove it when CI-531 is done

        override fun onActive() {
            onReady { positionManager.addPositionChangeListener(positionChangeListener) }
            routeDemonstrationManager.currentPosition.observeForever(demonstrationPositionObserver)
        }

        override fun onInactive() {
            onReady { positionManager.removePositionChangeListener(positionChangeListener) }
            routeDemonstrationManager.currentPosition.removeObserver(demonstrationPositionObserver)
        }
    }

    override fun enableRemotePositioningService() = onReady { positionManager.enableRemotePositioningService() }

    override fun disableRemotePositioningService() = onReady { positionManager.disableRemotePositioningService() }

    override fun getLastKnownPosition(callback: (GeoPosition) -> Unit) = onReady { callback.invoke(positionManager.lastKnownPosition) }

    override fun setSdkPositionUpdatingEnabled(enabled: Boolean) =
        onReady { positionManager.run { if (enabled) startPositionUpdating() else stopPositionUpdating() } }
}
