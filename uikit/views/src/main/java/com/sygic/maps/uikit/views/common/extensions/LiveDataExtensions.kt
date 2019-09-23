/*
 * Copyright (c) 2019 Sygic a.s. All rights reserved.
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
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.sygic.maps.uikit.views.common.livedata.SingleLiveEvent

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
fun <T : Any> LiveData<T>.asSingleEvent(): SingleLiveEvent<T> {
    return if (this is SingleLiveEvent<T>) this else throw IllegalArgumentException("$this is not an instance of SingleLiveEvent!")
}

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
fun <T : Any> LiveData<T>.asMutable(): MutableLiveData<T> {
    return if (this is MutableLiveData<T>) this else throw IllegalArgumentException("$this is not an instance of MutableLiveData!")
}

fun <A, B> LiveData<A>.combineLatest(with: LiveData<B>): LiveData<Pair<A, B>> {
    return MediatorLiveData<Pair<A, B>>().apply {
        var lastA: A? = null
        var lastB: B? = null

        addSource(this@combineLatest) {
            if (it == null && value != null) value = null
            lastA = it
            if (lastA != null && lastB != null) value = lastA!! to lastB!!
        }

        addSource(with) {
            if (it == null && value != null) value = null
            lastB = it
            if (lastA != null && lastB != null) value = lastA!! to lastB!!
        }
    }
}

fun <A, B, C> combineLatest(a: LiveData<A>, b: LiveData<B>, c: LiveData<C>): LiveData<Triple<A?, B?, C?>> {

    fun Triple<A?, B?, C?>?.replaceFirst(first: A?): Triple<A?, B?, C?> {
        if (this@replaceFirst == null) return Triple(first, null, null)
        return this@replaceFirst.copy(first = first)
    }

    fun Triple<A?, B?, C?>?.replaceSecond(second: B?): Triple<A?, B?, C?> {
        if (this@replaceSecond == null) return Triple(null, second, null)
        return this@replaceSecond.copy(second = second)
    }

    fun Triple<A?, B?, C?>?.replaceThird(third: C?): Triple<A?, B?, C?> {
        if (this@replaceThird == null) return Triple(null, null, third)
        return this@replaceThird.copy(third = third)
    }

    return MediatorLiveData<Triple<A?, B?, C?>>().apply {
        addSource(a) { value = value.replaceFirst(it) }
        addSource(b) { value = value.replaceSecond(it) }
        addSource(c) { value = value.replaceThird(it) }
    }
}

fun <A, B> LiveData<A>.withLatestFrom(from: LiveData<B>): LiveData<Pair<A, B>> {
    return MediatorLiveData<Pair<A, B>>().apply {
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
}