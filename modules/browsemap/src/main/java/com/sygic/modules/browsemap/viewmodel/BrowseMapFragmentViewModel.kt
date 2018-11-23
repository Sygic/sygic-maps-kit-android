package com.sygic.modules.browsemap.viewmodel

import android.content.res.TypedArray
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sygic.modules.browsemap.R
import com.sygic.modules.common.di.ViewModelCreatorFactory
import javax.inject.Inject

class BrowseMapFragmentViewModel private constructor(attributesTypedArray: TypedArray?) : ViewModel() {

    val compassEnabled: MutableLiveData<Boolean> = MutableLiveData()
    val compassHideIfNorthUp: MutableLiveData<Boolean> = MutableLiveData()
    val positionLockFabEnabled: MutableLiveData<Boolean> = MutableLiveData()
    val zoomControlsEnabled: MutableLiveData<Boolean> = MutableLiveData()

    init {
        attributesTypedArray?.let {
            compassEnabled.value = it.getBoolean(R.styleable.BrowseMapFragment_sygic_compassEnabled, false)
            compassHideIfNorthUp.value = it.getBoolean(R.styleable.BrowseMapFragment_sygic_compassHideIfNorthUp, false)
            positionLockFabEnabled.value =
                    it.getBoolean(R.styleable.BrowseMapFragment_sygic_positionLockFabEnabled, false)
            zoomControlsEnabled.value = it.getBoolean(R.styleable.BrowseMapFragment_sygic_zoomControlsEnabled, false)
            it.recycle()
        }
    }

    //todo: generate this
    class Factory @Inject constructor() : ViewModelCreatorFactory {
        override fun create(vararg assistedValues: Any?) = BrowseMapFragmentViewModel(
            if (assistedValues.isNotEmpty()) assistedValues[0] as TypedArray? else null
        )
    }
}