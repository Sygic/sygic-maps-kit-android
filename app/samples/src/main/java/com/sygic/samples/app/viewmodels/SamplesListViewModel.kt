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

package com.sygic.samples.app.viewmodels

import android.app.Application
import androidx.lifecycle.*
import com.sygic.maps.uikit.views.common.extensions.asMutable
import com.sygic.samples.app.activities.CommonSampleActivity
import com.sygic.samples.app.adapters.SamplesRecyclerViewAdapter
import com.sygic.samples.app.models.Sample
import com.sygic.maps.uikit.views.common.extensions.asSingleEvent
import com.sygic.maps.uikit.views.common.extensions.isLandscape
import com.sygic.maps.uikit.views.common.livedata.SingleLiveEvent
import com.sygic.samples.utils.GridLayoutSpanCount

class SamplesListViewModel(
    app: Application,
    samples: List<Sample>
) : AndroidViewModel(app), DefaultLifecycleObserver, SamplesRecyclerViewAdapter.ClickListener {

    val adapter = SamplesRecyclerViewAdapter()
    val spanCount: LiveData<Int> = MutableLiveData(GridLayoutSpanCount.PORTRAIT)
    val startActivityObservable: LiveData<Class<out CommonSampleActivity>> = SingleLiveEvent()

    init {
        adapter.clickListener = this
        adapter.items = samples
    }

    override fun onCreate(owner: LifecycleOwner) {
        spanCount.asMutable().value = if (isLandscape()) GridLayoutSpanCount.LANDSCAPE else GridLayoutSpanCount.PORTRAIT
    }

    override fun onSampleItemClick(sample: Sample) {
        startActivityObservable.asSingleEvent().value = sample.target
    }

    @Suppress("UNCHECKED_CAST")
    class Factory(private val app: Application, private val samples: List<Sample>) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return SamplesListViewModel(app, samples) as T
        }
    }
}