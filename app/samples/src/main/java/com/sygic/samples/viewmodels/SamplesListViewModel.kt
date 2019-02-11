package com.sygic.samples.viewmodels

import androidx.databinding.Bindable
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.sygic.samples.CommonSampleActivity
import com.sygic.samples.adapters.SamplesRecyclerViewAdapter
import com.sygic.samples.models.Sample
import com.sygic.ui.common.extensions.asSingleEvent
import com.sygic.ui.common.livedata.SingleLiveEvent

class SamplesListViewModel : ViewModel(), SamplesRecyclerViewAdapter.ClickListener {

    @Bindable
    val adapter: SamplesRecyclerViewAdapter = SamplesRecyclerViewAdapter()

    val startActivityObservable: LiveData<Class<out CommonSampleActivity>> = SingleLiveEvent()

    init {
        adapter.clickListener = this
    }

    override fun onSampleItemClick(sample: Sample) {
        startActivityObservable.asSingleEvent().value = sample.target
    }
}