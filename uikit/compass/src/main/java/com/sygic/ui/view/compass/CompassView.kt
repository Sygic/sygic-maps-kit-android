package com.sygic.ui.view.compass

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.annotation.DrawableRes

private const val ANIMATION_DURATION = 300L
private const val ANIMATION_DELAY = 500L

@Suppress("unused", "MemberVisibilityCanBePrivate")
class CompassView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.compassStyle,
    defStyleRes: Int = R.style.SygicCompassStyle
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes) {

    private val arrowImageView: ImageView?
    private val alphaSetter = AlphaSetter()

    private var compassRotation: Float
    var hideCompassIfNorthUpAllowed: Boolean = false

    init {
        //applyStyle(R.style.SygicComponentStyle) //todo: use it or remove it?!

        compassRotation = rotation

        addView(createImageView(context, R.drawable.compass_nicks))
        addView(createImageView(context, R.drawable.compass_arrow).also { arrowImageView = it })
    }

    private fun createImageView(context: Context, @DrawableRes resourceId: Int): ImageView {
        val imageView = ImageView(context)
        imageView.setImageResource(resourceId)
        imageView.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
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
            postDelayed(alphaSetter, ANIMATION_DELAY)
        }

        // setRotation is sometimes called from View constructor
        arrowImageView?.let { it.rotation = rotation }
    }

    private fun hideCompassIfNorthUp(animationDuration: Long) {
        if (!hideCompassIfNorthUpAllowed) {
            return
        }

        if (!isNorthUp(compassRotation)) {
            animate().alpha(1f).duration = animationDuration
        } else {
            animate().alpha(0f).duration = animationDuration
        }
    }

    private inner class AlphaSetter : Runnable {
        override fun run() {
            hideCompassIfNorthUp(ANIMATION_DURATION)
        }
    }
}
