package com.sygic.modules.browsemap.viewmodel

import android.content.res.TypedArray
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sygic.modules.browsemap.R

class BrowseMapFragmentViewModel(attributesTypedArray: TypedArray?) : ViewModel() {

    val compassEnabled: MutableLiveData<Boolean> = MutableLiveData()
    val compassHideIfNorthUp: MutableLiveData<Boolean> = MutableLiveData()
    val positionLockFabEnabled: MutableLiveData<Boolean> = MutableLiveData()
    val zoomControlsEnabled: MutableLiveData<Boolean> = MutableLiveData()

    init {
        attributesTypedArray?.let {
            compassEnabled.value = it.getBoolean(R.styleable.BrowseMapFragment_sygic_compassEnabled, false)
            compassHideIfNorthUp.value = it.getBoolean(R.styleable.BrowseMapFragment_sygic_compassHideIfNorthUp, false)
            positionLockFabEnabled.value = it.getBoolean(R.styleable.BrowseMapFragment_sygic_positionLockFabEnabled, false)
            zoomControlsEnabled.value = it.getBoolean(R.styleable.BrowseMapFragment_sygic_zoomControlsEnabled, false)
            it.recycle()
        }
    }

    internal class ViewModelFactory(private val attributesTypedArray: TypedArray?) :
        ViewModelProvider.NewInstanceFactory() {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return BrowseMapFragmentViewModel(attributesTypedArray) as T
        }
    }
}