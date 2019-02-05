package com.sygic.ui.view.poidetail

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.sygic.ui.common.extensions.copyToClipboard
import com.sygic.ui.common.extensions.openEmail
import com.sygic.ui.common.extensions.openPhone
import com.sygic.ui.common.extensions.openUrl
import com.sygic.ui.common.listeners.DialogFragmentListener
import com.sygic.ui.common.sdk.data.PoiData
import com.sygic.ui.common.views.BottomSheetDialog
import com.sygic.ui.view.poidetail.databinding.LayoutPoiDetailInternalBinding
import com.sygic.ui.view.poidetail.manager.PreferencesManager
import com.sygic.ui.view.poidetail.viewmodel.DEFAULT_BEHAVIOR_STATE
import com.sygic.ui.view.poidetail.viewmodel.PoiDetailInternalViewModel
import com.sygic.ui.view.poidetail.viewmodel.SHOWCASE_BEHAVIOR_STATE

private const val POI_DATA = "poi_data"

/**
 * A [PoiDetailBottomDialogFragment] is a custom version of the [DialogFragment] that shows a bottom sheet using custom
 * [BottomSheetDialog] instead of a floating dialog. It can be used for a visual representation of the [PoiData] object.
 *
 * You can register an [DialogFragmentListener] using [setListener] method. Then you will be notified when dialog is dismissed.
 *
 * Content colors can be changed with the standard _colorBackground_, _textColorPrimary_, _textColorSecondary_ or
 * _colorAccent_ attribute.
*/
open class PoiDetailBottomDialogFragment : AppCompatDialogFragment() {

    private var listener: DialogFragmentListener? = null
    private var viewModel: PoiDetailInternalViewModel? = null

    private lateinit var preferencesManager: PreferencesManager
    private lateinit var binding: LayoutPoiDetailInternalBinding

    /**
     * @see PoiDetailBottomDialogFragment
     */
    companion object {

        const val TAG = "poi_detail_bottom_dialog_fragment"

        /**
         * Allows you to simply create new instance of [PoiDetailBottomDialogFragment]. You need to provide a valid [PoiData] object.
         *
         * @param poiData [PoiData] to be applied to the dialog content.
         */
        @JvmStatic
        fun newInstance(poiData: PoiData): PoiDetailBottomDialogFragment = PoiDetailBottomDialogFragment().apply {
            arguments = Bundle().apply {
                putParcelable(POI_DATA, poiData)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
            this.requireContext(),
            this.theme,
            this.resources.getDimensionPixelSize(R.dimen.defaultPeekHeight),
            if (preferencesManager.showcaseAllowed) SHOWCASE_BEHAVIOR_STATE else DEFAULT_BEHAVIOR_STATE
        )
    }

    // Using LayoutInflater from AppCompatActivity instead of provided inflater from onCreateView fix DialogFragment
    // styling problem (https://stackoverflow.com/q/32784009/3796931 or https://issuetracker.google.com/issues/37042151)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = LayoutPoiDetailInternalBinding.inflate(requireActivity().layoutInflater, container, false)
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

    /**
     * Register a callback to be invoked when a [PoiDetailBottomDialogFragment] is dismissed.
     *
     * @param listener [DialogFragmentListener] callback to invoke [PoiDetailBottomDialogFragment] dismiss.
     */
    fun setListener(listener: DialogFragmentListener) {
        viewModel?.setListener(listener) ?: run { this.listener = listener }
    }

    override fun onDestroy() {
        super.onDestroy()

        listener = null
    }
}