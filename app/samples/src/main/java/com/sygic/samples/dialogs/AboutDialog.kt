package com.sygic.samples.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.lifecycle.ViewModelProviders
import com.sygic.samples.databinding.LayoutAboutDialogBinding
import com.sygic.samples.viewmodels.AboutDialogViewModel

class AboutDialog : AppCompatDialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = LayoutAboutDialogBinding.inflate(inflater, container, false)
        binding.aboutDialogViewModel = ViewModelProviders.of(
            this,
            AboutDialogViewModel.ViewModelFactory(requireActivity().application)
        )[AboutDialogViewModel::class.java]
        binding.lifecycleOwner = this
        return binding.root
    }
}