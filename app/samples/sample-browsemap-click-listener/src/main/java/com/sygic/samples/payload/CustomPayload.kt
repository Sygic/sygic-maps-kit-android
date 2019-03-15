package com.sygic.samples.payload

import android.os.Parcel
import android.os.Parcelable
import com.sygic.sdk.position.GeoCoordinates
import com.sygic.ui.common.sdk.data.BasicPayload

class CustomPayload : BasicPayload {

    val customString: String

    constructor(
        geoCoordinates: GeoCoordinates,
        title: String,
        description: String = "",
        customString: String
    ) : super(title, description, geoCoordinates) {
        this.customString = customString
    }

    override fun getType(): String {
        return TYPE_CUSTOM
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        super.writeToParcel(parcel, flags)
        parcel.writeString(customString)
    }

    private constructor(parcel: Parcel) : super(parcel) {
        customString = parcel.readString()!!
    }

    companion object {
        const val TYPE_CUSTOM = "payload_custom"

        @JvmField
        val CREATOR = object : Parcelable.Creator<CustomPayload> {
            override fun createFromParcel(parcel: Parcel): CustomPayload {
                return CustomPayload(parcel)
            }

            override fun newArray(size: Int): Array<CustomPayload?> {
                return arrayOfNulls(size)
            }
        }
    }
}