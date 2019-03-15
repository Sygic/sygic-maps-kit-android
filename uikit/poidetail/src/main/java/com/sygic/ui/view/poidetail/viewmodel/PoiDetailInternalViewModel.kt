package com.sygic.ui.view.poidetail.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.sygic.sdk.map.`object`.payload.MarkerData
import com.sygic.ui.common.behaviors.BottomSheetBehaviorWrapper
import com.sygic.ui.common.extensions.asSingleEvent
import com.sygic.ui.common.listeners.DialogFragmentListener
import com.sygic.ui.common.livedata.SingleLiveEvent
import com.sygic.ui.common.sdk.data.BasicMarkerData
import com.sygic.ui.common.sdk.data.PoiMarkerData
import com.sygic.ui.common.sdk.extension.getFormattedLocation
import com.sygic.ui.view.poidetail.manager.PreferencesManager
import kotlinx.coroutines.*
import java.util.concurrent.TimeUnit

private val SHOWCASE_DELAY = TimeUnit.SECONDS.toMillis(1)
const val DEFAULT_BEHAVIOR_STATE = BottomSheetBehavior.STATE_COLLAPSED
const val SHOWCASE_BEHAVIOR_STATE = BottomSheetBehavior.STATE_EXPANDED

internal class PoiDetailInternalViewModel(data: MarkerData,
                                          private val preferencesManager: PreferencesManager) : ViewModel(),
    BottomSheetBehaviorWrapper.StateListener {

    val titleText: String = if (data is BasicMarkerData) data.title else ""
    val subtitleText: String = if (data is BasicMarkerData) data.description else ""
    val coordinatesText: String? = data.position.getFormattedLocation()
    val urlText: String? = if (data is PoiMarkerData) data.url else null
    val emailText: String? = if (data is PoiMarkerData) data.email else null
    val phoneText: String? = if (data is PoiMarkerData) data.phone else null

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

    class ViewModelFactory(private val data: MarkerData,
                           private val preferencesManager: PreferencesManager) : ViewModelProvider.NewInstanceFactory() {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return PoiDetailInternalViewModel(data, preferencesManager) as T
        }
    }
}