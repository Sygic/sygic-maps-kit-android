package com.sygic.ui.view.poidetail.viewmodel

import android.text.TextUtils
import androidx.lifecycle.*
import com.sygic.ui.common.sdk.data.PoiData
import java.util.*

class PoiDetailInternalViewModel(poiData: PoiData) : ViewModel() {

    val titleText: String? = getTitleText(poiData)
    val subtitleText: String? = getSetSubtitleText(poiData)
    val coordinatesText: String? = getCoordinatesText(poiData)
    val urlText: String? = poiData.url
    val emailText: String? = poiData.email
    val phoneText: String? = poiData.phone

    val webUrlClickObservable: LiveData<String> = MutableLiveData()
    val emailClickObservable: LiveData<String> = MutableLiveData()
    val phoneNumberClickObservable: LiveData<String> = MutableLiveData()
    val coordinatesClickObservable: LiveData<String> = MutableLiveData()

    private fun getTitleText(poiData: PoiData): String {
        return poiData.name ?: getCoordinatesText(poiData)
    }

    private fun getSetSubtitleText(poiData: PoiData): String {
        val stringBuilder = StringBuilder()
        poiData.street?.let { stringBuilder.append(String.format("%s", it)) }
        poiData.city?.let { stringBuilder.append(String.format(if (TextUtils.isEmpty(poiData.street)) "%s" else ", %s", it)) }
        return stringBuilder.toString()
    }

    private fun getCoordinatesText(poiData: PoiData): String {
        return String.format(
            Locale.US,
            "%.6f, %.6f",
            poiData.coordinates.latitude,
            poiData.coordinates.longitude
        )
    }

    fun onWebUrlClick() {
        (webUrlClickObservable as MutableLiveData<String>).value = urlText
    }

    fun onEmailClick() {
        (emailClickObservable as MutableLiveData<String>).value = emailText
    }

    fun onPhoneNumberClick() {
        (phoneNumberClickObservable as MutableLiveData<String>).value = phoneText
    }

    fun onCoordinatesClick() {
        (coordinatesClickObservable as MutableLiveData<String>).value = coordinatesText
    }

    class ViewModelFactory(private val poiData: PoiData) : ViewModelProvider.NewInstanceFactory() {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return PoiDetailInternalViewModel(poiData) as T
        }
    }
}