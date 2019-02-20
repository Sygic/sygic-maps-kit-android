package com.sygic.ui.common.sdk.data

import android.os.Parcel
import android.os.Parcelable
import android.text.TextUtils
import com.sygic.sdk.map.`object`.payload.Payload
import com.sygic.sdk.places.PoiInfo
import com.sygic.sdk.position.GeoCoordinates
import com.sygic.ui.common.extensions.EMPTY_STRING
import com.sygic.ui.common.sdk.extension.getFormattedLocation

data class PoiDataPayload(
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
) : Payload {

    val isEmpty: Boolean
        get() = coordinates == GeoCoordinates.Invalid

    val addressComponent: AddressComponent
        get() {
            name?.let {
                if (it.isNotEmpty()) {
                    return AddressComponent(it, getStreetWithHouseNumberAndCityWithPostal())
                }
            }
            street?.let { street ->
                if (street.isNotEmpty()) {
                    return AddressComponent(
                        getStreetWithHouseNumber(),
                        city?.let { if (it.isNotEmpty()) getCityWithPostal() else null })
                }
            }
            city?.let {
                if (it.isNotEmpty()) {
                    return AddressComponent(getCityWithPostal())
                }
            }

            return AddressComponent(coordinates.getFormattedLocation())
        }

    override fun getPosition(): GeoCoordinates = coordinates

    override fun getTitle(): String = addressComponent.formattedTitle

    override fun getDescription(): String = addressComponent.formattedSubtitle

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

    /**
     * Formatted example: Mlynské nivy 16, 821 09 Bratislava
     */
    private fun getStreetWithHouseNumberAndCityWithPostal(): String {
        val builder = StringBuilder()
        val street = street?.let { getStreetWithHouseNumber() }

        street?.let { builder.append(it) }
        getCityWithPostal()?.let {
            if (!TextUtils.isEmpty(street)) builder.append(", ")
            builder.append(it)
        }
        return builder.toString()
    }

    /**
     * Formatted example: Mlynské nivy 16
     */
    private fun getStreetWithHouseNumber(): String? =
        houseNumber?.let { if (it.isNotEmpty()) String.format("%s %s", street, it) else street } ?: street

    /**
     * Formatted example: 821 09 Bratislava
     */
    private fun getCityWithPostal(): String? =
        postal?.let { if (it.isNotEmpty()) return String.format("%s %s", it, city) else city } ?: city

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeParcelable(coordinates, flags)
        dest.writeString(name)
        dest.writeString(iso)
        dest.writeInt(poiGroup)
        dest.writeInt(poiCategory)
        dest.writeString(city)
        dest.writeString(street)
        dest.writeString(houseNumber)
        dest.writeString(postal)
        dest.writeString(phone)
        dest.writeString(email)
        dest.writeString(url)
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

        @JvmField
        val EMPTY = PoiDataPayload()

        @JvmField
        val CREATOR = object : Parcelable.Creator<PoiDataPayload> {
            override fun createFromParcel(parcel: Parcel): PoiDataPayload {
                return PoiDataPayload(parcel)
            }

            override fun newArray(size: Int): Array<PoiDataPayload?> {
                return arrayOfNulls(size)
            }
        }
    }

    class AddressComponent(private val title: String? = null, private val subtitle: String? = null) {
        val formattedTitle: String
            get() = title?.let { it } ?: EMPTY_STRING
        val formattedSubtitle: String
            get() = subtitle?.let { it } ?: EMPTY_STRING
    }
}
