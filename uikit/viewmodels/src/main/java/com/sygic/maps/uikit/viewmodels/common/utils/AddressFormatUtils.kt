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

import android.text.TextUtils

/**
 * Formatted example: Mlynské nivy 16, 821 09 Bratislava
 */
fun getStreetWithHouseNumberAndCityWithPostal(
    street: String? = null,
    houseNumber: String? = null,
    city: String? = null,
    postal: String? = null
): String {
    val builder = StringBuilder()
    val streetWithHouseNumber = street?.let { getStreetWithHouseNumber(street, houseNumber) }

    streetWithHouseNumber?.let { builder.append(it) }
    getCityWithPostal(city, postal)?.let {
        if (!TextUtils.isEmpty(streetWithHouseNumber)) builder.append(", ")
        builder.append(it)
    }
    return builder.toString()
}

/**
 * Formatted example: Mlynské nivy 16
 */
fun getStreetWithHouseNumber(street: String?, houseNumber: String?): String? =
    houseNumber?.let { if (it.isNotEmpty()) String.format("%s %s", street, it) else street } ?: street

/**
 * Formatted example: 821 09 Bratislava
 */
fun getCityWithPostal(city: String?, postal: String?): String? =
    postal?.let { if (it.isNotEmpty()) return String.format("%s %s", it, city) else city } ?: city