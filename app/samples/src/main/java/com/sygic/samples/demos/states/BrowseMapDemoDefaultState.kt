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

package com.sygic.samples.demos.states

import com.sygic.maps.module.browsemap.BrowseMapFragment
import com.sygic.maps.module.common.mapinteraction.MapSelectionMode
import com.sygic.maps.uikit.viewmodels.common.sdk.skin.VehicleSkin
import com.sygic.sdk.map.Camera
import com.sygic.sdk.map.MapAnimation
import com.sygic.sdk.map.MapCenter
import com.sygic.sdk.map.MapCenterSettings

private const val TILT_2D = 0f
private val SCREEN_CENTER = MapCenter(0.5f, 0.5f)
private val MAP_CENTER_SETTINGS = MapCenterSettings(SCREEN_CENTER, SCREEN_CENTER, MapAnimation.NONE, MapAnimation.NONE)

class BrowseMapDemoDefaultState {

    companion object {

        fun setTo(browseMapFragment: BrowseMapFragment) {
            with(browseMapFragment) {
                compassEnabled = true
                compassHideIfNorthUp = false
                zoomControlsEnabled = true
                positionOnMapEnabled = true
                positionLockFabEnabled = true
                mapSelectionMode = MapSelectionMode.FULL
                setVehicleSkin(VehicleSkin.PEDESTRIAN)
                cameraDataModel.apply {
                    tilt = TILT_2D
                    mapCenterSettings = MAP_CENTER_SETTINGS
                    movementMode = Camera.MovementMode.FollowGpsPositionWithAutozoom
                    rotationMode = Camera.RotationMode.Attitude
                }
            }
        }
    }
}