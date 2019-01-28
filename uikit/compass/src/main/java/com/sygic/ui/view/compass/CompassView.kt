package com.sygic.ui.view.compass

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.annotation.UiThread

private const val DEFAULT_ANIMATION_DELAY = 500L
private const val DEFAULT_ANIMATION_DURATION = 300L

/**
 * A [CompassView] is a simple but powerful view with bundled virtual needle. The primary purpose of this child view is point
 * to the north relatively to the map rotation. The view can be simply controlled with the standard [setRotation] method.
 *
 * The size, background drawable or color can be changed with the custom style or attribute. See "Sample app" for more info.
 */
@UiThread
@Suppress("unused", "MemberVisibilityCanBePrivate")
open class CompassView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.compassStyle,
    defStyleRes: Int = R.style.SygicCompassStyle
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes) {

    /**
     * A *[hideAnimationDelay]* modifies the [CompassView] auto hide delay. The default value is 500ms.
     *
     * @param [Long] delay value in milliseconds.
     *
     * @return the animation delay value in milliseconds.
     */
    var hideAnimationDelay: Long = DEFAULT_ANIMATION_DELAY

    /**
     * A *[hideAnimationDuration]* modifies the [CompassView] auto hide animation duration. The default value is 300ms.
     *
     * @param [Long] animation duration value in milliseconds.
     *
     * @return the animation duration value in milliseconds.
     */
    var hideAnimationDuration: Long = DEFAULT_ANIMATION_DURATION

    /**
     * A *[hideCompassIfNorthUpAllowed]* modifies the [CompassView] auto hide behaviour.
     *
     * @param [Boolean] true to hide the [CompassView] automatically when it points northwards, false otherwise.
     *
     * @return whether the [CompassView] auto hide behaviour is on or off.
     */
    var hideCompassIfNorthUpAllowed: Boolean = false

    private val arrowImageView: ImageView?
    private val alphaSetter = AlphaSetter()

    private var compassRotation: Float

    init {
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

    /**
     * Allows you to change rotation of the [CompassView] needle. The default value is 0 (north).
     *
     * @param rotation [Float] to be applied to the [CompassView] needle.
     */
    override fun setRotation(rotation: Float) {
        val isNorth = isNorthUp(compassRotation)
        val northWanted = isNorthUp(rotation)
        compassRotation = rotation

        if (isNorth != northWanted) {
            // the visibility is going to change
            removeCallbacks(alphaSetter)
            postDelayed(alphaSetter, hideAnimationDelay)
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

    private fun isNorthUp(rotation: Float): Boolean = rotation >= -1 && rotation <= 1

    private inner class AlphaSetter : Runnable {
        override fun run() {
            hideCompassIfNorthUp(hideAnimationDuration)
        }
    }
}
