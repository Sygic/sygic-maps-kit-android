package com.sygic.ui.common

import android.content.Context
import android.view.View

fun Context.isRtl(): Boolean {
    return this.resources.configuration.layoutDirection == View.LAYOUT_DIRECTION_RTL
}