package com.sygic.ui.viewmodel.poidetail

import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sygic.ui.common.sdk.data.PoiData
import com.sygic.ui.common.sdk.model.ExtendedMapDataModel

class PoiDetailViewModel(private val extendedMapDataModel: ExtendedMapDataModel) : ViewModel(), DefaultLifecycleObserver {

    //val rotation: MutableLiveData<Float> = MutableLiveData()

    private val poiDataObserver = Observer<PoiData> { poiData ->
        Log.d("Tomas", "called with: poiDataObserver = [$poiData]")
    }

    init {
        extendedMapDataModel.poiDataObservable.observeForever(poiDataObserver)
    }

    override fun onCleared() {
        extendedMapDataModel.poiDataObservable.removeObserver(poiDataObserver)
        super.onCleared()
    }

    class ViewModelFactory(private val extendedMapDataModel: ExtendedMapDataModel) :
        ViewModelProvider.NewInstanceFactory() {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return PoiDetailViewModel(extendedMapDataModel) as T
        }
    }
}