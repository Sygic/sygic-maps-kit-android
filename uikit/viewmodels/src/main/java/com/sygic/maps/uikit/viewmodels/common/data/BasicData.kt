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

package com.sygic.maps.uikit.viewmodels.common.data

import android.os.Parcelable
import com.sygic.maps.uikit.views.common.extensions.EMPTY_STRING
import kotlinx.android.parcel.Parcelize

@Parcelize
open class BasicData(private val basicDescription: BasicDescription) : Parcelable {

    val title: String
        get() = basicDescription.formattedTitle

    val description: String
        get() = basicDescription.formattedSubtitle

    constructor(title: String, description: String = "") : this(
        BasicDescription(title, description)
    )

    @Parcelize
    class BasicDescription(private val title: String? = null, private val subtitle: String? = null) : Parcelable {
        val formattedTitle: String
            get() = title?.let { it } ?: EMPTY_STRING
        val formattedSubtitle: String
            get() = subtitle?.let { it } ?: EMPTY_STRING
    }
}