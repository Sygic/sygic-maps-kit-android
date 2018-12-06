package com.sygic.ui.common.sdk.location.livedata

import android.app.Activity
import android.content.Context
import androidx.annotation.MainThread
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.sygic.ui.common.livedata.SingleLiveData
import com.sygic.ui.common.locationManager


class LocationProviderCheckLiveEvent : SingleLiveData<String>() {

    private lateinit var observer: Observer<Boolean>

    fun observe(owner: LifecycleOwner) {
        this.observe(owner, Observer { })
    }

    override fun observe(owner: LifecycleOwner, observer: Observer<in String>) {
        super.observe(owner, Observer { data ->
            val context: Context? = when (owner) {
                is Activity -> owner
                is Fragment -> owner.context
                else -> throw  NotImplementedError("Unexpected LifecycleOwner ${owner::class}! Only Activity and Fragment are supported as LifecycleOwner by now.")
            }

            this.observer.onChanged(evaluate(context, value))
            observer.onChanged(data)
        })
    }

    @MainThread
    fun checkEnabled(provider: String, observer: Observer<Boolean>) {
        this.observer = observer
        value = provider
    }

    private fun evaluate(context: Context?, provider: String?): Boolean {
        return provider?.let { context?.locationManager?.isProviderEnabled(it) ?: false } ?: false
    }
}