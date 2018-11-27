package com.sygic.ui.view.poidetail.viewmodel

import android.text.TextUtils
import androidx.lifecycle.*
import androidx.lifecycle.Observer
import com.sygic.ui.common.sdk.data.PoiData
import java.util.*

class PoiDetailInternalViewModel : ViewModel() {

    val titleText: MutableLiveData<String> = MutableLiveData()
    val subtitleText: MutableLiveData<String> = MutableLiveData()

    private val poiDataObserver = Observer<PoiData> { poiData ->
        setTitle(poiData)
        setSubtitle(poiData.street, poiData.city)
    }

    private fun setTitle(poiData: PoiData) {
        titleText.value = poiData.name ?:
                String.format(Locale.US, "%.6f, %.6f", poiData.coordinates.latitude, poiData.coordinates.longitude)
    }

    private fun setSubtitle(street: String?, city: String?) {
        val stringBuilder = StringBuilder()
        street?.let { stringBuilder.append(String.format("%s", it)) }
        city?.let { stringBuilder.append(String.format(if (TextUtils.isEmpty(street)) "%s" else ", %s", it)) }
        subtitleText.value = stringBuilder.toString()
    }

    fun addObservable(poiData: MutableLiveData<PoiData>) {
        poiData.observeForever(poiDataObserver)
    }

    fun removeObservable(poiData: MutableLiveData<PoiData>) {
        poiData.removeObserver(poiDataObserver)
    }

    class ViewModelFactory : ViewModelProvider.NewInstanceFactory() {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return PoiDetailInternalViewModel() as T
        }
    }
}