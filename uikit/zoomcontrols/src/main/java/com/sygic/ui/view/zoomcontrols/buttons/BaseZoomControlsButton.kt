package com.sygic.ui.view.zoomcontrols.buttons

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.sygic.ui.view.zoomcontrols.R
import com.sygic.ui.view.zoomcontrols.ZoomControlsMenu

@Suppress("unused", "MemberVisibilityCanBePrivate")
internal abstract class BaseZoomControlsButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.zoomControlsMenuStyle,
    defStyleRes: Int = R.style.SygicZoomControlsMenuStyle,
    @DrawableRes iconDrawableRes: Int = 0
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes) {

    internal var interactionListener: ZoomControlsMenu.InteractionListener? = null

    private val iconImageView: ImageView
    private val showAnimation: Animation = AnimationUtils.loadAnimation(context, R.anim.zoom_button_scale_up)
    private val hideAnimation: Animation = AnimationUtils.loadAnimation(context, R.anim.zoom_button_scale_down)

    open fun onActionDown() {}
    abstract fun onActionUpOrCancel()

    init {
        isClickable = true
        isFocusable = true
        clipToOutline = true
        addView(createButtonIcon(context, attrs, defStyleAttr, iconDrawableRes).also { iconImageView = it })
    }

    private fun createButtonIcon(context: Context, attrs: AttributeSet?,
                                 defStyleAttr: Int, @DrawableRes resourceId: Int): ImageView {
        val imageView = ImageView(context, attrs, defStyleAttr)
        imageView.id = View.NO_ID
        imageView.setImageResource(resourceId)
        imageView.layoutParams =
                LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply { gravity = Gravity.CENTER }
        return imageView
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> onActionDown()
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                onActionUpOrCancel()
            }
        }
        return super.onTouchEvent(event)
    }

    private fun playShowAnimation() {
        hideAnimation.cancel()
        startAnimation(showAnimation)
    }

    private fun playHideAnimation() {
        showAnimation.cancel()
        startAnimation(hideAnimation)
    }

    internal fun setImageResource(@DrawableRes iconDrawableRes: Int) {
        iconImageView.setImageResource(iconDrawableRes)
    }

    internal fun setImageDrawable(drawable: Drawable?) {
        iconImageView.setImageDrawable(drawable)
    }

    fun show(animate: Boolean) {
        if (visibility == View.INVISIBLE) {
            if (animate) {
                playShowAnimation()
            }
            super.setVisibility(View.VISIBLE)
        }
    }

    fun hide(animate: Boolean) {
        if (visibility == View.VISIBLE) {
            if (animate) {
                playHideAnimation()
            }
            super.setVisibility(View.INVISIBLE)
        }
    }
}
