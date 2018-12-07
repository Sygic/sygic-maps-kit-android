package com.sygic.ui.view.poidetail.viewmodel

import androidx.lifecycle.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.sygic.ui.common.behaviors.BottomSheetBehaviorWrapper
import com.sygic.ui.common.livedata.SingleLiveEvent
import com.sygic.ui.common.sdk.data.PoiData
import com.sygic.ui.common.sdk.extension.getFormattedLocation
import com.sygic.ui.view.poidetail.PoiDetailBottomDialogFragment
import com.sygic.ui.view.poidetail.manager.PreferencesManager
import kotlinx.coroutines.*
import java.util.concurrent.TimeUnit

private val SHOWCASE_DELAY = TimeUnit.SECONDS.toMillis(1)
const val DEFAULT_BEHAVIOR_STATE = BottomSheetBehavior.STATE_COLLAPSED

internal class PoiDetailInternalViewModel(poiData: PoiData,
                                 private val preferencesManager: PreferencesManager) : ViewModel(),
    BottomSheetBehaviorWrapper.StateListener {

    val titleText: String
    val subtitleText: String
    val coordinatesText: String?
    val urlText: String? = poiData.url
    val emailText: String? = poiData.email
    val phoneText: String? = poiData.phone

    val expandObservable: LiveData<Any> = SingleLiveEvent()
    val collapseObservable: LiveData<Any> = SingleLiveEvent()
    val webUrlClickObservable: LiveData<String> = SingleLiveEvent()
    val emailClickObservable: LiveData<String> = SingleLiveEvent()
    val phoneNumberClickObservable: LiveData<String> = SingleLiveEvent()
    val coordinatesClickObservable: LiveData<String> = SingleLiveEvent()

    private var listener: PoiDetailBottomDialogFragment.Listener? = null
    private var showcaseLaunch: Job? = null

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

    override fun onStateChanged(@BottomSheetBehavior.State newState: Int) {
        startShowcase(newState)
    }

    private fun startShowcase(newState: Int) {
        if (newState != DEFAULT_BEHAVIOR_STATE || !preferencesManager.showcaseAllowed) {
            return
        }

        preferencesManager.showcaseAllowed = false
        launchShowcaseBlock {
            (expandObservable as SingleLiveEvent<Any>).call()
            launchShowcaseBlock {
                (collapseObservable as SingleLiveEvent<Any>).call()
            }
        }
    }

    private fun launchShowcaseBlock(showcaseBlock: () -> Unit) {
        showcaseLaunch = GlobalScope.launch(Dispatchers.Main) {
            delay(SHOWCASE_DELAY)
            showcaseBlock()
        }
    }

    override fun onCleared() {
        super.onCleared()

        listener?.onPoiDetailBottomDialogDismiss()
        showcaseLaunch?.cancel()
        listener = null
    }

    class ViewModelFactory(private val poiData: PoiData,
                           private val preferencesManager: PreferencesManager) : ViewModelProvider.NewInstanceFactory() {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return PoiDetailInternalViewModel(poiData, preferencesManager) as T
        }
    }
}