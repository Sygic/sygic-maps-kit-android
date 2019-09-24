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

package com.sygic.maps.uikit.viewmodels.common.location.livedata

import android.content.Context
import androidx.annotation.MainThread
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.sygic.maps.uikit.viewmodels.common.extensions.context
import com.sygic.maps.uikit.views.common.extensions.locationManager
import com.sygic.maps.uikit.views.common.livedata.SingleLiveData

class LocationProviderCheckLiveEvent : SingleLiveData<String>() {

    private lateinit var observer: Observer<Boolean>

    fun observe(owner: LifecycleOwner) {
        this.observe(owner, Observer { })
    }

    override fun observe(owner: LifecycleOwner, observer: Observer<in String>) {
        super.observe(owner, Observer { data ->
            val context: Context? = owner.context()

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