package com.sygic.maps.uikit.views.common.livedata

import androidx.annotation.MainThread

/**
 * A lifecycle-aware observable that sends only new updates after subscription, used for events like
 * navigation and Snackbar messages.
 *
 *
 * This avoids a common problem with events: on configuration change (like rotation) an update
 * can be emitted if the observer is active. This LiveData only calls the observable if there's an
 * explicit call to setValue() or call().
 *
 *
 * Note that only one observer is going to be notified of changes.
 */
@Suppress("unused")
open class SingleLiveEvent<T> : SingleLiveData<T>() {

    @MainThread
    public override fun setValue(t: T?) {
        super.setValue(t)
    }

    /**
     * Used for cases where T is Void, to make calls cleaner.
     */
    @MainThread
    fun call() {
        value = null
    }
}