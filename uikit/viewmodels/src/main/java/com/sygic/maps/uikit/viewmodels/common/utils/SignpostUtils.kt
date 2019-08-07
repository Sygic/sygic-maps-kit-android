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

package com.sygic.maps.uikit.viewmodels.common.utils

import com.sygic.maps.uikit.viewmodels.R
import com.sygic.maps.uikit.viewmodels.common.extensions.*
import com.sygic.maps.uikit.viewmodels.common.extensions.getDirectionInstruction
import com.sygic.maps.uikit.viewmodels.common.extensions.isRoundabout
import com.sygic.maps.uikit.viewmodels.common.extensions.nextRoadText
import com.sygic.maps.uikit.views.common.utils.TextHolder
import com.sygic.sdk.navigation.warnings.DirectionInfo
import com.sygic.sdk.navigation.warnings.NaviSignInfo

internal fun createInstructionText(directionInfo: DirectionInfo, naviSignInfo: NaviSignInfo? = null): TextHolder {
    if (directionInfo.distance > 2000) { // 2km
        return TextHolder.from(R.string.follow_the_route)
    }

    naviSignInfo?.let { info ->
        val acceptedSignElements = info.signElements
            .filter { element ->
                element.elementType.let {
                    it == NaviSignInfo.SignElement.SignElementType.ExitNumber
                            || it == NaviSignInfo.SignElement.SignElementType.PlaceName
                            || it == NaviSignInfo.SignElement.SignElementType.OtherDestination
                }
            }
            .sortedByDescending { it.elementType == NaviSignInfo.SignElement.SignElementType.ExitNumber }

        return if (acceptedSignElements.any { it.elementType == NaviSignInfo.SignElement.SignElementType.ExitNumber }) {
            TextHolder.from(R.string.exit_number, acceptedSignElements.concatItems())
        } else {
            TextHolder.from(acceptedSignElements.concatItems())
        }
    }

    with(directionInfo.primary) {
        if (!isValid) {
            return TextHolder.empty
        }

        if (isRoundabout()) {
            getDirectionInstruction().let {
                return if (it != 0) TextHolder.from(it, roundaboutExit) else TextHolder.empty
            }
        }

        with(nextRoadText()) {
            if (isNotEmpty()) return TextHolder.from(this)
        }

        getDirectionInstruction().let {
            return if (it != 0) TextHolder.from(it) else TextHolder.empty
        }
    }
}
