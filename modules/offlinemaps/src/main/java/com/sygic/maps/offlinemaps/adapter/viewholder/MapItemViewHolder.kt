package com.sygic.maps.offlinemaps.adapter.viewholder

import android.view.View
import android.widget.ImageButton
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.ProgressIndicator
import com.google.android.material.textview.MaterialTextView
import com.sygic.maps.offlinemaps.R
import com.sygic.maps.module.common.maploader.MapItem
import com.sygic.sdk.map.MapLoader
import kotlinx.android.synthetic.main.map_item_layout.view.*

class MapItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val nameText: MaterialTextView = itemView.mapName
    private val detailsText: MaterialTextView = itemView.subtitleText
    private val primaryActionButton: ImageButton = itemView.primaryActionButton
    private val loadButton: MaterialButton = itemView.loadButton
    private val progressIndicator: ProgressIndicator = itemView.progressIndicator
    private val statusText: MaterialTextView = itemView.statusText
    private val updateActionButton: ImageButton = itemView.updateActionButton

    fun bind(item: MapItem) {
        nameText.text = item.name
        detailsText.text = item.details
        updateStatus(item.data.status, item.data.progress)
    }

    fun updateStatus(status: MapLoader.MapStatus, progress: Int) {
        statusText.text = status.toString()
        when (status) {
            MapLoader.MapStatus.Installing, MapLoader.MapStatus.Updating -> {
                progressIndicator.setProgressCompat(progress, false)
                progressIndicator.visibility = View.GONE
                progressIndicator.isIndeterminate = progress == 0
                progressIndicator.visibility = View.VISIBLE
                primaryActionButton.setImageResource(R.drawable.ic_cancel)
                primaryActionButton.visibility = View.VISIBLE
                loadButton.visibility = View.GONE
            }
            MapLoader.MapStatus.Uninstalling -> {
                progressIndicator.visibility = View.GONE
                progressIndicator.isIndeterminate = true
                progressIndicator.visibility = View.VISIBLE
                primaryActionButton.visibility = View.VISIBLE
                primaryActionButton.setImageResource(R.drawable.ic_cancel)
                loadButton.visibility = View.GONE
            }
            MapLoader.MapStatus.Installed, MapLoader.MapStatus.Loaded -> {
                primaryActionButton.visibility = View.VISIBLE
                primaryActionButton.setImageResource(R.drawable.ic_delete)
                progressIndicator.visibility = View.GONE
                loadButton.visibility = View.VISIBLE
                loadButton.text = if (status == MapLoader.MapStatus.Loaded) {
                    itemView.context.getString(R.string.unload_map)
                } else {
                    itemView.context.getString(R.string.load_map)
                }
            }
            MapLoader.MapStatus.PartiallyInstalled -> {
                primaryActionButton.visibility = View.GONE
                progressIndicator.visibility = View.GONE
                loadButton.visibility = View.GONE
            }
            MapLoader.MapStatus.NotInstalled -> {
                primaryActionButton.setImageResource(R.drawable.ic_download)
                primaryActionButton.visibility = View.VISIBLE
                progressIndicator.visibility = View.GONE
                loadButton.visibility = View.GONE
            }
            else -> {
                primaryActionButton.setImageResource(R.drawable.ic_error)
                primaryActionButton.visibility = View.VISIBLE
                progressIndicator.visibility = View.GONE
                loadButton.visibility = View.GONE
            }
        }
    }

    fun updateProgress(progress: Int) {
        progressIndicator.setProgressCompat(progress, true)
    }

    fun setPrimaryActionClickListener(listener: (View) -> Unit) = primaryActionButton.setOnClickListener(listener)
    fun setLoadButtonClickListener(listener: (View) -> Unit) = loadButton.setOnClickListener(listener)
    fun setUpdateButtonClickListener(listener: (View) -> Unit) = updateActionButton.setOnClickListener(listener)
}
