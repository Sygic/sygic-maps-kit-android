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

package com.sygic.maps.uikit.viewmodels.navigation.preview

import androidx.lifecycle.*
import com.sygic.maps.tools.annotations.AutoFactory
import com.sygic.maps.uikit.viewmodels.common.navigation.preview.RouteDemonstrationManager
import com.sygic.maps.uikit.viewmodels.common.navigation.preview.state.DemonstrationState
import com.sygic.maps.uikit.views.navigation.preview.RoutePreviewControls
import com.sygic.maps.uikit.views.navigation.preview.state.PlayPauseButtonState

/**
 * A [RoutePreviewControlsViewModel] is a basic ViewModel implementation for the [RoutePreviewControls] class. It listens
 * to the [RouteDemonstrationManager.demonstrationState] and set appropriate state to the [RoutePreviewControls] view.
 * It also listens to the [RoutePreviewControls] user interaction callbacks and control the [RouteDemonstrationManager].
 */
@AutoFactory
@Suppress("unused", "MemberVisibilityCanBePrivate")
open class RoutePreviewControlsViewModel internal constructor(
    private val routeDemonstrationManager: RouteDemonstrationManager
) : ViewModel(), RoutePreviewControls.OnPlayPauseStateChangedListener {

    val playPauseButtonState: MutableLiveData<PlayPauseButtonState> = MutableLiveData(PlayPauseButtonState.PLAY)

    private val demonstrationStateObserver = Observer<DemonstrationState> { state ->
        playPauseButtonState.value =
            if (state == DemonstrationState.ACTIVE) PlayPauseButtonState.PAUSE else PlayPauseButtonState.PLAY
    }

    init {
        routeDemonstrationManager.demonstrationState.observeForever(demonstrationStateObserver)
    }

    override fun onPlayPauseButtonStateChanged(state: PlayPauseButtonState) {
        when (state) {
            PlayPauseButtonState.PLAY -> routeDemonstrationManager.pause()
            PlayPauseButtonState.PAUSE -> {
                if (routeDemonstrationManager.demonstrationState.value == DemonstrationState.PAUSED) {
                    routeDemonstrationManager.unPause()
                } else {
                    routeDemonstrationManager.restart()
                }
            }
        }
    }

    open fun onSpeedButtonClick() {
        routeDemonstrationManager.speedMultiplier.value = routeDemonstrationManager.speedMultiplier.value!! * 2 % 15
    }

    open fun onStopButtonClick() {
        routeDemonstrationManager.stop()
    }

    override fun onCleared() {
        super.onCleared()

        routeDemonstrationManager.demonstrationState.removeObserver(demonstrationStateObserver)
        routeDemonstrationManager.destroy()
    }
}