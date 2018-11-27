package com.sygic.ui.view.poidetail.viewmodel

import android.text.TextUtils
import androidx.lifecycle.*
import androidx.lifecycle.Observer
import com.sygic.ui.common.sdk.data.PoiData
import java.util.*

class PoiDetailInternalViewModel : ViewModel() {

    val titleText: MutableLiveData<String> = MutableLiveData()
    val subtitleText: MutableLiveData<String> = MutableLiveData()
    val urlText: MutableLiveData<String> = MutableLiveData()
    val emailText: MutableLiveData<String> = MutableLiveData()
    val phoneText: MutableLiveData<String> = MutableLiveData()

    private var onHeaderContainerClickObserver: Observer<Any>? = null
    private val poiDataObserver = Observer<PoiData> { poiData ->
        titleText.value = getTitleText(poiData)
        subtitleText.value = getSetSubtitleText(poiData.street, poiData.city)
        urlText.value = poiData.url
        emailText.value = poiData.email
        phoneText.value = poiData.phone
    }

    private fun getTitleText(poiData: PoiData): String {
        return poiData.name ?: String.format(
            Locale.US,
            "%.6f, %.6f",
            poiData.coordinates.latitude,
            poiData.coordinates.longitude
        )
    }

    private fun getSetSubtitleText(street: String?, city: String?): String {
        val stringBuilder = StringBuilder()
        street?.let { stringBuilder.append(String.format("%s", it)) }
        city?.let { stringBuilder.append(String.format(if (TextUtils.isEmpty(street)) "%s" else ", %s", it)) }
        return stringBuilder.toString()
    }

    fun setOnHeaderContainerClickObserver(onHeaderContainerClickObserver: Observer<Any>) {
        this.onHeaderContainerClickObserver = onHeaderContainerClickObserver
    }

    fun addDataObservable(poiData: MutableLiveData<PoiData>) {
        poiData.observeForever(poiDataObserver)
    }

    fun removeDataObservable(poiData: MutableLiveData<PoiData>) {
        poiData.removeObserver(poiDataObserver)
    }

    fun onHeaderContainerClick() {
        onHeaderContainerClickObserver?.onChanged(Any())
    }

    override fun onCleared() {
        super.onCleared()

        onHeaderContainerClickObserver = null
    }

    class ViewModelFactory : ViewModelProvider.NewInstanceFactory() {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return PoiDetailInternalViewModel() as T
        }
    }
}