package com.sygic.ui.common.sdk.utlils

import android.text.TextUtils
import com.sygic.ui.common.sdk.data.PoiData

fun getAddressTitle(poiData: PoiData): String {
    poiData.name?.let { if (it.isNotEmpty()) return it }
    poiData.street?.let { if (it.isNotEmpty()) return getStreetWithHouseNumber(it, poiData.houseNumber) }
    poiData.city?.let { if (it.isNotEmpty()) return getCityWithPostal(it, poiData.postal) }

    return getFormattedLocation(poiData.coordinates)
}

fun getAddressSubtitle(poiData: PoiData): String {
    poiData.name?.let { if (it.isNotEmpty()) return getStreetWithHouseNumberAndCityWithPostal(poiData) }
    poiData.street?.let { street ->
        if (street.isNotEmpty()) poiData.city?.let { if (it.isNotEmpty()) return getCityWithPostal(it, poiData.postal) }
    }

    return ""
}

/**
 * Formatted example: Mlynské nivy 16
 */
private fun getStreetWithHouseNumber(street: String, houseNumber: String?): String {
    houseNumber?.let { if (it.isNotEmpty()) return String.format("%s %s", street, it) }

    return street
}

/**
 * Formatted example: 821 09 Bratislava
 */
private fun getCityWithPostal(city: String, postal: String?): String {
    postal?.let { if (it.isNotEmpty()) return String.format("%s %s", it, city) }

    return city
}

/**
 * Formatted example: Mlynské nivy 16, 821 09 Bratislava
 */
private fun getStreetWithHouseNumberAndCityWithPostal(poiData: PoiData): String {
    val builder = StringBuilder()
    val city = poiData.city?.let { getCityWithPostal(it, poiData.postal) }
    val street = poiData.street?.let { getStreetWithHouseNumber(it, poiData.houseNumber) }

    street?.let { builder.append(it) }
    city?.let { builder.append(if (TextUtils.isEmpty(street)) it else String.format(", %s", it)) }
    return builder.toString()
}
