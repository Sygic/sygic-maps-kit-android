package com.sygic.modules.common.initialization.manager

import android.app.Application
import androidx.annotation.RestrictTo

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
interface SdkInitializationManager {

    interface Callback {
        fun onSdkInitialized()
    }

    fun initialize(application: Application, callback: Callback)
}