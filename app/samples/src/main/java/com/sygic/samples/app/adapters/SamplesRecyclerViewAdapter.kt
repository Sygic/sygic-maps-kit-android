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