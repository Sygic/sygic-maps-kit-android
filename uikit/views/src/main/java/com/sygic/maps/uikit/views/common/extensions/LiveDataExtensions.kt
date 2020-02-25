/*
 * Copyright (c) 2020 Sygic a.s. All rights reserved.
 *
 * This project is licensed under the MIT License.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.sygic.maps.uikit.views.common.extensions

import androidx.annotation.RestrictTo
import androidx.lifecycle.*
import com.sygic.maps.uikit.views.common.livedata.SingleLiveEvent

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
fun <T : Any> LiveData<T>.asSingleEvent(): SingleLiveEvent<T> {
    return if (this is SingleLiveEvent<T>) this else throw IllegalArgumentException("$this is not an instance of SingleLiveEvent!")
}

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
fun <T : Any> LiveData<T>.asMutable(): MutableLiveData<T> {
    return if (this is MutableLiveData<T>) this else throw IllegalArgumentException("$this is not an instance of MutableLiveData!")
}

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
fun <T : Any> MutableLiveData<T>.notifyObservers() {
    this.value = this.value
}

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
fun <K, V> MutableLiveData<Map<K, V>>.put(key: K, newValue: V) {
    (this.value as MutableMap)[key] = newValue
    notifyObservers()
}

fun <T> LiveData<T>.observeOnce(observer: (T) -> Unit) {
    observeForever(object: Observer<T> {
        override fun onChanged(value: T) {
            removeObserver(this)
            observer(value)
        }
    })
}

fun <T> LiveData<T>.observeOnce(owner: LifecycleOwner, observer: (T) -> Unit) {
    observe(owner, object: Observer<T> {
        override fun onChanged(value: T) {
            removeObserver(this)
            observer(value)
        }
    })
}

/**
 * Combines the latest values from two LiveData objects.
 * First emits after both LiveData objects have emitted a value, and will emit afterwards after any
 * of them emits a new value.
 *
 * The difference between combineLatest and zip is that the zip only emits after all LiveData
 * objects have a new value, but combineLatest will emit after any of them has a new value.
 */
fun <A, B> combineLatest(first: LiveData<A>, second: LiveData<B>) = combineLatest(first, second) { a, b -> Pair(a, b) }

@Suppress("UNCHECKED_CAST")
fun <A, B, R> combineLatest(first: LiveData<A>, second: LiveData<B>, combineFunction: (A, B) -> R) = MediatorLiveData<R>().apply {

    var firstEmitted = false
    var firstValue: A? = null

    var secondEmitted = false
    var secondValue: B? = null

    addSource(first) {
        firstEmitted = true
        firstValue = it
        if (firstEmitted && secondEmitted) {
            value = combineFunction(firstValue as A, secondValue as B)
        }
    }

    addSource(second) {
        secondEmitted = true
        secondValue = it
        if (firstEmitted && secondEmitted) {
            value = combineFunction(firstValue as A, secondValue as B)
        }
    }
}

/**
 * Combines the latest values from two LiveData objects.
 * First emits after both LiveData objects have emitted a value, and will emit afterwards after any
 * of them emits a new value.
 *
 * The difference between combineLatest and zip is that the zip only emits after all LiveData
 * objects have a new value, but combineLatest will emit after any of them has a new value.
 */
fun <A, B, C> combineLatest(first: LiveData<A>, second: LiveData<B>, third: LiveData<C>) = combineLatest(first, second, third) { a, b, c -> Triple(a, b, c) }

@Suppress("UNCHECKED_CAST")
fun <A, B, C, R> combineLatest(first: LiveData<A>, second: LiveData<B>, third: LiveData<C>, combineFunction: (A, B, C) -> R) = MediatorLiveData<R>().apply {

    var firstEmitted = false
    var firstValue: A? = null

    var secondEmitted = false
    var secondValue: B? = null

    var thirdEmitted = false
    var thirdValue: C? = null

    addSource(first) {
        firstEmitted = true
        firstValue = it
        if (firstEmitted && secondEmitted && thirdEmitted) {
            value = combineFunction(firstValue as A, secondValue as B, thirdValue as C)
        }
    }

    addSource(second) {
        secondEmitted = true
        secondValue = it
        if (firstEmitted && secondEmitted && thirdEmitted) {
            value = combineFunction(firstValue as A, secondValue as B, thirdValue as C)
        }
    }

    addSource(third) {
        thirdEmitted = true
        thirdValue = it
        if (firstEmitted && secondEmitted && thirdEmitted) {
            value = combineFunction(firstValue as A, secondValue as B, thirdValue as C)
        }
    }
}

/**
 * zips both of the LiveData and emits a value after both of them have emitted their values,
 * after that, waits again for both LiveData to emit new value.
 *
 * The difference between combineLatest and zip is that the zip only emits after all LiveData
 * objects have a new value, but combineLatest will emit after any of them has a new value.
 */
fun <T, Y> zip(first: LiveData<T>, second: LiveData<Y>) = zip(first, second) { t, y -> Pair(t, y) }

fun <T, Y, Z> zip(first: LiveData<T>, second: LiveData<Y>, zipFunction: (T, Y) -> Z) = MediatorLiveData<Z>().apply {

    var firstEmitted = false
    var firstValue: T? = null

    var secondEmitted = false
    var secondValue: Y? = null

    addSource(first) {
        firstEmitted = true
        firstValue = it
        if (firstEmitted && secondEmitted) {
            value = zipFunction(firstValue!!, secondValue!!)
            firstEmitted = false
            secondEmitted = false
        }
    }

    addSource(second) {
        secondEmitted = true
        secondValue = it
        if (firstEmitted && secondEmitted) {
            value = zipFunction(firstValue!!, secondValue!!)
            firstEmitted = false
            secondEmitted = false
        }
    }
}

/**
 * zips three LiveData and emits a value after all of them have emitted their values,
 * after that, waits again for both LiveData to emit new value.
 *
 * The difference between combineLatest and zip is that the zip only emits after all LiveData
 * objects have a new value, but combineLatest will emit after any of them has a new value.
 */
fun <T, Y, X> zip(first: LiveData<T>, second: LiveData<Y>, third: LiveData<X>) = zip(first, second, third) { t, y, x -> Triple(t, y, x) }

fun <T, Y, X, Z> zip(first: LiveData<T>, second: LiveData<Y>, third: LiveData<X>, zipFunction: (T, Y, X) -> Z) = MediatorLiveData<Z>().apply {

    var firstEmitted = false
    var firstValue: T? = null

    var secondEmitted = false
    var secondValue: Y? = null

    var thirdEmitted = false
    var thirdValue: X? = null

    addSource(first) {
        firstEmitted = true
        firstValue = it
        if (firstEmitted && secondEmitted && thirdEmitted) {
            value = zipFunction(firstValue!!, secondValue!!, thirdValue!!)
            firstEmitted = false
            secondEmitted = false
            thirdEmitted = false
        }
    }

    addSource(second) {
        secondEmitted = true
        secondValue = it
        if (firstEmitted && secondEmitted && thirdEmitted) {
            firstEmitted = false
            secondEmitted = false
            thirdEmitted = false
            value = zipFunction(firstValue!!, secondValue!!, thirdValue!!)
        }
    }

    addSource(third) {
        thirdEmitted = true
        thirdValue = it
        if (firstEmitted && secondEmitted && thirdEmitted) {
            firstEmitted = false
            secondEmitted = false
            thirdEmitted = false
            value = zipFunction(firstValue!!, secondValue!!, thirdValue!!)
        }
    }
}

fun <A, B> LiveData<A>.withLatestFrom(from: LiveData<B>): LiveData<Pair<A, B>> = MediatorLiveData<Pair<A, B>>().apply {
    var lastA: A? = null
    var lastFrom: B? = null

    addSource(from) {
        if (lastFrom == null && lastA != null) value = lastA!! to it
        lastFrom = it
    }

    addSource(this@withLatestFrom) {
        if (it == null && value != null) value = null
        lastA = it
        if (lastFrom != null && it != null) value = it to lastFrom!!
    }
}
