package com.sygic.ui.common.sdk.data

import android.os.Parcel
import android.os.Parcelable
import com.sygic.sdk.places.PoiInfo
import com.sygic.sdk.position.GeoCoordinates

// Todo: Builder for Java
data class PoiData(
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
) : Parcelable {

    fun isEmpty() = coordinates == GeoCoordinates.Invalid

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

    companion object CREATOR : Parcelable.Creator<PoiData> {
        override fun createFromParcel(parcel: Parcel): PoiData {
            return PoiData(parcel)
        }

        override fun newArray(size: Int): Array<PoiData?> {
            return arrayOfNulls(size)
        }
    }
}
