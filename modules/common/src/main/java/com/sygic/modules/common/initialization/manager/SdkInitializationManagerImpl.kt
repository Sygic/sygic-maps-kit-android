package com.sygic.modules.common.initialization.manager

import android.app.Application
import androidx.annotation.RestrictTo
import com.sygic.modules.common.utils.getApiKey
import com.sygic.sdk.SygicEngine
import java.util.*

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
class SdkInitializationManagerImpl : SdkInitializationManager,
    SygicEngine.OnInitListener {

    private var initialized = false
    private val callbacks = LinkedHashSet<SdkInitializationManager.Callback>()

    override fun initialize(application: Application, callback: SdkInitializationManager.Callback) {
        synchronized(this) {
            if (initialized) {
                callback.onSdkInitialized()
                return
            }

            callbacks.add(callback)
        }

        application.getApiKey()?.let { key ->
            SygicEngine.Builder(application)
                .setKeyAndSecret(application.packageName, key)
                .setInitListener(this).init()
        }
    }

    override fun onSdkInitialized() {
        synchronized(this) {
            initialized = true
        }
        callbacks.forEach { it.onSdkInitialized() }
        callbacks.clear()
    }

    override fun onError(@SygicEngine.OnInitListener.InitError error: Int) {
        /* Currently do nothing */
    }
}