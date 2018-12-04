package com.sygic.ui.view.poidetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.sygic.ui.common.extensions.copyToClipboard
import com.sygic.ui.common.extensions.openEmail
import com.sygic.ui.common.extensions.openPhone
import com.sygic.ui.common.extensions.openUrl
import com.sygic.ui.common.sdk.data.PoiData
import com.sygic.ui.view.poidetail.databinding.LayoutPoiDetailInternalBinding
import com.sygic.ui.view.poidetail.viewmodel.PoiDetailInternalViewModel

class PoiDetailBottomDialogFragment : BottomSheetDialogFragment() {

    interface Listener {
        fun onPoiDetailBottomDialogDismiss()
    }

    private lateinit var binding: LayoutPoiDetailInternalBinding

    private var viewModel: PoiDetailInternalViewModel? = null
    private var listener: Listener? = null

    companion object {

        const val TAG = "poi_detail_bottom_dialog_fragment"
        private const val POI_DATA = "poi_data"

        @JvmStatic
        fun newInstance(poiData: PoiData): PoiDetailBottomDialogFragment = PoiDetailBottomDialogFragment().apply {
            arguments = Bundle().apply {
                putParcelable(POI_DATA, poiData)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProviders.of(
            this, PoiDetailInternalViewModel.ViewModelFactory(arguments?.getParcelable(POI_DATA)!!)
        )[PoiDetailInternalViewModel::class.java].apply {
            this.setListener(listener)
            listener = null

            this.webUrlClickObservable.observe(this@PoiDetailBottomDialogFragment, Observer<String> { context?.openUrl(it) })
            this.emailClickObservable.observe(this@PoiDetailBottomDialogFragment, Observer<String> { context?.openEmail(it) })
            this.phoneNumberClickObservable.observe(this@PoiDetailBottomDialogFragment, Observer<String> { context?.openPhone(it) })
            this.coordinatesClickObservable.observe(this@PoiDetailBottomDialogFragment, Observer<String> { context?.copyToClipboard(it) })
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = LayoutPoiDetailInternalBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.poiDetailInternalViewModel = viewModel
        binding.setLifecycleOwner(this)
    }

    fun seListener(listener: Listener) {
        viewModel?.setListener(listener) ?: run { this.listener = listener }
    }

    override fun onDestroy() {
        super.onDestroy()

        listener = null
    }
}