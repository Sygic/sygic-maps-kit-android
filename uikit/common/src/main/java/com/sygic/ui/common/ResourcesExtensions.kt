package com.sygic.ui.common

import android.content.res.Resources
import android.util.TypedValue

fun Resources.dpToPixels(dp: Float): Float {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, this.displayMetrics)
}