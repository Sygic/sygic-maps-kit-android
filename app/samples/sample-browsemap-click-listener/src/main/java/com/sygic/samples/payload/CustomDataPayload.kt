package com.sygic.samples.payload

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class CustomDataPayload(val customString: String) : Parcelable