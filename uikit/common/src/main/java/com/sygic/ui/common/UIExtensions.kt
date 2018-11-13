package com.sygic.ui.common

import android.graphics.drawable.Drawable
import android.graphics.drawable.TransitionDrawable
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.core.graphics.drawable.DrawableCompat
import androidx.appcompat.content.res.AppCompatResources
import android.view.View
import android.widget.ImageView
import java.util.*

fun Drawable.setCompatTint(@ColorInt color: Int): Drawable {
    if (color != 0) {
        val wrapper = DrawableCompat.wrap(this)
        wrapper.mutate()
        DrawableCompat.setTintList(wrapper, null)
        DrawableCompat.setTint(wrapper, color)
    }

    return this
}

fun View.getVectorDrawable(@DrawableRes drawableId: Int): Drawable {
    return AppCompatResources.getDrawable(context, drawableId)
            ?: throw MissingResourceException("Provided drawable id $drawableId not found",
                    AppCompatResources::javaClass.name, drawableId.toString())
}

fun View.getVectorDrawableWithTint(@DrawableRes drawableId: Int, @ColorInt color: Int): Drawable {
    return this.getVectorDrawable(drawableId).setCompatTint(color)
}

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