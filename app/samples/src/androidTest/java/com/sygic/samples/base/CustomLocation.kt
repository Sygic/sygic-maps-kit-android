package com.sygic.samples.base

import android.view.View
import androidx.test.espresso.action.CoordinatesProvider

enum class CustomLocation : CoordinatesProvider {
    BOTTOM_CENTER_OFFSET {
        override fun calculateCoordinates(view: View) = getCoordinates(view, .75f, .5f)
    };

    companion object {
        fun getCoordinates(view: View, vertical: Float, horizontal: Float): FloatArray {
            val xy = IntArray(2)
            view.getLocationOnScreen(xy)
            return floatArrayOf(xy[0] * horizontal, xy[1] * vertical)
        }
    }
}