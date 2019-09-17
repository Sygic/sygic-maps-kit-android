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

package com.sygic.maps.uikit.views.navigation.actionmenu.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sygic.maps.uikit.views.common.extensions.EMPTY_STRING
import com.sygic.maps.uikit.views.common.extensions.asMutable
import com.sygic.maps.uikit.views.common.utils.TextHolder
import com.sygic.maps.uikit.views.navigation.actionmenu.DEFAULT_SPAN_COUNT
import com.sygic.maps.uikit.views.navigation.actionmenu.adapter.ActionMenuAdapter
import com.sygic.maps.uikit.views.navigation.actionmenu.data.ActionMenuData
import com.sygic.maps.uikit.views.navigation.actionmenu.listener.ActionMenuItemClickListener

internal class ActionMenuInternalViewModel : ViewModel() {

    val adapter = ActionMenuAdapter()
    val titleText: LiveData<TextHolder> = MutableLiveData(TextHolder.from(EMPTY_STRING))
    val spanCount: MutableLiveData<Int> = MutableLiveData(DEFAULT_SPAN_COUNT)

    var listener: ActionMenuItemClickListener? = null
        set(value) {
            field = value
            adapter.clickListener = value
        }
    var actionMenuData: ActionMenuData = ActionMenuData.empty
        set(value) {
            field = value
            titleText.asMutable().value = value.menuTitle
            adapter.items = value.menuItems
        }

    override fun onCleared() {
        super.onCleared()

        listener = null
    }
}