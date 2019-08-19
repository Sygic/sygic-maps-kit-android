/*
 * Copyright (c) 2019 Sygic a.s. All rights reserved.
 *
 * This project is licensed under the MIT License.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.sygic.maps.uikit.views.poidetail.dialog

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.view.Window
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.annotation.LayoutRes
import androidx.annotation.RestrictTo
import androidx.annotation.StyleRes
import androidx.appcompat.app.AppCompatDialog
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.AccessibilityDelegateCompat
import androidx.core.view.ViewCompat
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat
import com.google.android.material.R.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.sygic.maps.uikit.views.poidetail.behavior.BottomSheetBehaviorWrapper

/**
 * Friendly and more usable alternative to the official material BottomSheetDialog with available BottomSheetBehavior
 * and other minor improvements.
 */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
class BottomSheetDialog @JvmOverloads constructor(
    context: Context,
    @StyleRes theme: Int,
    private val initialPeekHeight: Int? = null,
    private val initialState: Int = BottomSheetBehavior.STATE_COLLAPSED
) : AppCompatDialog(context, getSubThemeResId(context, theme)),
    DialogInterface.OnShowListener,
    BottomSheetBehaviorWrapper.StateListener {

    var behavior: BottomSheetBehaviorWrapper? = null
        private set
    private var cancelable: Boolean = true
    private var canceledOnTouchOutside: Boolean = true
    private var canceledOnTouchOutsideSet: Boolean = false

    init {
        setOnShowListener(this)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window?.let {
            it.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            it.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            it.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        }
    }

    override fun setContentView(view: View) {
        super.setContentView(wrapInBottomSheet(0, view, null))
    }

    override fun setContentView(view: View, params: LayoutParams?) {
        super.setContentView(wrapInBottomSheet(0, view, params))
    }

    override fun setContentView(@LayoutRes layoutResId: Int) {
        super.setContentView(wrapInBottomSheet(layoutResId, null, null))
    }

    override fun setCancelable(cancelable: Boolean) {
        super.setCancelable(cancelable)

        if (this.cancelable != cancelable) {
            this.cancelable = cancelable
            behavior?.isHideable = cancelable
        }
    }

    override fun onStart() {
        super.onStart()

        behavior?.let {
            if (it.state != initialState) {
                it.state = initialState
            }
        }
    }

    override fun setCanceledOnTouchOutside(cancel: Boolean) {
        super.setCanceledOnTouchOutside(cancel)
        if (cancel && !cancelable) {
            cancelable = true
        }

        canceledOnTouchOutside = cancel
        canceledOnTouchOutsideSet = true
    }

    override fun onStateChanged(@BottomSheetBehavior.State newState: Int) {
        if (newState == BottomSheetBehavior.STATE_HIDDEN) {
            cancel()
        }
    }

    override fun onShow(dialog: DialogInterface) {
        behavior?.notifyStateChanged(initialState)
    }

    @SuppressLint("PrivateResource")
    private fun wrapInBottomSheet(layoutResId: Int, view: View?, params: LayoutParams?): View {
        val container = View.inflate(context, layout.design_bottom_sheet_dialog, null) as FrameLayout
        val coordinator = container.findViewById<CoordinatorLayout>(id.coordinator)
        val bottomSheet = coordinator.findViewById<FrameLayout>(id.design_bottom_sheet)

        behavior = BottomSheetBehaviorWrapper(BottomSheetBehavior.from(bottomSheet)).apply {
            addStateListener(this@BottomSheetDialog)
            isHideable = cancelable
            initialPeekHeight?.let { peekHeight = initialPeekHeight }
        }
        params?.let {
            bottomSheet.addView(getInflatedView(layoutResId, view, coordinator), params)
        } ?: bottomSheet.addView(getInflatedView(layoutResId, view, coordinator))

        coordinator.findViewById<View>(id.touch_outside).setOnClickListener {
            if (cancelable && isShowing && shouldWindowCloseOnTouchOutside()) {
                cancel()
            }
        }
        ViewCompat.setAccessibilityDelegate(bottomSheet, object : AccessibilityDelegateCompat() {
            override fun onInitializeAccessibilityNodeInfo(host: View, info: AccessibilityNodeInfoCompat) {
                super.onInitializeAccessibilityNodeInfo(host, info)
                if (cancelable) {
                    info.addAction(AccessibilityNodeInfoCompat.ACTION_DISMISS)
                    info.isDismissable = true
                } else {
                    info.isDismissable = false
                }
            }

            override fun performAccessibilityAction(host: View, action: Int, args: Bundle): Boolean {
                return if (action == AccessibilityNodeInfoCompat.ACTION_DISMISS && cancelable) {
                    cancel()
                    true
                } else {
                    super.performAccessibilityAction(host, action, args)
                }
            }
        })
        bottomSheet.setOnTouchListener { _, _ -> true }
        return container
    }

    private fun getInflatedView(layoutResId: Int, view: View?, coordinator: CoordinatorLayout): View? {
        view?.let {
            return it
        }

        if (layoutResId != 0) {
            return layoutInflater.inflate(layoutResId, coordinator, false)
        }

        return null
    }

    private fun shouldWindowCloseOnTouchOutside(): Boolean {
        if (!canceledOnTouchOutsideSet) {
            @Suppress("Recycle")
            context.obtainStyledAttributes(intArrayOf(android.R.attr.windowCloseOnTouchOutside)).also {
                canceledOnTouchOutside = it.getBoolean(0, true)
                canceledOnTouchOutsideSet = true
            }.recycle()
        }

        return canceledOnTouchOutside
    }
}

@SuppressLint("PrivateResource")
private fun getSubThemeResId(context: Context, themeId: Int): Int {
    if (themeId == 0) {
        val outValue = TypedValue()
        return if (context.theme.resolveAttribute(attr.bottomSheetDialogTheme, outValue, true)) {
            outValue.resourceId
        } else {
            style.Theme_Design_Light_BottomSheetDialog
        }
    }

    return themeId
}