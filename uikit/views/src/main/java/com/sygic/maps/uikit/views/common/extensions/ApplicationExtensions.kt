package com.sygic.maps.uikit.views.common.extensions

import android.app.Application
import com.sygic.maps.uikit.views.R

fun Application.versionName(): String = packageManager.getPackageInfo(packageName, 0).versionName
fun Application.versionCode(): String = packageManager.getPackageInfo(packageName, 0).versionCode.toString()

fun Application.abiType(): String {
    return resources.getIntArray(R.array.all_abis_ids).toTypedArray()
        .zip(resources.getStringArray(R.array.all_abis_values))
        .toMap()[Integer.parseInt(versionCode().substring(0, 1))]?.let { it } ?: EMPTY_STRING
}
