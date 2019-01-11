package com.sygic.ui.view.poidetail

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.sygic.ui.common.extensions.copyToClipboard
import com.sygic.ui.common.extensions.openEmail
import com.sygic.ui.common.extensions.openPhone
import com.sygic.ui.common.extensions.openUrl
import com.sygic.ui.common.sdk.data.PoiData
import com.sygic.ui.view.poidetail.databinding.LayoutPoiDetailInternalBinding
import com.sygic.ui.view.poidetail.viewmodel.PoiDetailInternalViewModel
import androidx.appcompat.app.AppCompatDialogFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.sygic.ui.common.views.BottomSheetDialog
import com.sygic.ui.view.poidetail.manager.PreferencesManager
import com.sygic.ui.view.poidetail.viewmodel.DEFAULT_BEHAVIOR_STATE
import com.sygic.ui.view.poidetail.viewmodel.SHOWCASE_BEHAVIOR_STATE

private const val POI_DATA = "poi_data"

class PoiDetailBottomDialogFragment : AppCompatDialogFragment() {

    interface Listener {
        fun onPoiDetailBottomDialogDismiss()
    }

    private var listener: Listener? = null
    private var viewModel: PoiDetailInternalViewModel? = null

    private lateinit var preferencesManager: PreferencesManager
    private lateinit var binding: LayoutPoiDetailInternalBinding

    companion object {

        const val TAG = "poi_detail_bottom_dialog_fragment"

        @JvmStatic
        fun newInstance(poiData: PoiData): PoiDetailBottomDialogFragment = PoiDetailBottomDialogFragment().apply {
            arguments = Bundle().apply {
                putParcelable(POI_DATA, poiData)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setStyle(STYLE_NORMAL, R.style.PoiDetailBottomDialog)
        preferencesManager = PreferencesManager(requireContext())

        viewModel = ViewModelProviders.of(
            this,
            PoiDetailInternalViewModel.ViewModelFactory(
                arguments?.getParcelable(POI_DATA)!!,
                preferencesManager
            )
        )[PoiDetailInternalViewModel::class.java].apply {
            this.setListener(listener)
            listener = null

            this.expandObservable.observe(this@PoiDetailBottomDialogFragment, Observer<Any> { expandBottomSheet() })
            this.collapseObservable.observe(this@PoiDetailBottomDialogFragment, Observer<Any> { collapseBottomSheet() })
            this.webUrlClickObservable.observe(this@PoiDetailBottomDialogFragment, Observer<String> { context?.openUrl(it) })
            this.emailClickObservable.observe(this@PoiDetailBottomDialogFragment, Observer<String> { context?.openEmail(it) })
            this.phoneNumberClickObservable.observe(this@PoiDetailBottomDialogFragment, Observer<String> { context?.openPhone(it) })
            this.coordinatesClickObservable.observe(this@PoiDetailBottomDialogFragment, Observer<String> { context?.copyToClipboard(it) })
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(
            requireContext(),
            theme,
            resources.getDimensionPixelSize(R.dimen.defaultPeekHeight),
            if (preferencesManager.showcaseAllowed) SHOWCASE_BEHAVIOR_STATE else DEFAULT_BEHAVIOR_STATE
        )
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

    override fun onResume() {
        super.onResume()

        viewModel?.let { (dialog as BottomSheetDialog).behavior?.addStateListener(it) }
    }

    private fun expandBottomSheet() {
        (dialog as BottomSheetDialog).behavior?.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun collapseBottomSheet() {
        (dialog as BottomSheetDialog).behavior?.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    fun setListener(listener: Listener) {
        viewModel?.setListener(listener) ?: run { this.listener = listener }
    }

    override fun onDestroy() {
        super.onDestroy()

        listener = null
    }
}