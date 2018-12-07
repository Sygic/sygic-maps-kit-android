package com.sygic.ui.view.poidetail.viewmodel

import androidx.lifecycle.*
import com.sygic.ui.common.livedata.SingleLiveEvent
import com.sygic.ui.common.sdk.data.PoiData
import com.sygic.ui.common.sdk.extension.getFormattedLocation
import com.sygic.ui.view.poidetail.PoiDetailBottomDialogFragment

class PoiDetailInternalViewModel(poiData: PoiData) : ViewModel() {

    val titleText: String
    val subtitleText: String
    val coordinatesText: String?
    val urlText: String? = poiData.url
    val emailText: String? = poiData.email
    val phoneText: String? = poiData.phone

    val expandObservable: LiveData<Any> = SingleLiveEvent()
    val webUrlClickObservable: LiveData<String> = SingleLiveEvent()
    val emailClickObservable: LiveData<String> = SingleLiveEvent()
    val phoneNumberClickObservable: LiveData<String> = SingleLiveEvent()
    val coordinatesClickObservable: LiveData<String> = SingleLiveEvent()

    private var listener: PoiDetailBottomDialogFragment.Listener? = null

    init {
        val addressComponent = poiData.getAddressComponent()
        titleText = addressComponent.formattedTitle
        subtitleText = addressComponent.formattedSubtitle
        coordinatesText = poiData.coordinates.getFormattedLocation()
    }

    fun onHeaderClick() {
        (expandObservable as SingleLiveEvent<Any>).call()
    }

    fun onWebUrlClick() {
        (webUrlClickObservable as SingleLiveEvent<String>).value = urlText
    }

    fun onEmailClick() {
        (emailClickObservable as SingleLiveEvent<String>).value = emailText
    }

    fun onPhoneNumberClick() {
        (phoneNumberClickObservable as SingleLiveEvent<String>).value = phoneText
    }

    fun onCoordinatesClick() {
        (coordinatesClickObservable as SingleLiveEvent<String>).value = coordinatesText
    }

    fun setListener(listener: PoiDetailBottomDialogFragment.Listener?) {
        this.listener = listener
    }

    override fun onCleared() {
        super.onCleared()

        listener?.onPoiDetailBottomDialogDismiss()
        listener = null
    }

    class ViewModelFactory(private val poiData: PoiData) : ViewModelProvider.NewInstanceFactory() {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return PoiDetailInternalViewModel(poiData) as T
        }
    }
}