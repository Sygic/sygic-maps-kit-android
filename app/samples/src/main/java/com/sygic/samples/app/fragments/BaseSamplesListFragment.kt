package com.sygic.samples.app.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.sygic.samples.databinding.LayoutSamplesListBinding
import com.sygic.samples.app.models.Sample
import com.sygic.samples.app.viewmodels.SamplesListViewModel
import com.sygic.maps.uikit.views.common.extensions.openActivity
import kotlinx.android.synthetic.main.activity_samples.*

abstract class BaseSamplesListFragment : Fragment() {

    @get:StringRes
    protected abstract val title: Int
    protected abstract val items: List<Sample>

    private lateinit var samplesListViewModel: SamplesListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requireActivity().toolbar?.setTitle(title)
        samplesListViewModel =
                ViewModelProviders.of(this, SamplesListViewModel.Factory(items)).get(SamplesListViewModel::class.java).apply {
                    this.startActivityObservable.observe(
                        this@BaseSamplesListFragment,
                        Observer<Class<out AppCompatActivity>> { requireContext().openActivity(it) })
                }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding: LayoutSamplesListBinding = LayoutSamplesListBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.samplesListViewModel = samplesListViewModel
        return binding.root
    }
}