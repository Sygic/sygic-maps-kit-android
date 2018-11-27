package com.sygic.ui.viewmodel.poidetail

import android.annotation.SuppressLint
import androidx.lifecycle.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.sygic.ui.common.sdk.data.PoiData
import com.sygic.ui.common.sdk.model.ExtendedMapDataModel
import com.sygic.ui.view.poidetail.listener.PoiDetailStateListener

class PoiDetailViewModel(private val extendedMapDataModel: ExtendedMapDataModel) : ViewModel(),
    PoiDetailStateListener, DefaultLifecycleObserver {

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

    @SuppressLint("SwitchIntDef")
    override fun onPoiDetailStateChanged(@BottomSheetBehavior.State state: Int) {
        when (state) {
            BottomSheetBehavior.STATE_HIDDEN -> extendedMapDataModel.notifyPoiDataChanged(PoiData())
        }
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