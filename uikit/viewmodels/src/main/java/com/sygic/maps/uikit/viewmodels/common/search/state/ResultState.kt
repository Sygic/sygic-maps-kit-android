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

package com.sygic.maps.uikit.viewmodels.common.search.state

import android.os.Parcelable
import com.sygic.sdk.search.SearchManager
import kotlinx.android.parcel.Parcelize

@Parcelize
enum class ResultState : Parcelable {
    SUCCESS, NETWORK_UNAVAILABLE, NETWORK_TIMEOUT, GENERAL_ERROR, UNKNOWN
}

fun SearchManager.ErrorCode.toResultState(): ResultState = when (this) {
    SearchManager.ErrorCode.REQUEST_CANCELED -> ResultState.GENERAL_ERROR
    SearchManager.ErrorCode.INAVLID_LOCATION_ID -> ResultState.GENERAL_ERROR
    SearchManager.ErrorCode.INVALID_CATEGORY_TAG -> ResultState.GENERAL_ERROR
    SearchManager.ErrorCode.UNATHORIZED -> ResultState.GENERAL_ERROR
    SearchManager.ErrorCode.NETWORK_UNAVAILABLE -> ResultState.NETWORK_UNAVAILABLE
    SearchManager.ErrorCode.NETWORK_TIMEOUT -> ResultState.NETWORK_TIMEOUT
    SearchManager.ErrorCode.UNKNOWN -> ResultState.UNKNOWN
}