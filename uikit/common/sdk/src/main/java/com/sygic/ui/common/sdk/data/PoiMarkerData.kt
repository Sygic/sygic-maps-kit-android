package com.sygic.ui.common.sdk.data

import android.os.Parcel
import android.os.Parcelable
import android.text.TextUtils
import com.sygic.sdk.places.PoiInfo
import com.sygic.sdk.position.GeoCoordinates
import com.sygic.ui.common.sdk.extension.getFormattedLocation

data class PoiMarkerData(
    var coordinates: GeoCoordinates = GeoCoordinates.Invalid,
    var name: String? = null,
    var iso: String? = null,
    @PoiInfo.PoiGroup var poiGroup: Int = PoiInfo.PoiGroup.Unknown,
    @PoiInfo.PoiCategory var poiCategory: Int = PoiInfo.PoiCategory.Unknown,
    var city: String? = null,
    var street: String? = null,
    var houseNumber: String? = null,
    var postal: String? = null,
    var phone: String? = null,
    var email: String? = null,
    var url: String? = null
) : BasicMarkerData(createAddressDescription(name, street, houseNumber, city, postal, coordinates), coordinates) {

    val isEmpty: Boolean
        get() = coordinates == GeoCoordinates.Invalid

    val addressComponent: BasicDescription
        get() = basicDescription

    override fun getPosition(): GeoCoordinates = coordinates

    override fun toString(): String {
        val builder = StringBuilder()

        builder.append(javaClass.simpleName)
        builder.append("\n").append("${coordinates.latitude}, ${coordinates.longitude}")
        name?.let { if (!it.isEmpty()) builder.append("\n").append(it) }
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

    override fun getType(): String {
        return TYPE_POI
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(coordinates, flags)
        parcel.writeString(name)
        parcel.writeString(iso)
        parcel.writeInt(poiGroup)
        parcel.writeInt(poiCategory)
        parcel.writeString(city)
        parcel.writeString(street)
        parcel.writeString(houseNumber)
        parcel.writeString(postal)
        parcel.writeString(phone)
        parcel.writeString(email)
        parcel.writeString(url)
    }

    constructor(parcel: Parcel) : this(
        parcel.readParcelable(GeoCoordinates::class.java.classLoader)!!,
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    )

    companion object {

        const val TYPE_POI = "payload_poi"

        @JvmField
        val EMPTY = PoiMarkerData()

        @JvmField
        val CREATOR = object : Parcelable.Creator<PoiMarkerData> {
            override fun createFromParcel(parcel: Parcel): PoiMarkerData {
                return PoiMarkerData(parcel)
            }

            override fun newArray(size: Int): Array<PoiMarkerData?> {
                return arrayOfNulls(size)
            }
        }
    }

}

private fun createAddressDescription(
    name: String?,
    street: String?,
    houseNumber: String?,
    city: String?,
    postal: String?,
    coordinates: GeoCoordinates
): BasicDescription {
    name?.let {
        if (it.isNotEmpty()) {
            return BasicDescription(
                it,
                getStreetWithHouseNumberAndCityWithPostal(street, houseNumber, city, postal)
            )
        }
    }
    street?.let {
        if (it.isNotEmpty()) {
            return BasicDescription(
                getStreetWithHouseNumber(it, houseNumber),
                city?.let { if (it.isNotEmpty()) getCityWithPostal(city, postal) else null })
        }
    }
    city?.let {
        if (it.isNotEmpty()) {
            return BasicDescription(getCityWithPostal(city, postal))
        }
    }

    return BasicDescription(coordinates.getFormattedLocation())
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