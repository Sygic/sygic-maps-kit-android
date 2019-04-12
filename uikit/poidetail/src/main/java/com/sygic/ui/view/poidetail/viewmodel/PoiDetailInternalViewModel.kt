/*
 * Copyright (c) 2019 Sygic a.s. All rights reserved.
 *
 * This project is licensed under the MIT License.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.sygic.ui.view.poidetail.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.sygic.sdk.map.`object`.data.ViewObjectData
import com.sygic.ui.common.behaviors.BottomSheetBehaviorWrapper
import com.sygic.ui.common.extensions.EMPTY_STRING
import com.sygic.ui.common.extensions.asSingleEvent
import com.sygic.ui.common.listeners.DialogFragmentListener
import com.sygic.ui.common.livedata.SingleLiveEvent
import com.sygic.ui.common.sdk.data.BasicData
import com.sygic.ui.common.sdk.data.PoiData
import com.sygic.ui.common.sdk.extension.getFormattedLocation
import com.sygic.ui.view.poidetail.manager.PreferencesManager
import kotlinx.coroutines.*
import java.util.concurrent.TimeUnit

private val SHOWCASE_DELAY = TimeUnit.SECONDS.toMillis(1)
const val DEFAULT_BEHAVIOR_STATE = BottomSheetBehavior.STATE_COLLAPSED
const val SHOWCASE_BEHAVIOR_STATE = BottomSheetBehavior.STATE_EXPANDED

internal class PoiDetailInternalViewModel(
    data: ViewObjectData,
    private val preferencesManager: PreferencesManager
) : ViewModel(), BottomSheetBehaviorWrapper.StateListener {

    private val payload = data.payload
    val titleText: String = if (payload is BasicData) payload.title else data.position.getFormattedLocation()
    val subtitleText: String = if (payload is BasicData) payload.description else EMPTY_STRING
    val urlText: String? = if (payload is PoiData) payload.url else null
    val emailText: String? = if (payload is PoiData) payload.email else null
    val phoneText: String? = if (payload is PoiData) payload.phone else null
    val coordinatesText: String? = data.position.getFormattedLocation()

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

    class ViewModelFactory(
        private val data: ViewObjectData,
        private val preferencesManager: PreferencesManager
    ) : ViewModelProvider.NewInstanceFactory() {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return PoiDetailInternalViewModel(data, preferencesManager) as T
        }
    }
}