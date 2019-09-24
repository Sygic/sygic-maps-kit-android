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

package com.sygic.maps.uikit.viewmodels.common.permission.livedata

import android.app.Activity
import android.content.pm.PackageManager
import androidx.annotation.MainThread
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.sygic.maps.uikit.viewmodels.common.extensions.activity
import com.sygic.maps.uikit.views.common.livedata.SingleLiveData

open class PermissionCheckLiveEvent : SingleLiveData<String>() {

    private lateinit var observer: Observer<Boolean>

    fun observe(owner: LifecycleOwner) {
        this.observe(owner, Observer { })
    }

    override fun observe(owner: LifecycleOwner, observer: Observer<in String>) {
        super.observe(owner, Observer { data ->
            val activity: Activity? = owner.activity()

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