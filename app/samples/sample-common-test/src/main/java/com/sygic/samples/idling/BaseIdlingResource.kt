package com.sygic.samples.idling

import androidx.test.espresso.IdlingResource
import com.sygic.samples.CommonSampleActivity

abstract class BaseIdlingResource(protected val activity: CommonSampleActivity) : IdlingResource {

    private var callback: IdlingResource.ResourceCallback? = null

    abstract fun isIdle(): Boolean

    override fun registerIdleTransitionCallback(callback: IdlingResource.ResourceCallback?) {
        this.callback = callback
    }

    final override fun isIdleNow(): Boolean {
        if (isIdle()) {
            callback?.onTransitionToIdle()
            return true
        }

        return false
    }
}
