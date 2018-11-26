package com.sygic.ui.viewmodel.poidetail

import androidx.lifecycle.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.sygic.ui.common.sdk.data.PoiData
import com.sygic.ui.common.sdk.model.ExtendedMapDataModel

class PoiDetailViewModel(private val extendedMapDataModel: ExtendedMapDataModel) : ViewModel(), DefaultLifecycleObserver {

    val poiData: MutableLiveData<PoiData> = MutableLiveData()
    val poiDetailState: MutableLiveData<Int> = MutableLiveData()

    private val poiDataObserver = Observer<PoiData> { poiData ->
        this.poiData.value = poiData
        if (poiData.isEmpty()) {
            poiDetailState.value = BottomSheetBehavior.STATE_HIDDEN
        } else {
            poiDetailState.value = BottomSheetBehavior.STATE_COLLAPSED
        }
    }

    init {
        poiDetailState.value = BottomSheetBehavior.STATE_HIDDEN
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