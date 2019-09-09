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

package com.sygic.maps.uikit.viewmodels.common.datetime

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.SparseArray
import androidx.annotation.RestrictTo
import com.sygic.maps.uikit.viewmodels.common.utils.Time
import com.sygic.maps.uikit.views.common.utils.SingletonHolder
import java.text.DateFormat
import java.util.*
import kotlin.math.abs

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
class DateTimeManagerImpl private constructor(
    app: Application
) : DateTimeManager, BroadcastReceiver() {

    companion object : SingletonHolder<DateTimeManagerImpl>() {
        @JvmStatic
        fun getInstance(app: Application): DateTimeManagerImpl = getInstance { DateTimeManagerImpl(app) }
    }

    private val timeInstances = SparseArray<DateFormat>()
    private val dateInstances = SparseArray<DateFormat>()

    init {
        app.applicationContext.registerReceiver(this, IntentFilter(Intent.ACTION_TIME_CHANGED))
    }

    override fun formatTime(date: Date, style: Int): String {
        timeInstances[style]?.let {
            return it.format(date)
        }

        return DateFormat.getTimeInstance(style).also { timeInstances.put(style, it) }.format(date)
    }

    override fun formatDate(date: Date, style: Int): String {
        dateInstances[style]?.let {
            return it.format(date)
        }

        return DateFormat.getDateInstance(style).also { dateInstances.put(style, it) }.format(date)
    }

    override fun formatDuration(duration: Int): String = Time.getFormattedTime(abs(duration))

    override fun onReceive(context: Context, intent: Intent) {
        timeInstances.clear()
        dateInstances.clear()
    }
}