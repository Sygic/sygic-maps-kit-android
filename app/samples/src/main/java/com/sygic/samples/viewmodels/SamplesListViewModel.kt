package com.sygic.samples.viewmodels

import androidx.databinding.Bindable
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sygic.samples.CommonSampleActivity
import com.sygic.samples.adapters.SamplesRecyclerViewAdapter
import com.sygic.samples.models.Sample
import com.sygic.ui.common.extensions.asSingleEvent
import com.sygic.ui.common.livedata.SingleLiveEvent

class SamplesListViewModel(samples: List<Sample>) : ViewModel(), SamplesRecyclerViewAdapter.ClickListener {

    @Bindable
    val adapter: SamplesRecyclerViewAdapter = SamplesRecyclerViewAdapter()
    val startActivityObservable: LiveData<Class<out CommonSampleActivity>> = SingleLiveEvent()

    init {
        adapter.clickListener = this
        adapter.items = samples
    }

    override fun onSampleItemClick(sample: Sample) {
        startActivityObservable.asSingleEvent().value = sample.target
    }

    @Suppress("UNCHECKED_CAST")
    class Factory(private val samples: List<Sample>): ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return SamplesListViewModel(samples) as T
        }
    }
}