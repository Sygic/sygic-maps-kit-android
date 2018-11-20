package com.sygic.modules.common.manager

import androidx.annotation.RestrictTo

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
internal interface SdkInitializationManager {

    interface Callback {
        fun onSdkInitialized()
    }

    fun initialize(callback: Callback)
}