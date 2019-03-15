package com.sygic.ui.common.sdk.data

import com.sygic.ui.common.extensions.EMPTY_STRING

class BasicDescription(private val title: String? = null, private val subtitle: String? = null) {
    val formattedTitle: String
        get() = title?.let { it } ?: EMPTY_STRING
    val formattedSubtitle: String
        get() = subtitle?.let { it } ?: EMPTY_STRING
}