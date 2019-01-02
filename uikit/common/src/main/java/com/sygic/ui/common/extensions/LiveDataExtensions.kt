package com.sygic.ui.common.extensions

import androidx.annotation.RestrictTo
import androidx.lifecycle.LiveData
import com.sygic.ui.common.livedata.SingleLiveEvent

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
fun <T : Any> LiveData<T>.asSingleEvent(): SingleLiveEvent<T> {
    return if (this is SingleLiveEvent<T>) this else throw IllegalArgumentException("$this is not an instance of SingleLiveEvent!")
}