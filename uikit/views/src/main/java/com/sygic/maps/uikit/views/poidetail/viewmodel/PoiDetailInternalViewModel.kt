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

import android.app.Application
import android.content.Intent
import androidx.lifecycle.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.sygic.maps.uikit.views.common.extensions.*
import com.sygic.maps.uikit.views.common.BottomSheetBehaviorWrapper
import com.sygic.maps.uikit.views.common.livedata.SingleLiveEvent
import com.sygic.maps.uikit.views.poidetail.PoiDetailBottomDialogFragment
import com.sygic.maps.uikit.views.poidetail.component.PoiDetailComponent
import com.sygic.maps.uikit.views.poidetail.manager.PreferencesManager
import kotlinx.coroutines.*
import java.util.concurrent.TimeUnit

private val SHOWCASE_DELAY = TimeUnit.SECONDS.toMillis(1)
const val DEFAULT_BEHAVIOR_STATE = BottomSheetBehavior.STATE_COLLAPSED
const val SHOWCASE_BEHAVIOR_STATE = BottomSheetBehavior.STATE_EXPANDED

internal class PoiDetailInternalViewModel(app: Application, private val preferencesManager: PreferencesManager) :
    AndroidViewModel(app),
    BottomSheetBehaviorWrapper.StateListener {

    val titleText: LiveData<String> = MutableLiveData()
    val subtitleText: LiveData<String> = MutableLiveData()
    val urlText: LiveData<String> = MutableLiveData()
    val emailText: LiveData<String> = MutableLiveData()
    val phoneText: LiveData<String> = MutableLiveData()
    val coordinatesText: LiveData<String> = MutableLiveData()
    val contentViewSwitcherIndex: LiveData<Int> = MutableLiveData()
    val navigationButtonEnabled: LiveData<Boolean> = MutableLiveData(false)

    val dialogStateObservable: LiveData<Int> = SingleLiveEvent()

    var listener: PoiDetailBottomDialogFragment.Listener? = null

    private var showcaseLaunch: Job? = null

    fun onHeaderClick() {
        dialogStateObservable.asSingleEvent().value = BottomSheetBehavior.STATE_EXPANDED
    }

    fun onNavigationButtonClick() {
        listener?.onNavigationButtonClick()
    }

    fun onWebUrlClick() = urlText.value?.let { application.openUrl(it, Intent.FLAG_ACTIVITY_NEW_TASK) }

    fun onEmailClick() = emailText.value?.let { application.openEmail(it, Intent.FLAG_ACTIVITY_NEW_TASK) }

    fun onPhoneNumberClick() = phoneText.value?.let { application.openPhone(it, Intent.FLAG_ACTIVITY_NEW_TASK) }

    fun onCoordinatesClick() = coordinatesText.value?.let { application.copyToClipboard(it) }

    fun onComponentChanged(component: PoiDetailComponent?) {
        component?.let {
            titleText.asMutable().value = it.data.titleString
            subtitleText.asMutable().value = it.data.subtitleString
            urlText.asMutable().value = it.data.urlString
            emailText.asMutable().value = it.data.emailString
            phoneText.asMutable().value = it.data.phoneString
            coordinatesText.asMutable().value = it.data.coordinatesString
            navigationButtonEnabled.asMutable().value = it.navigationButtonEnabled
            contentViewSwitcherIndex.asMutable().value = PoiDetailContentViewSwitcherIndex.CONTENT
        } ?: run {
            contentViewSwitcherIndex.asMutable().value = PoiDetailContentViewSwitcherIndex.PROGRESSBAR
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
        launchShowcaseBlock { dialogStateObservable.asSingleEvent().value = BottomSheetBehavior.STATE_COLLAPSED }
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

    class Factory(
        private val app: Application,
        private val preferencesManager: PreferencesManager
    ) : ViewModelProvider.NewInstanceFactory() {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return PoiDetailInternalViewModel(app, preferencesManager) as T
        }
    }
}