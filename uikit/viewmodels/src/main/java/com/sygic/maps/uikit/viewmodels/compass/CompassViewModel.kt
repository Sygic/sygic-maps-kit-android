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

package com.sygic.maps.uikit.viewmodels.compass

import android.view.View
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sygic.maps.tools.annotations.AutoFactory
import com.sygic.maps.uikit.viewmodels.common.sdk.DEFAULT_ANIMATION
import com.sygic.maps.uikit.viewmodels.common.sdk.model.ExtendedCameraModel
import com.sygic.maps.uikit.views.compass.CompassView
import com.sygic.sdk.map.Camera
import com.sygic.sdk.position.GeoCoordinates

private const val NORTH_UP = 0f

/**
 * A [CompassViewModel] is a basic ViewModel implementation for the [CompassView] class. It listens to the Sygic SDK
 * [Camera.PositionChangedListener] and set appropriate rotation to the [CompassView] needle. It also sets the
 * north (default) rotation when [View.OnClickListener] click method is called.
 */
@AutoFactory
open class CompassViewModel internal constructor(
    private val cameraModel: ExtendedCameraModel
) : ViewModel(), Camera.PositionChangedListener, DefaultLifecycleObserver {

    val rotation: MutableLiveData<Float> = MutableLiveData()

    init {
        rotation.value = cameraModel.rotation
    }

    override fun onStart(owner: LifecycleOwner) {
        cameraModel.addPositionChangedListener(this)
    }

    override fun onStop(owner: LifecycleOwner) {
        cameraModel.removePositionChangedListener(this)
    }

    fun onClick() {
        cameraModel.setRotation(NORTH_UP, DEFAULT_ANIMATION)
    }

    override fun onPositionChanged(geoCenter: GeoCoordinates, zoom: Float, rotation: Float, tilt: Float) {
        this.rotation.value = rotation
    }

    override fun onPositionChangeCompleted() {
        // Do nothing
    }
}