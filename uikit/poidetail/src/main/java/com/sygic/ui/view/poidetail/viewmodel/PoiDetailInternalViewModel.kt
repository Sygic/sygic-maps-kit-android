package com.sygic.ui.view.poidetail.viewmodel

import androidx.lifecycle.*
import com.sygic.ui.common.livedata.SingleLiveEvent
import com.sygic.ui.common.sdk.data.PoiData
import com.sygic.ui.common.sdk.utlils.getAddressTitle
import com.sygic.ui.common.sdk.utlils.getAddressSubtitle
import com.sygic.ui.common.sdk.utlils.getFormattedLocation
import com.sygic.ui.view.poidetail.PoiDetailBottomDialogFragment

class PoiDetailInternalViewModel(poiData: PoiData) : ViewModel() {

    val titleText: String? = getAddressTitle(poiData)
    val subtitleText: String? = getAddressSubtitle(poiData)
    val coordinatesText: String? = getFormattedLocation(poiData.coordinates)
    val urlText: String? = poiData.url
    val emailText: String? = poiData.email
    val phoneText: String? = poiData.phone

    val webUrlClickObservable: LiveData<String> = SingleLiveEvent()
    val emailClickObservable: LiveData<String> = SingleLiveEvent()
    val phoneNumberClickObservable: LiveData<String> = SingleLiveEvent()
    val coordinatesClickObservable: LiveData<String> = SingleLiveEvent()

    private var listener: PoiDetailBottomDialogFragment.Listener? = null

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