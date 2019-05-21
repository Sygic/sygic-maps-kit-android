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

internal class PoiDetailInternalViewModel(private val preferencesManager: PreferencesManager) : ViewModel(),
    BottomSheetBehaviorWrapper.StateListener {

    val titleText: MutableLiveData<String> = MutableLiveData()
    val subtitleText: MutableLiveData<String> = MutableLiveData()
    val urlText: MutableLiveData<String> = MutableLiveData()
    val emailText: MutableLiveData<String> = MutableLiveData()
    val phoneText: MutableLiveData<String> = MutableLiveData()
    val coordinatesText: MutableLiveData<String> = MutableLiveData()

    val expandObservable: LiveData<Any> = SingleLiveEvent()
    val collapseObservable: LiveData<Any> = SingleLiveEvent()
    val webUrlClickObservable: LiveData<String> = SingleLiveEvent()
    val emailClickObservable: LiveData<String> = SingleLiveEvent()
    val phoneNumberClickObservable: LiveData<String> = SingleLiveEvent()
    val coordinatesClickObservable: LiveData<String> = SingleLiveEvent()

    val contentViewSwitcherIndexObservable: LiveData<Int> = SingleLiveEvent()

    private var listener: DialogFragmentListener? = null
    private var showcaseLaunch: Job? = null

    fun onHeaderClick() {
        expandObservable.asSingleEvent().call()
    }

    fun onWebUrlClick() {
        webUrlClickObservable.asSingleEvent().value = urlText.value
    }

    fun onEmailClick() {
        emailClickObservable.asSingleEvent().value = emailText.value
    }

    fun onPhoneNumberClick() {
        phoneNumberClickObservable.asSingleEvent().value = phoneText.value
    }

    fun onCoordinatesClick() {
        coordinatesClickObservable.asSingleEvent().value = coordinatesText.value
    }

    fun setListener(listener: DialogFragmentListener?) {
        this.listener = listener
    }

    fun setData(data: PoiDetailData?) {
        data?.let {
            titleText.value = it.titleString
            subtitleText.value = it.subtitleString
            urlText.value = it.urlString
            emailText.value = it.emailString
            phoneText.value = it.phoneString
            coordinatesText.value = it.coordinatesString
            contentViewSwitcherIndexObservable.asSingleEvent().value = PoiDetailContentViewSwitcherIndex.CONTENT
        } ?: run {
            contentViewSwitcherIndexObservable.asSingleEvent().value = PoiDetailContentViewSwitcherIndex.PROGRESSBAR
        }
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

    class ViewModelFactory(private val preferencesManager: PreferencesManager) :
        ViewModelProvider.NewInstanceFactory() {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return PoiDetailInternalViewModel(preferencesManager) as T
        }
    }
}