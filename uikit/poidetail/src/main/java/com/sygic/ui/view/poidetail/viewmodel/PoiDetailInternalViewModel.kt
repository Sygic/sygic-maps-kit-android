package com.sygic.ui.view.poidetail.viewmodel

import androidx.lifecycle.*
import com.sygic.ui.common.sdk.data.PoiData

class PoiDetailInternalViewModel : ViewModel() {

    val titleText: MutableLiveData<String> = MutableLiveData()
    val subtitleText: MutableLiveData<String> = MutableLiveData()

    private val poiDataObserver = Observer<PoiData> { poiData ->
        //poiData.name?.let { titleText.value = it } //todo
        //subtitleText.value = String.format("%s, %s", poiData.street, poiData.city) //todo
    }

    init {
        titleText.value = "" //todo
    }

    fun addObservable(poiData: MutableLiveData<PoiData>) {
        poiData.observeForever(poiDataObserver)
    }

    fun removeObservable(poiData: MutableLiveData<PoiData>) {
        poiData.removeObserver(poiDataObserver)
    }

    class ViewModelFactory :
        ViewModelProvider.NewInstanceFactory() {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return PoiDetailInternalViewModel() as T
        }
    }
}