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

package com.sygic.maps.uikit.viewmodels.navigation.lanes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sygic.maps.tools.annotations.AutoFactory
import com.sygic.maps.uikit.viewmodels.R
import com.sygic.maps.uikit.viewmodels.common.navigation.NavigationManagerClient
import com.sygic.maps.uikit.views.common.extensions.asMutable
import com.sygic.maps.uikit.views.navigation.lanes.data.SimpleLanesData
import com.sygic.sdk.navigation.NavigationManager
import com.sygic.sdk.navigation.routeeventnotifications.LaneInfo
import com.sygic.sdk.navigation.routeeventnotifications.LaneInfo.Lane.Direction.*

@AutoFactory
class LanesViewModel internal constructor(
    val navigationManagerClient: NavigationManagerClient
) : ViewModel(), NavigationManager.OnLaneListener {

    val enabled: LiveData<Boolean> = MutableLiveData(false)
    val lanesData: LiveData<Array<SimpleLanesData>> = MutableLiveData(emptyArray())

    init {
        navigationManagerClient.addOnLaneListener(this)
    }

    override fun onLaneInfoChanged(info: LaneInfo) {
        if (!info.isActive) {
            enabled.asMutable().value = false
            return
        }

        with(info.simpleLanesInfo) {
            val lanesArray = this?.lanes?.map {
                SimpleLanesData(
                    it.directions.map(transformationDirections).ifEmpty { listOf(R.drawable.ic_lanedirection_straight) },
                    it.isHighlighted
                )
            }?.toTypedArray() ?: emptyArray()

            lanesData.asMutable().value = lanesArray
            enabled.asMutable().value = lanesArray.isNotEmpty()
        }
    }

    override fun onCleared() {
        super.onCleared()
        navigationManagerClient.removeOnLaneListener(this)
    }
}

/**
 * converts [LaneInfo.Lane.Direction] values to appropriate drawables
 *
 */
private val transformationDirections: (Int) -> Int = {
    when (it) {
        Right -> R.drawable.ic_lanedirection_right
        HalfRight -> R.drawable.ic_lanedirection_right_half
        SharpRight -> R.drawable.ic_lanedirection_right_sharp
        UTurnRight -> R.drawable.ic_lanedirection_right_uturn
        Left -> R.drawable.ic_lanedirection_left
        HalfLeft -> R.drawable.ic_lanedirection_left_half
        SharpLeft -> R.drawable.ic_lanedirection_left_sharp
        UTurnLeft -> R.drawable.ic_lanedirection_left_uturn
        else -> R.drawable.ic_lanedirection_straight
    }
}