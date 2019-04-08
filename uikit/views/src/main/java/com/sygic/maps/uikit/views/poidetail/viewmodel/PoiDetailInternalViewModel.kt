package com.sygic.maps.uikit.views.poidetail.viewmodel

import androidx.lifecycle.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.sygic.maps.uikit.views.poidetail.behavior.BottomSheetBehaviorWrapper
import com.sygic.maps.uikit.views.common.extensions.asSingleEvent
import com.sygic.maps.uikit.views.poidetail.listener.DialogFragmentListener
import com.sygic.maps.uikit.views.common.livedata.SingleLiveEvent
import com.sygic.maps.uikit.views.poidetail.data.PoiDetailData
import com.sygic.maps.uikit.views.poidetail.manager.PreferencesManager
import kotlinx.coroutines.*
import java.util.concurrent.TimeUnit

private val SHOWCASE_DELAY = TimeUnit.SECONDS.toMillis(1)
const val DEFAULT_BEHAVIOR_STATE = BottomSheetBehavior.STATE_COLLAPSED
const val SHOWCASE_BEHAVIOR_STATE = BottomSheetBehavior.STATE_EXPANDED

internal class PoiDetailInternalViewModel(poiDetailData: PoiDetailData,
                                          private val preferencesManager: PreferencesManager
) : ViewModel(), BottomSheetBehaviorWrapper.StateListener {

    val titleText: String = poiDetailData.titleString
    val subtitleText: String = poiDetailData.subtitleString
    val urlText: String? = poiDetailData.urlString
    val emailText: String? = poiDetailData.emailString
    val phoneText: String? = poiDetailData.phoneString
    val coordinatesText: String? = poiDetailData.coordinatesString

    val expandObservable: LiveData<Any> = SingleLiveEvent()
    val collapseObservable: LiveData<Any> = SingleLiveEvent()
    val webUrlClickObservable: LiveData<String> = SingleLiveEvent()
    val emailClickObservable: LiveData<String> = SingleLiveEvent()
    val phoneNumberClickObservable: LiveData<String> = SingleLiveEvent()
    val coordinatesClickObservable: LiveData<String> = SingleLiveEvent()

    private var listener: DialogFragmentListener? = null
    private var showcaseLaunch: Job? = null

    fun onHeaderClick() {
        expandObservable.asSingleEvent().call()
    }

    fun onWebUrlClick() {
        webUrlClickObservable.asSingleEvent().value = urlText
    }

    fun onEmailClick() {
        emailClickObservable.asSingleEvent().value = emailText
    }

    fun onPhoneNumberClick() {
        phoneNumberClickObservable.asSingleEvent().value = phoneText
    }

    fun onCoordinatesClick() {
        coordinatesClickObservable.asSingleEvent().value = coordinatesText
    }

    fun setListener(listener: DialogFragmentListener?) {
        this.listener = listener
    }

    override fun onStateChanged(@BottomSheetBehavior.State newState: Int) {
        startShowcase(newState)
    }

    private fun startShowcase(newState: Int) {
        if (newState != SHOWCASE_BEHAVIOR_STATE || !preferencesManager.showcaseAllowed) {
            return
        }

        preferencesManager.showcaseAllowed = false
        launchShowcaseBlock { collapseObservable.asSingleEvent().call() }
    }

    private fun launchShowcaseBlock(showcaseBlock: () -> Unit) {
        showcaseLaunch = GlobalScope.launch(Dispatchers.Main) {
            delay(SHOWCASE_DELAY)
            showcaseBlock()
        }
    }

    override fun onCleared() {
        super.onCleared()

        listener?.onDismiss()
        showcaseLaunch?.cancel()
        listener = null
    }

    class ViewModelFactory(private val poiDetailData: PoiDetailData,
                           private val preferencesManager: PreferencesManager
    ) : ViewModelProvider.NewInstanceFactory() {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return PoiDetailInternalViewModel(poiDetailData, preferencesManager) as T
        }
    }
}