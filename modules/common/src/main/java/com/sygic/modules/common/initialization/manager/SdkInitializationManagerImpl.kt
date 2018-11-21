package com.sygic.modules.common.initialization.manager

import android.app.Application
import androidx.annotation.RestrictTo
import com.sygic.modules.common.R
import com.sygic.modules.common.utils.getApiKey
import com.sygic.sdk.SygicEngine
import java.util.LinkedHashSet

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
internal class SdkInitializationManagerImpl : SdkInitializationManager,
    SygicEngine.OnInitListener {

    private var initialized = false
    private val callbacks = LinkedHashSet<SdkInitializationManager.Callback>()

    override fun initialize(application: Application, callback: SdkInitializationManager.Callback) {
        if (initialized) {
            callback.onSdkInitialized()
            return
        }

        callbacks.add(callback)
        application.getApiKey()?.let { key ->
            SygicEngine.Builder(application.getString(R.string.com_sygic_secret), key, application)
                .setInitListener(this).init()
        }
    }

    override fun onSdkInitialized() {
        initialized = true
        callbacks.forEach { it.onSdkInitialized() }
        callbacks.clear()
    }

    override fun onError(p0: Int) {
        /* Currently do nothing */
    }
}