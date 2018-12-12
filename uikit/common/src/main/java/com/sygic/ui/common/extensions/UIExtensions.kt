package com.sygic.ui.common.extensions

import android.graphics.drawable.Drawable
import android.graphics.drawable.TransitionDrawable
import android.widget.ImageView

fun ImageView.setImageDrawableWithFade(targetDrawable: Drawable, duration: Int) {
    val currentDrawable = this.drawable
    if (currentDrawable == null) {
        this.setImageDrawable(targetDrawable)
        return
    }

    val layers = arrayOf(currentDrawable, targetDrawable)
    val transition = TransitionDrawable(layers)
    transition.isCrossFadeEnabled = true
    this.setImageDrawable(transition)
    transition.startTransition(duration)
}