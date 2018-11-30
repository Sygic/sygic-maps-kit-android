package com.sygic.ui.view.poidetail

import android.content.DialogInterface
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.sygic.ui.common.*
import com.sygic.ui.common.sdk.data.PoiData
import com.sygic.ui.view.poidetail.databinding.LayoutPoiDetailInternalBinding
import com.sygic.ui.view.poidetail.viewmodel.PoiDetailInternalViewModel

class PoiDetailBottomDialogFragment : BottomSheetDialogFragment() {

    private lateinit var binding: LayoutPoiDetailInternalBinding
    private lateinit var viewModel: PoiDetailInternalViewModel

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
        )[PoiDetailInternalViewModel::class.java]
        viewModel.webUrlClickObservable.observe(this, Observer<String> { context?.openUrl(it) })
        viewModel.emailClickObservable.observe(this, Observer<String> { context?.openEmail(it) })
        viewModel.phoneNumberClickObservable.observe(this, Observer<String> { context?.openPhone(it) })
        viewModel.coordinatesClickObservable.observe(this, Observer<String> { context?.copyToClipboard(it) })
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

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)

        //todo: inject ExtendedMapDataModel and hide pin when dismiss?
        Log.d("Tomas", "onDismiss() called with: dialog = [$dialog]")
    }
}