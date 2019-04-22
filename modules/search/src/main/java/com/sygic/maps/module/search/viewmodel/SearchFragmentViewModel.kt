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

package com.sygic.maps.module.search.viewmodel

import android.app.Application
import androidx.annotation.RestrictTo
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.MutableLiveData
import com.sygic.maps.module.search.component.SearchFragmentInitComponent
import com.sygic.maps.module.search.extensions.resolveAttributes
import com.sygic.maps.tools.annotations.Assisted
import com.sygic.maps.tools.annotations.AutoFactory
import com.sygic.sdk.position.GeoCoordinates

@AutoFactory
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
class SearchFragmentViewModel internal constructor(
    app: Application,
    @Assisted initComponent: SearchFragmentInitComponent
) : AndroidViewModel(app), DefaultLifecycleObserver {

    val initialSearchInput: MutableLiveData<String> = MutableLiveData()
    val initialSearchPosition: MutableLiveData<GeoCoordinates> = MutableLiveData()

    init {
        initComponent.resolveAttributes(app)
        initialSearchInput.value = initComponent.initialSearchInput
        initialSearchPosition.value = initComponent.initialSearchPosition
        initComponent.recycle()
    }
}
