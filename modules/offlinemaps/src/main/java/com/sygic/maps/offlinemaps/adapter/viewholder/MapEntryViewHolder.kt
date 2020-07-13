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

package com.sygic.maps.offlinemaps.adapter.viewholder

import android.view.View
import android.widget.ImageButton
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.ProgressIndicator
import com.google.android.material.textview.MaterialTextView
import com.sygic.maps.offlinemaps.R
import com.sygic.sdk.map.MapLoader
import kotlinx.android.synthetic.main.map_item_layout.view.*

abstract class MapEntryViewHolder<T : Any>(itemView: View, private val loadButtonEnabled: Boolean) : RecyclerView.ViewHolder(itemView) {
    protected val mapName: MaterialTextView = itemView.mapName
    protected val subtitleText: MaterialTextView = itemView.subtitleText
    protected val primaryActionButton: ImageButton = itemView.primaryActionButton
    protected val loadButton: MaterialButton = itemView.loadButton
    protected val progressIndicator: ProgressIndicator = itemView.progressIndicator
    protected val statusText: MaterialTextView = itemView.statusText
    protected val updateActionButton: ImageButton = itemView.updateActionButton

    abstract fun bind(mapHolder: T)

    fun setPrimaryActionClickListener(callback: (View) -> Unit) = primaryActionButton.setOnClickListener(callback)

    fun setLoadButtonClickListener(callback: (View) -> Unit) = loadButton.setOnClickListener(callback)

    fun setUpdateButtonClickListener(callback: (View) -> Unit) = updateActionButton.setOnClickListener(callback)

    fun updateUpdateable(updateAvailable: Boolean, status: MapLoader.MapStatus) {
        if (updateAvailable && (status == MapLoader.MapStatus.Installed || status == MapLoader.MapStatus.Loaded)) {
            updateActionButton.visibility = View.VISIBLE
        } else {
            updateActionButton.visibility = View.GONE
        }
    }

    fun updateProgress(progress: Int) {
        if (progressIndicator.visibility != View.VISIBLE) {
            loadButton.visibility = View.GONE
            primaryActionButton.visibility = View.GONE
            progressIndicator.visibility = View.VISIBLE
        }

        if (progress == 0 && !progressIndicator.isIndeterminate) {
            progressIndicator.visibility = View.GONE
            progressIndicator.isIndeterminate = true
            progressIndicator.setProgressCompat(progress, false)
            progressIndicator.visibility = View.VISIBLE
        } else if (progress > 0 && progressIndicator.isIndeterminate) {
            progressIndicator.visibility = View.GONE
            progressIndicator.isIndeterminate = false
            progressIndicator.visibility = View.VISIBLE
            progressIndicator.setProgressCompat(progress, true)
        }

        progressIndicator.setProgressCompat(progress, true)
    }

    fun updateFromStatus(status: MapLoader.MapStatus, progress: Int) {
        statusText.text = status.toString()
        when (status) {
            MapLoader.MapStatus.Loaded -> {
                if (loadButtonEnabled) {
                    loadButton.visibility = View.VISIBLE
                    loadButton.text = itemView.context.getString(R.string.unload_map)
                }
                primaryActionButton.visibility = View.VISIBLE
                primaryActionButton.setImageResource(R.drawable.ic_delete)
                progressIndicator.visibility = View.GONE
                progressIndicator.setProgressCompat(progress, false)
            }
            MapLoader.MapStatus.Installed -> {
                if (loadButtonEnabled) {
                    loadButton.visibility = View.VISIBLE
                    loadButton.text = itemView.context.getString(R.string.load_map)
                }
                primaryActionButton.visibility = View.VISIBLE
                primaryActionButton.setImageResource(R.drawable.ic_delete)
                progressIndicator.visibility = View.GONE
                progressIndicator.setProgressCompat(progress, false)
            }
            MapLoader.MapStatus.PartiallyInstalled -> {
                loadButton.visibility = View.GONE
                primaryActionButton.visibility = View.GONE
                progressIndicator.visibility = View.GONE
                progressIndicator.setProgressCompat(progress, false)
            }
            MapLoader.MapStatus.Installing, MapLoader.MapStatus.Updating -> {
                loadButton.visibility = View.GONE
                primaryActionButton.setImageResource(R.drawable.ic_cancel)
                primaryActionButton.visibility = View.VISIBLE
                updateActionButton.visibility = View.GONE
                progressIndicator.visibility = View.GONE
                progressIndicator.setProgressCompat(progress, false)
                progressIndicator.isIndeterminate = progress == 0
                progressIndicator.visibility = View.VISIBLE
            }
            MapLoader.MapStatus.Uninstalling -> {
                loadButton.visibility = View.GONE
                primaryActionButton.visibility = View.GONE
                updateActionButton.visibility = View.GONE
                progressIndicator.visibility = View.GONE
                progressIndicator.isIndeterminate = true
                progressIndicator.visibility = View.VISIBLE
            }
            MapLoader.MapStatus.NotInstalled -> {
                loadButton.visibility = View.GONE
                primaryActionButton.visibility = View.VISIBLE
                primaryActionButton.setImageResource(R.drawable.ic_download)
                updateActionButton.visibility = View.GONE
                progressIndicator.visibility = View.GONE
                progressIndicator.setProgressCompat(progress, false)
            }
            MapLoader.MapStatus.Corrupted, MapLoader.MapStatus.Unknown -> {
                loadButton.visibility = View.GONE
                updateActionButton.visibility = View.GONE
                primaryActionButton.visibility = View.VISIBLE
                primaryActionButton.setImageResource(R.drawable.ic_error)
                progressIndicator.visibility = View.GONE
                progressIndicator.setProgressCompat(progress, false)
            }
        }
    }
}
