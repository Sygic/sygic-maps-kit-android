package com.sygic.ui.view.compass

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.annotation.CallSuper
import androidx.annotation.DrawableRes

private const val DEFAULT_ANIMATION_DELAY = 500L
private const val DEFAULT_ANIMATION_DURATION = 300L

/**
 * A [CompassView] is a simple view using virtual needle to point to the geographic north relatively to the map rotation.
 * The view can be simply controlled with the standard [setRotation] method.
 *
 * The size, background drawable or color can be changed with the custom _compassStyle_ style or standard android
 * attributes as _background_ or _accentColor_ definition.
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
open class CompassView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.compassStyle,
    defStyleRes: Int = R.style.SygicCompassStyle
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes) {

    /**
     * A *[hideAnimationDelay]* modifies the [CompassView] auto hide delay. The default value is [DEFAULT_ANIMATION_DELAY].
     *
     * @param [Long] delay value in milliseconds.
     *
     * @return the animation delay value in milliseconds.
     */
    var hideAnimationDelay: Long = DEFAULT_ANIMATION_DELAY
        protected set

    /**
     * A *[hideAnimationDuration]* modifies the [CompassView] auto hide animation duration. The default value is [DEFAULT_ANIMATION_DURATION].
     *
     * @param [Long] animation duration value in milliseconds.
     *
     * @return the animation duration value in milliseconds.
     */
    var hideAnimationDuration: Long = DEFAULT_ANIMATION_DURATION
        protected set

    /**
     * A *[hideCompassIfNorthUpAllowed]* modifies the [CompassView] auto hide behaviour.
     *
     * @param [Boolean] true to hide the [CompassView] automatically when it points northwards, false otherwise.
     *
     * @return whether the [CompassView] auto hide behaviour is on or off.
     */
    var hideCompassIfNorthUpAllowed: Boolean = false

    protected val arrowImageView: ImageView?
    protected val alphaSetter = AlphaSetter()

    private var needleRotation: Float

    init {
        needleRotation = rotation

        addView(createImageView(context, attrs, defStyleAttr, R.drawable.compass_nicks))
        addView(createImageView(context, R.drawable.compass_arrow).also { arrowImageView = it })
    }

    private fun createImageView(context: Context, @DrawableRes resourceId: Int): ImageView {
        val imageView = ImageView(context)
        imageView.setImageResource(resourceId)
        imageView.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        return imageView
    }

    private fun createImageView(context: Context, attrs: AttributeSet?,
                                defStyleAttr: Int, @DrawableRes resourceId: Int): ImageView {
        val imageView = ImageView(context, attrs, defStyleAttr)
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
    @CallSuper
    override fun setRotation(rotation: Float) {
        val isNorth = isNorthUp(needleRotation)
        val northWanted = isNorthUp(rotation)
        needleRotation = rotation

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

        if (!isNorthUp(needleRotation)) {
            animate().alpha(1f).duration = animationDuration
        } else {
            animate().alpha(0f).duration = animationDuration
        }
    }

    private fun isNorthUp(rotation: Float): Boolean = rotation >= -1 && rotation <= 1

    protected inner class AlphaSetter : Runnable {
        override fun run() {
            hideCompassIfNorthUp(hideAnimationDuration)
        }
    }
}
