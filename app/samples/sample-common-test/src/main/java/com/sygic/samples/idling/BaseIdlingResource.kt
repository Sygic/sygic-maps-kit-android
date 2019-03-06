package com.sygic.samples.idling

import androidx.test.espresso.IdlingResource
import com.sygic.samples.CommonSampleActivity

abstract class BaseIdlingResource(protected val activity: CommonSampleActivity) : IdlingResource {

    protected var callback: IdlingResource.ResourceCallback? = null

    override fun registerIdleTransitionCallback(callback: IdlingResource.ResourceCallback?) {
        this.callback = callback
    }
}
