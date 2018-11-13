package com.sygic.ui.view.zoomcontrols

import android.annotation.TargetApi
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.Outline
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.RippleDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.StateListDrawable
import android.graphics.drawable.shapes.OvalShape
import android.graphics.drawable.shapes.Shape
import android.os.Build
import android.support.annotation.CallSuper
import android.support.annotation.ColorInt
import android.support.annotation.DrawableRes
import android.support.v4.content.ContextCompat
import android.support.v7.widget.AppCompatImageButton
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.view.animation.Animation
import android.view.animation.AnimationUtils

import com.sygic.ui.common.*

internal abstract class ZoomControlsBaseButton @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : AppCompatImageButton(context, attrs, defStyleAttr) {

    @ColorInt
    protected val iconColor: Int
    @ColorInt
    private val backgroundColor: Int
    @ColorInt
    private val shadowColor: Int

    private val circleSize: Int
    private val iconSize: Int
    private val shadowRadius: Int

    private val showAnimation: Animation
    private val hideAnimation: Animation

    private var buttonIcon: Drawable? = null
    private var buttonBackgroundDrawable: Drawable? = null

    var interactionListener: ZoomControlsMenu.InteractionListener? = null

    @DrawableRes
    abstract fun iconDrawableRes(): Int
    abstract fun onActionUpOrCancel()

    init {
        val resources = resources
        circleSize = resources.getDimensionPixelSize(R.dimen.zoomControlFAB)
        iconSize = resources.getDimensionPixelSize(R.dimen.zoomControlIconSize)
        shadowRadius = resources.getDimensionPixelSize(R.dimen.zoomControlShadowRadius)

        val typedArray = resources.obtainAttributes(attrs, R.styleable.ZoomControlsMenu)
        try {
            iconColor = typedArray.getColor(R.styleable.ZoomControlsMenu_iconColor, ContextCompat.getColor(context, R.color.colorPrimary))
            backgroundColor = typedArray.getColor(R.styleable.ZoomControlsMenu_backgroundColor, ContextCompat.getColor(context, R.color.colorAccent))
            shadowColor = typedArray.getColor(R.styleable.ZoomControlsMenu_shadowColor, ContextCompat.getColor(context, R.color.black_a20))
        } finally {
            typedArray.recycle()
        }

        showAnimation = AnimationUtils.loadAnimation(context, R.anim.zoom_button_scale_up)
        hideAnimation = AnimationUtils.loadAnimation(context, R.anim.zoom_button_scale_down)

        setIconImage(iconDrawableRes())
        isClickable = true
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(circleSize + shadowRadius * 2, circleSize + shadowRadius * 2)
    }

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        super.onSizeChanged(width, height, oldWidth, oldHeight)
        updateBackground()
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    override fun setLayoutParams(params: ViewGroup.LayoutParams) {
        if (params is ViewGroup.MarginLayoutParams) {
            params.leftMargin += shadowRadius
            params.topMargin += shadowRadius
            params.rightMargin += shadowRadius
            params.bottomMargin += shadowRadius
        }
        super.setLayoutParams(params)
    }

    private fun updateBackground() {
        val iconDrawable = drawable
        val layerDrawable = LayerDrawable(arrayOf(Shadow(), createFillDrawable(), iconDrawable))

        var iconSize = -1
        if (iconDrawable != null) {
            iconSize = Math.max(iconDrawable.intrinsicWidth, iconDrawable.intrinsicHeight)
        }

        val iconOffset = (circleSize - if (iconSize > 0) iconSize else this.iconSize) / 2
        val bound = shadowRadius + iconOffset
        layerDrawable.setLayerInset(2, bound, bound, bound, bound)

        background = layerDrawable
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun createFillDrawable(): Drawable {
        val drawable = StateListDrawable()
        drawable.addState(intArrayOf(), createCircleDrawable(backgroundColor))

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val ripple = RippleDrawable(ColorStateList(arrayOf(intArrayOf()),
                    intArrayOf(ContextCompat.getColor(context, R.color.black_a20))), drawable, null)
            outlineProvider = object : ViewOutlineProvider() {
                override fun getOutline(view: View, outline: Outline) {
                    outline.setOval(0, 0, view.width, view.height)
                }
            }
            clipToOutline = true
            buttonBackgroundDrawable = ripple
            return ripple
        }

        buttonBackgroundDrawable = drawable
        return drawable
    }

    private fun createCircleDrawable(@ColorInt color: Int): Drawable {
        val shapeDrawable = CircleDrawable(OvalShape())
        shapeDrawable.paint.color = color
        return shapeDrawable
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

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @CallSuper
    internal open fun onActionDown() {
        if (buttonBackgroundDrawable is StateListDrawable) {
            val drawable = buttonBackgroundDrawable as StateListDrawable
            drawable.state = intArrayOf(android.R.attr.state_enabled, android.R.attr.state_pressed)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val ripple = buttonBackgroundDrawable as RippleDrawable
            ripple.state = intArrayOf(android.R.attr.state_enabled, android.R.attr.state_pressed)
            ripple.setHotspot((measuredWidth / 2).toFloat(), (measuredHeight / 2).toFloat())
            ripple.setVisible(true, true)
        }
    }

    private fun playShowAnimation() {
        hideAnimation.cancel()
        startAnimation(showAnimation)
    }

    private fun playHideAnimation() {
        showAnimation.cancel()
        startAnimation(hideAnimation)
    }

    private inner class CircleDrawable constructor(shape: Shape) : ShapeDrawable(shape) {

        override fun draw(canvas: Canvas) {
            setBounds(shadowRadius, shadowRadius, circleSize + shadowRadius,
                    circleSize + shadowRadius)
            super.draw(canvas)
        }
    }

    private inner class Shadow : Drawable() {

        private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        private val erase = Paint(Paint.ANTI_ALIAS_FLAG)
        private val radius: Float

        init {
            setLayerType(View.LAYER_TYPE_SOFTWARE, null)
            paint.style = Paint.Style.FILL
            paint.color = backgroundColor

            erase.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
            paint.setShadowLayer(shadowRadius.toFloat(), 0f, 0f, shadowColor)

            radius = (circleSize / 2).toFloat()
        }

        override fun draw(canvas: Canvas) {
            canvas.drawCircle((measuredWidth / 2).toFloat(), (measuredHeight / 2).toFloat(), radius, paint)
            canvas.drawCircle((measuredWidth / 2).toFloat(), (measuredHeight / 2).toFloat(), radius, erase)
        }

        override fun setAlpha(alpha: Int) {}
        override fun setColorFilter(cf: ColorFilter?) {}
        override fun getOpacity(): Int {
            return PixelFormat.UNKNOWN
        }
    }

    protected fun setIconImage(@DrawableRes iconDrawableId: Int) {
        setImageDrawable(this.getVectorDrawableWithTint(iconDrawableId, iconColor))
    }

    override fun setImageDrawable(drawable: Drawable?) {
        if (buttonIcon !== drawable) {
            buttonIcon = drawable
            updateBackground()
        }
    }

    override fun getDrawable(): Drawable? {
        if (buttonIcon == null) {
            return ColorDrawable(Color.TRANSPARENT)
        }

        return buttonIcon
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
