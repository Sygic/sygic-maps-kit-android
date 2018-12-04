package com.sygic.ui.view.zoomcontrols

import android.content.Context
import android.os.Handler
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup

import com.sygic.ui.common.extensions.isRtl

@Suppress("unused", "MemberVisibilityCanBePrivate")
class ZoomControlsMenu : ViewGroup, ZoomControlsMenuButton.MenuCallback {

    companion object {
        private const val ANIMATION_DELAY_PER_ITEM = 50
    }

    private var maxButtonHeight: Int = 0
    private var buttonsCount: Int = 0

    private var isMenuOpening: Boolean = false
    private var isMenuOpened: Boolean = false

    private val uiHandler = Handler()

    private lateinit var menuButton: ZoomControlsMenuButton
    private lateinit var zoomControlsMapViewModeButton: ZoomControlsMapViewModeButton

    private var isRtl = false

    interface InteractionListener {
        fun onZoomInStart()
        fun onZoomInStop()
        fun onZoomOutStart()
        fun onZoomOutStop()
        fun onCameraProjectionChanged()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet) {
        val defaultLayoutParams = super.generateDefaultLayoutParams()
        isRtl = context.isRtl()

        addView(createMenuButton(context, attrs), defaultLayoutParams)
        addView(createMapModeMenuButton(context, attrs), defaultLayoutParams)
        addView(ZoomControlsZoomInButton(context, attrs), defaultLayoutParams)
        addView(ZoomControlsZoomOutButton(context, attrs), defaultLayoutParams)
    }

    private fun createMenuButton(context: Context, attrs: AttributeSet): ZoomControlsMenuButton {
        menuButton = ZoomControlsMenuButton(context, attrs, callback = this)
        return menuButton
    }

    private fun createMapModeMenuButton(context: Context, attrs: AttributeSet): ZoomControlsMapViewModeButton {
        zoomControlsMapViewModeButton = ZoomControlsMapViewModeButton(context, attrs)
        return zoomControlsMapViewModeButton
    }

    fun setTiltType(@TiltType tiltType: Int) {
        zoomControlsMapViewModeButton.cameraProjectionChanged(tiltType)
    }

    fun setInteractionListener(interactionListener: InteractionListener?) {
        for (i in 0 until childCount) {
            (getChildAt(i) as ZoomControlsBaseButton).interactionListener = interactionListener
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var width = 0
        maxButtonHeight = 0

        for (i in 0 until buttonsCount) {
            val child = getChildAt(i)

            if (child.visibility == View.GONE) continue

            measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0)
            maxButtonHeight = Math.max(maxButtonHeight, child.measuredHeight)
            width += child.measuredWidth
        }

        width += paddingTop + paddingBottom
        width = adjustForOvershoot(width)

        val height = maxButtonHeight + paddingTop + paddingBottom
        setMeasuredDimension(width, height)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val buttonsVerticalCenter = bottom - top - maxButtonHeight / 2 - paddingBottom
        val menuButtonTop = bottom - top - menuButton.measuredHeight - paddingBottom
        val menuButtonLeft = if (isRtl)
            right - left - menuButton.measuredWidth - paddingRight
        else
            buttonsVerticalCenter - menuButton.measuredWidth / 2

        menuButton.layout(menuButtonLeft, menuButtonTop, menuButtonLeft + menuButton.measuredWidth,
                menuButtonTop + menuButton.measuredHeight)

        var nextX = menuButtonLeft
        for (i in buttonsCount - 1 downTo 0) {
            val child = getChildAt(i)

            val controlsBaseButton = child as ZoomControlsBaseButton
            if (controlsBaseButton.visibility == View.GONE) continue

            val childY = buttonsVerticalCenter - controlsBaseButton.measuredHeight / 2
            if (controlsBaseButton !== menuButton) {
                controlsBaseButton.layout(nextX, childY, nextX + controlsBaseButton.measuredWidth,
                        childY + controlsBaseButton.measuredHeight)

                if (!isMenuOpening) {
                    controlsBaseButton.hide(false)
                }
            }

            nextX = if (isRtl)
                nextX - controlsBaseButton.measuredWidth
            else
                nextX + controlsBaseButton.measuredWidth
        }
    }

    private fun adjustForOvershoot(dimension: Int): Int {
        return (dimension * 0.03 + dimension).toInt()
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        bringChildToFront(menuButton)
        buttonsCount = childCount
    }

    override fun generateLayoutParams(attributeSet: AttributeSet): ViewGroup.MarginLayoutParams {
        return ViewGroup.MarginLayoutParams(context, attributeSet)
    }

    override fun generateLayoutParams(layoutParams: ViewGroup.LayoutParams): ViewGroup.MarginLayoutParams {
        return ViewGroup.MarginLayoutParams(layoutParams)
    }

    override fun generateDefaultLayoutParams(): ViewGroup.MarginLayoutParams {
        return ViewGroup.MarginLayoutParams(ViewGroup.MarginLayoutParams.WRAP_CONTENT, ViewGroup.MarginLayoutParams.WRAP_CONTENT)
    }

    override fun checkLayoutParams(layoutParams: ViewGroup.LayoutParams): Boolean {
        return layoutParams is ViewGroup.MarginLayoutParams
    }

    override fun toggleMenu() {
        if (isMenuOpened) {
            close(true)
        } else {
            open(true)
        }
    }

    fun open(animate: Boolean) {
        if (!isMenuOpened) {
            menuButton.onMenuAction(true)

            var delay = 0
            var counter = 0
            isMenuOpening = true
            for (i in childCount - 1 downTo 0) {
                val child = getChildAt(i)
                if (child is ZoomControlsBaseButton && child.getVisibility() != View.GONE) {
                    counter++

                    uiHandler.postDelayed(Runnable {
                        if (isMenuOpened) return@Runnable

                        if (child !== menuButton) {
                            child.show(animate)
                        }
                    }, delay.toLong())
                    delay += ANIMATION_DELAY_PER_ITEM
                }
            }

            uiHandler.postDelayed({ isMenuOpened = true }, (++counter * ANIMATION_DELAY_PER_ITEM).toLong())
        }
    }

    fun close(animate: Boolean) {
        if (isMenuOpened) {
            menuButton.onMenuAction(false)

            var delay = 0
            var counter = 0
            isMenuOpening = false
            for (i in 0 until childCount) {
                val child = getChildAt(i)
                if (child is ZoomControlsBaseButton && child.getVisibility() != View.GONE) {
                    counter++

                    uiHandler.postDelayed(Runnable {
                        if (!isMenuOpened) return@Runnable

                        if (child !== menuButton) {
                            child.hide(animate)
                        }
                    }, delay.toLong())
                    delay += ANIMATION_DELAY_PER_ITEM
                }
            }

            uiHandler.postDelayed({ isMenuOpened = false }, (++counter * ANIMATION_DELAY_PER_ITEM).toLong())
        }
    }
}
