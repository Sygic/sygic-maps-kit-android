package com.sygic.ui.common.sdk.data

import android.os.Parcel
import android.os.Parcelable
import com.sygic.sdk.map.`object`.payload.PositionPayload
import com.sygic.sdk.position.GeoCoordinates


open class BasicPayload : PositionPayload {

    protected val basicDescription: BasicDescription

    val title: String
        get() = basicDescription.formattedTitle

    val description: String
        get() = basicDescription.formattedSubtitle

    constructor(title: String, description: String = "", latitude: Double, longitude: Double) :
            this(title, description, GeoCoordinates(latitude, longitude))

    constructor(title: String, description: String = "", position: GeoCoordinates) : this(
        BasicDescription(title, description), position
    )

    constructor(description: BasicDescription, position: GeoCoordinates) : super(position) {
        this.basicDescription = description
    }

    constructor(parcel: Parcel) : super(parcel) {
        this.basicDescription = BasicDescription(parcel.readString(), parcel.readString())
    }

    override fun getType(): String {
        return TYPE_BASIC
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(basicDescription.formattedTitle)
        parcel.writeString(basicDescription.formattedSubtitle)
    }

    companion object {
        const val TYPE_BASIC = "payload_basic"

        @JvmField
        val CREATOR = object : Parcelable.Creator<BasicPayload> {
            override fun createFromParcel(parcel: Parcel): BasicPayload {
                return BasicPayload(parcel)
            }

            override fun newArray(size: Int): Array<BasicPayload?> {
                return arrayOfNulls(size)
            }
        }
    }
}