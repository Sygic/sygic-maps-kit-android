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

import android.text.TextUtils
import com.sygic.maps.uikit.viewmodels.common.utils.appendOnNewLine
import com.sygic.maps.uikit.viewmodels.common.utils.getCityWithPostal
import com.sygic.maps.uikit.viewmodels.common.utils.getStreetWithHouseNumber
import com.sygic.maps.uikit.viewmodels.common.utils.getStreetWithHouseNumberAndCityWithPostal
import com.sygic.sdk.places.PoiInfo
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PoiData(
    var name: String? = null,
    var street: String? = null,
    var houseNumber: String? = null,
    var city: String? = null,
    var postal: String? = null,
    var iso: String? = null,
    var phone: String? = null,
    var email: String? = null,
    var url: String? = null,
    @PoiInfo.PoiGroup var poiGroup: Int = PoiInfo.PoiGroup.Unknown,
    @PoiInfo.PoiCategory var poiCategory: Int = PoiInfo.PoiCategory.Unknown
) : BasicData(createBasicDescription(name, street, houseNumber, city, postal)) {

    override fun toString(): String {
        val builder = StringBuilder()

        name?.let { if (it.isNotEmpty()) builder.appendOnNewLine(it) }
        city?.let { if (it.isNotEmpty()) builder.appendOnNewLine(it) }
        street?.let { if (it.isNotEmpty()) builder.appendOnNewLine(it) }
        houseNumber?.let { if (it.isNotEmpty()) builder.appendOnNewLine(it) }
        postal?.let { if (it.isNotEmpty()) builder.appendOnNewLine(it) }
        iso?.let { if (it.isNotEmpty()) builder.appendOnNewLine(it) }
        phone?.let { if (it.isNotEmpty()) builder.appendOnNewLine(it) }
        email?.let { if (it.isNotEmpty()) builder.appendOnNewLine(it) }
        url?.let { if (it.isNotEmpty()) builder.appendOnNewLine(it) }

        return builder.toString()
    }
}

private fun createBasicDescription(
    name: String?,
    street: String?,
    houseNumber: String?,
    city: String?,
    postal: String?
): BasicData.BasicDescription {
    name?.let {
        if (it.isNotEmpty()) {
            return BasicData.BasicDescription(
                it,
                getStreetWithHouseNumberAndCityWithPostal(street, houseNumber, city, postal)
            )
        }
    }
    street?.let {
        if (it.isNotEmpty()) {
            return BasicData.BasicDescription(
                getStreetWithHouseNumber(it, houseNumber),
                city?.let { city -> if (city.isNotEmpty()) getCityWithPostal(city, postal) else null })
        }
    }
    city?.let {
        if (it.isNotEmpty()) {
            return BasicData.BasicDescription(getCityWithPostal(city, postal))
        }
    }

    return BasicData.BasicDescription()
}