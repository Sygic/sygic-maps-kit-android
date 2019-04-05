package com.sygic.samples.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sygic.samples.BuildConfig
import com.sygic.ui.common.extensions.abiType
import com.sygic.ui.common.extensions.versionCode
import com.sygic.ui.common.extensions.versionName

internal class AboutDialogViewModel(app: Application) : AndroidViewModel(app) {

    val sampleAppVersionName: String = app.versionName()
    val sampleAppVersionCode: String = app.versionCode()
    val sygicSdkVersion: String = BuildConfig.SDK_VERSION
    val abiType: String = app.abiType()

    class ViewModelFactory(private val app: Application) : ViewModelProvider.NewInstanceFactory() {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return AboutDialogViewModel(app) as T
        }
    }
}