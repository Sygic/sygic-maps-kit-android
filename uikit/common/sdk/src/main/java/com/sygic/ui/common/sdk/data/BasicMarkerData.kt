package com.sygic.ui.common.sdk.data

import android.os.Parcel
import android.os.Parcelable
import com.sygic.sdk.map.`object`.data.PositionMarkerData
import com.sygic.sdk.position.GeoCoordinates

open class BasicMarkerData : PositionMarkerData { //todo

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

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(basicDescription.formattedTitle)
        parcel.writeString(basicDescription.formattedSubtitle)
    }

    companion object {
        @JvmField
        val CREATOR = object : Parcelable.Creator<BasicMarkerData> {
            override fun createFromParcel(parcel: Parcel): BasicMarkerData {
                return BasicMarkerData(parcel)
            }

            override fun newArray(size: Int): Array<BasicMarkerData?> {
                return arrayOfNulls(size)
            }
        }
    }
}