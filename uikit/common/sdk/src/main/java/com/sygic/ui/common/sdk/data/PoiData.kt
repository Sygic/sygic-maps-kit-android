package com.sygic.ui.common.sdk.data

import android.text.TextUtils
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

        name?.let { if (!it.isEmpty()) builder.append(it) }
        city?.let { if (!it.isEmpty()) builder.append("\n").append(it) }
        street?.let { if (!it.isEmpty()) builder.append("\n").append(it) }
        houseNumber?.let { if (!it.isEmpty()) builder.append("\n").append(it) }
        postal?.let { if (!it.isEmpty()) builder.append("\n").append(it) }
        iso?.let { if (!it.isEmpty()) builder.append("\n").append(it) }
        phone?.let { if (!it.isEmpty()) builder.append("\n").append(it) }
        email?.let { if (!it.isEmpty()) builder.append("\n").append(it) }
        url?.let { if (!it.isEmpty()) builder.append("\n").append(it) }

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

/**
 * Formatted example: Mlynské nivy 16, 821 09 Bratislava
 */
private fun getStreetWithHouseNumberAndCityWithPostal(
    street: String?,
    houseNumber: String?,
    city: String?,
    postal: String?
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
private fun getStreetWithHouseNumber(street: String?, houseNumber: String?): String? =
    houseNumber?.let { if (it.isNotEmpty()) String.format("%s %s", street, it) else street } ?: street

/**
 * Formatted example: 821 09 Bratislava
 */
private fun getCityWithPostal(city: String?, postal: String?): String? =
    postal?.let { if (it.isNotEmpty()) return String.format("%s %s", it, city) else city } ?: city