package com.sygic.ui.common.listeners

import androidx.annotation.RestrictTo

@FunctionalInterface
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
interface DialogFragmentListener {
    fun onDismiss()
}