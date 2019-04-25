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

package com.sygic.maps.uikit.viewmodels.searchtoolbar

import android.util.Log
import android.view.inputmethod.EditorInfo
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sygic.maps.tools.annotations.AutoFactory
import com.sygic.maps.uikit.viewmodels.common.location.LocationManager
import com.sygic.maps.uikit.viewmodels.common.permission.PermissionsManager
import com.sygic.maps.uikit.viewmodels.common.sdk.model.ExtendedCameraModel
import com.sygic.maps.uikit.viewmodels.common.utils.TextWatcherAdapter
import com.sygic.maps.uikit.views.positionlockfab.PositionLockFab
import com.sygic.maps.uikit.views.searchtoolbar.SearchToolbar
import com.sygic.maps.uikit.views.searchtoolbar.SearchToolbarIconStateSwitcherIndex
import com.sygic.sdk.map.Camera

/**
 * A [SearchToolbarViewModel] is a basic ViewModel implementation for the [SearchToolbar] class. TODO It listens to the Sygic SDK
 * [Camera.ModeChangedListener] and set appropriate state to the [PositionLockFab] view. It also sets the [LockState.UNLOCKED]
 * as default.
 */
@AutoFactory
@Suppress("unused", "MemberVisibilityCanBePrivate")
open class SearchToolbarViewModel internal constructor(
    private val cameraModel: ExtendedCameraModel, //todo
    private val locationManager: LocationManager, //todo
    private val permissionsManager: PermissionsManager //todo
) : ViewModel(), DefaultLifecycleObserver {

    @SearchToolbarIconStateSwitcherIndex
    val iconStateSwitcherIndex: MutableLiveData<Int> = MutableLiveData()
    val text: MutableLiveData<String> = MutableLiveData() //todo

    val onTextChangedListener = object : TextWatcherAdapter() {
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            val searched = s.toString()

            //TODO
            /*if (searched != text) {
                searchDisposable?.dispose()
                textChangedSubject.onNext(searched)
            }*/
        }
    }

    init {
        iconStateSwitcherIndex.value = SearchToolbarIconStateSwitcherIndex.MAGNIFIER
        text.value = "Test" //todo
    }

    override fun onStart(owner: LifecycleOwner) { //todo
        /*cameraModel.addModeChangedListener(this)
        if (currentState.value == LockState.LOCKED) {
            locationManager.setSdkPositionUpdatingEnabled(true)
        }*/
    }

    fun onClearButtonClick() {
        Log.d("Tomas", "onClearButtonClick() called") //todo
    }

    fun onEditorActionEvent(actionId: Int): Boolean {
        return if (actionId == EditorInfo.IME_ACTION_SEARCH) { //todo
            Log.d("Tomas", "onEditorActionEvent() called with: actionId = [$actionId]")
            true
        } else false
    }

    override fun onStop(owner: LifecycleOwner) { //todo
        /*cameraModel.removeModeChangedListener(this)
        locationManager.setSdkPositionUpdatingEnabled(false)*/
    }

    override fun onCleared() { //todo
        super.onCleared()
    }
}