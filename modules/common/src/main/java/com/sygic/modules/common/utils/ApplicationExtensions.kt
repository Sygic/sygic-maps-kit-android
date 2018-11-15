package com.sygic.modules.common.utils

import android.app.Application
import android.content.pm.PackageManager

fun Application.getApiKey(): String? {
    return try {
        val applicationInfo = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
        applicationInfo.metaData.getString("com.sygic.ApiKey") //ToDO MS-4508
    } catch (e: PackageManager.NameNotFoundException) {
        null
    } catch (e: NullPointerException) {
        null
    }
}
