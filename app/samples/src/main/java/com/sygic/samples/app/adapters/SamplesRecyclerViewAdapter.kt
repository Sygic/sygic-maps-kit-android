package com.sygic.samples.app.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sygic.samples.databinding.LayoutCardItemBinding
import com.sygic.samples.app.models.Sample

class SamplesRecyclerViewAdapter : RecyclerView.Adapter<SamplesRecyclerViewAdapter.SampleViewHolder>() {

    var clickListener: ClickListener? = null
    var items: List<Sample> = mutableListOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    interface ClickListener {
        fun onSampleItemClick(sample: Sample)
    }

    override fun getItemCount(): Int = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SampleViewHolder {
        return SampleViewHolder(LayoutCardItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: SampleViewHolder, position: Int) {
        holder.update(items[position])
    }

    inner class SampleViewHolder(private val binding: LayoutCardItemBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener { clickListener?.onSampleItemClick(items[adapterPosition]) }
        }

        fun update(sample: Sample) {
            binding.previewImage.setImageResource(sample.previewImage)
            binding.title.setText(sample.title)
            binding.subtitle.setText(sample.subtitle)
        }
    }
}