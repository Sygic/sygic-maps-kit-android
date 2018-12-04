package com.sygic.ui.common.sdk.permission.livedata

import android.app.Activity
import androidx.core.app.ActivityCompat


class PermissionRationaleLiveEvent : PermissionCheckLiveEvent() {

    override fun evaluate(activity: Activity?, permission: String?): Boolean {
        if (activity != null && permission != null) {
            return ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)
        }

        return false
    }
}