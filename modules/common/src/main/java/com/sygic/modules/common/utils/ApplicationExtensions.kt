package com.sygic.modules.common.utils

import android.app.Application
import android.content.pm.PackageManager
import androidx.annotation.RestrictTo
import com.sygic.modules.common.R

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
fun Application.getApiKey(): String? {
    return try {
        val applicationInfo = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
        applicationInfo.metaData.getString(getString(R.string.com_sygic_api_key))
    } catch (e: PackageManager.NameNotFoundException) {
        null
    } catch (e: NullPointerException) {
        null
    }
}
