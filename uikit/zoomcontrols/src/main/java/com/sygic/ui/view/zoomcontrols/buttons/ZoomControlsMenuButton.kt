package com.sygic.ui.view.zoomcontrols.buttons

import android.content.Context
import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import com.sygic.ui.view.zoomcontrols.R

internal class ZoomControlsMenuButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null,
    defStyleAttr: Int = 0, private val callback: MenuCallback? = null
) : BaseZoomControlsButton(context, attrs, defStyleAttr, iconDrawableRes = R.drawable.ic_plus_minus) {

    private val openAnimation: Drawable? = ContextCompat.getDrawable(context, R.drawable.vector_morph_plus_minus)
    private val closeAnimation: Drawable? = ContextCompat.getDrawable(context, R.drawable.vector_morph_cross)

    interface MenuCallback {
        fun toggleMenu()
    }

    override fun onActionUpOrCancel() {
        callback?.toggleMenu()
    }

    fun onMenuAction(open: Boolean) {
        if (open) {
            runAnimation(openAnimation, closeAnimation)
        } else {
            runAnimation(closeAnimation, openAnimation)
        }
    }

    private fun runAnimation(animationToStart: Drawable?, animationToStop: Drawable?) {
        if (animationToStop is AnimatedVectorDrawable) animationToStop.stop()
        setImageDrawable(animationToStart)
        if (animationToStart is AnimatedVectorDrawable) animationToStart.start()
    }
}
