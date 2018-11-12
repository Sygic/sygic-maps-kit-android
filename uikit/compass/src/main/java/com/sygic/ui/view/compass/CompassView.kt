package com.sygic.ui.view.compass

import android.content.Context
import android.content.res.ColorStateList
import android.support.annotation.ColorInt
import android.support.annotation.DrawableRes
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewCompat
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.ImageView

import com.sygic.ui.common.*

@Suppress("unused", "MemberVisibilityCanBePrivate")
class CompassView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : FrameLayout(context, attrs, defStyle) {

    @ColorInt
    private val backgroundColor: Int
    private val arrowImageView: ImageView?
    private val alphaSetter = AlphaSetter()

    private var compassRotation: Float

    init {
        val typedArray = resources.obtainAttributes(attrs, R.styleable.CompassView)
        try {
            backgroundColor = typedArray.getColor(R.styleable.CompassView_backgroundColor, ContextCompat.getColor(context, R.color.colorAccent))
        } finally {
            typedArray.recycle()
        }

        compassRotation = rotation

        generateImage(context, R.drawable.compass_nicks)
        arrowImageView = generateImage(context, R.drawable.compass_arrow)
        setBackgroundResource(R.drawable.compass_background)

        ViewCompat.setBackgroundTintList(this, ColorStateList.valueOf(backgroundColor))
    }

    private fun generateImage(context: Context, @DrawableRes resourceId: Int): ImageView {
        val imageView = ImageView(context)
        imageView.setImageDrawable(getVectorDrawable(resourceId))
        imageView.layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
        addView(imageView)

        return imageView
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        hideCompassIfNorthUp(0)
    }

    fun isNorthUp(rotation: Float): Boolean {
        return rotation >= -1 && rotation <= 1
    }

    override fun setRotation(rotation: Float) {
        val isNorth = isNorthUp(this.compassRotation)
        val northWanted = isNorthUp(rotation)
        this.compassRotation = rotation

        if (isNorth != northWanted) {
            // the visibility is going to change
            removeCallbacks(alphaSetter)
            postDelayed(alphaSetter, 500L)
        }

        // setRotation is sometimes called from View constructor
        arrowImageView?.let { it.rotation = rotation }
    }

    private fun hideCompassIfNorthUp(animationDuration: Long) {
        if (!isNorthUp(compassRotation)) {
            animate().alpha(1f).duration = animationDuration
        } else {
            animate().alpha(0f).duration = animationDuration
        }
    }

    private inner class AlphaSetter : Runnable {
        override fun run() {
            hideCompassIfNorthUp(300L)
        }
    }
}
