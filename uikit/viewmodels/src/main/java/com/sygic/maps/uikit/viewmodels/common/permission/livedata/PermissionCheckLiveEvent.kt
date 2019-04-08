package com.sygic.maps.uikit.viewmodels.common.permission.livedata

import android.app.Activity
import android.content.pm.PackageManager
import androidx.annotation.MainThread
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.sygic.maps.uikit.views.common.livedata.SingleLiveData

open class PermissionCheckLiveEvent : SingleLiveData<String>() {

    private lateinit var observer: Observer<Boolean>

    fun observe(owner: LifecycleOwner) {
        this.observe(owner, Observer { })
    }

    override fun observe(owner: LifecycleOwner, observer: Observer<in String>) {
        super.observe(owner, Observer { data ->
            val activity: Activity? = when (owner) {
                is Activity -> owner
                is Fragment -> owner.activity
                else -> throw  NotImplementedError("Unexpected LifecycleOwner ${owner::class}! Only Activity and Fragment are supported as LifecycleOwner by now.")
            }

            this.observer.onChanged(evaluate(activity, value))
            observer.onChanged(data)
        })
    }

    @MainThread
    fun check(permission: String, observer: Observer<Boolean>) {
        this.observer = observer
        value = permission
    }

    protected open fun evaluate(activity: Activity?, permission: String?): Boolean {
        if (activity != null && permission != null) {
            return ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED
        }

        return false
    }
}