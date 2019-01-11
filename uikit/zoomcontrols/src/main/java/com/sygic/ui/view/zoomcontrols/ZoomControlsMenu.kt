package com.sygic.ui.view.zoomcontrols

import android.content.Context
import android.os.Handler
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import com.sygic.ui.common.extensions.isRtl

private const val ANIMATION_DELAY_PER_ITEM = 50

@Suppress("unused", "MemberVisibilityCanBePrivate")
class ZoomControlsMenu @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.zoomControlsMenuStyle
) : LinearLayout(context, attrs, 0), ZoomControlsMenuButton.MenuCallback {

    private val uiHandler = Handler()
    private var isMenuOpened: Boolean = false

    private val menuButton: ZoomControlsMenuButton
    private val internalButtonMargin: Int = resources.getDimensionPixelSize(R.dimen.zoomControlsButtonMargin)

    interface InteractionListener {
        fun onZoomInStart()
        fun onZoomInStop()
        fun onZoomOutStart()
        fun onZoomOutStop()
        fun onCameraProjectionChanged()
    }

    init {
        orientation = HORIZONTAL
        clipChildren = false

        addView(
            ZoomControlsMenuButton(context, attrs, defStyleAttr, this).also { menuButton = it },
            LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        )

        val layoutParams = getChildLayoutParams()
        addView(ZoomControlsZoomOutButton(context, attrs, defStyleAttr).apply { hide(false) }, layoutParams)
        addView(ZoomControlsZoomInButton(context, attrs, defStyleAttr).apply { hide(false) }, layoutParams)
        addView(ZoomControlsMapViewModeButton(context, attrs, defStyleAttr).apply { hide(false) }, layoutParams)
    }

    private fun getChildLayoutParams(): LayoutParams {
        return LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
            context.isRtl().let {
                setMargins(if (!it) internalButtonMargin else 0, 0, if (it) internalButtonMargin else 0, 0)
            }
        }
    }

    fun setTiltType(@TiltType tiltType: Int) {
        for (i in 0 until childCount) {
            getChildAt(i).let { if (it is ZoomControlsMapViewModeButton) it.cameraProjectionChanged(tiltType) }
        }
    }

    fun setInteractionListener(interactionListener: InteractionListener?) {
        for (i in 0 until childCount) {
            (getChildAt(i) as BaseZoomControlsButton).interactionListener = interactionListener
        }
    }

    override fun toggleMenu() {
        if (isMenuOpened) {
            close(true)
        } else {
            open(true)
        }
    }

    fun open(animate: Boolean) {
        if (isMenuOpened) {
            return
        }

        menuButton.onMenuAction(true)

        var delay = 0L
        var counter = 0L
        for (i in 0 until childCount) {
            getChildAt(i).let {
                if (it is BaseZoomControlsButton && it.visibility != View.GONE) {
                    counter++

                    uiHandler.postDelayed(Runnable {
                        if (isMenuOpened) return@Runnable
                        if (it !is ZoomControlsMenuButton) it.show(animate)
                    }, delay)
                    delay += ANIMATION_DELAY_PER_ITEM
                }
            }
        }

        uiHandler.postDelayed({ isMenuOpened = true }, (++counter * ANIMATION_DELAY_PER_ITEM))
    }

    fun close(animate: Boolean) {
        if (!isMenuOpened) {
            return
        }

        menuButton.onMenuAction(false)

        var delay = 0L
        var counter = 0L
        for (i in childCount - 1 downTo 0) {
            getChildAt(i).let {
                if (it is BaseZoomControlsButton && it.visibility != View.GONE) {
                    counter++

                    uiHandler.postDelayed(Runnable {
                        if (!isMenuOpened) return@Runnable
                        if (it !is ZoomControlsMenuButton) it.hide(animate)
                    }, delay)
                    delay += ANIMATION_DELAY_PER_ITEM
                }
            }
        }

        uiHandler.postDelayed({ isMenuOpened = false }, (++counter * ANIMATION_DELAY_PER_ITEM))
    }
}
