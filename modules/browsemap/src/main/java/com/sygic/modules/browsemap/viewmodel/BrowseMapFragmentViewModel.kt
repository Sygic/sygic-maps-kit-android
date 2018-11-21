package com.sygic.modules.browsemap.viewmodel

import android.content.Context
import android.content.res.TypedArray
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sygic.modules.browsemap.R
import javax.inject.Inject

class BrowseMapFragmentViewModel /*@Inject constructor */(x: Context, attributesTypedArray: TypedArray?) : ViewModel() {

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
        Log.d("BrowseMapFragmentVM", "$x")
    }

    class Factory @Inject constructor(val x: Context) {
        fun create(attributesTypedArray: TypedArray?) = BrowseMapFragmentViewModel(x, attributesTypedArray)
    }
}