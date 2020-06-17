/*
 * Copyright (c) 2020 Sygic a.s. All rights reserved.
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

package com.sygic.maps.uikit.views.common

import android.content.Context
import android.widget.ArrayAdapter
import androidx.annotation.LayoutRes

class ArrayIndicesAdapter<T>(
    context: Context,
    @LayoutRes resource: Int,
    objects: List<T> = mutableListOf(),
    private val indices: IntArray? = null
) : ArrayAdapter<T>(context, resource, objects) {
    var selected = 0

    fun getIndexForPosition(position: Int) = indices?.get(position) ?: position

    fun findPositionFromIndex(index: Int): Int {
        if (indices == null) {
            return index
        }
        indices.forEachIndexed { i, value ->
            if (value == index) {
                return i
            }
        }
        return -1
    }

    init {
        if (indices != null && objects.size != indices.size) {
            throw IllegalArgumentException("Objects and indices must have same size!")
        }
    }
}