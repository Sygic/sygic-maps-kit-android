package com.sygic.samples.payload

import android.os.Parcel
import android.os.Parcelable
import com.sygic.sdk.map.`object`.payload.BasicPayload
import com.sygic.sdk.position.GeoCoordinates

class CustomPayload : BasicPayload {

    val customString: String

    constructor(
        geoCoordinates: GeoCoordinates,
        title: String,
        description: String?,
        customString: String
    ) : super(geoCoordinates, title, description) {
        this.customString = customString
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        super.writeToParcel(dest, flags)
        dest.writeString(customString)
    }

    private constructor(parcel: Parcel) : super(parcel) {
        customString = parcel.readString()!!
    }

    companion object CREATOR : Parcelable.Creator<CustomPayload> {
        override fun createFromParcel(parcel: Parcel): CustomPayload {
            return CustomPayload(parcel)
        }

        override fun newArray(size: Int): Array<CustomPayload?> {
            return arrayOfNulls(size)
        }
    }
}