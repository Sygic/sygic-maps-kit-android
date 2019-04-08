package com.sygic.maps.uikit.views.poidetail.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PoiDetailData(
    val titleString: String,
    val subtitleString: String,
    val urlString: String? = null,
    val emailString: String? = null,
    val phoneString: String? = null,
    val coordinatesString: String? = null
) : Parcelable
