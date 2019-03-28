package com.sygic.ui.common.sdk.data

import android.os.Parcelable
import com.sygic.ui.common.extensions.EMPTY_STRING
import kotlinx.android.parcel.Parcelize

@Parcelize
open class BasicData(private val basicDescription: BasicDescription) : Parcelable {

    val title: String
        get() = basicDescription.formattedTitle

    val description: String
        get() = basicDescription.formattedSubtitle

    constructor(title: String, description: String = "") : this(
        BasicDescription(title, description)
    )

    @Parcelize
    class BasicDescription(private val title: String? = null, private val subtitle: String? = null) : Parcelable {
        val formattedTitle: String
            get() = title?.let { it } ?: EMPTY_STRING
        val formattedSubtitle: String
            get() = subtitle?.let { it } ?: EMPTY_STRING
    }
}