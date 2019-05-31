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

package com.sygic.maps.uikit.views.poidetail

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
import com.sygic.maps.uikit.views.R
import com.sygic.maps.uikit.views.common.extensions.copyToClipboard
import com.sygic.maps.uikit.views.common.extensions.openEmail
import com.sygic.maps.uikit.views.common.extensions.openPhone
import com.sygic.maps.uikit.views.common.extensions.openUrl
import com.sygic.maps.uikit.views.databinding.LayoutPoiDetailInternalBinding
import com.sygic.maps.uikit.views.poidetail.data.PoiDetailData
import com.sygic.maps.uikit.views.poidetail.dialog.BottomSheetDialog
import com.sygic.maps.uikit.views.poidetail.listener.DialogFragmentListener
import com.sygic.maps.uikit.views.poidetail.manager.PreferencesManager
import com.sygic.maps.uikit.views.poidetail.viewmodel.DEFAULT_BEHAVIOR_STATE
import com.sygic.maps.uikit.views.poidetail.viewmodel.PoiDetailInternalViewModel
import com.sygic.maps.uikit.views.poidetail.viewmodel.SHOWCASE_BEHAVIOR_STATE

private const val POI_DETAIL_DATA = "poi_detail_data"

/**
 * A [PoiDetailBottomDialogFragment] is a custom version of the [DialogFragment] that shows a bottom sheet using custom
 * [BottomSheetDialog] instead of a floating dialog. It can be used for a visual representation of the [PoiDetailData] object.
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
         * Allows you to simply create new instance of [PoiDetailBottomDialogFragment]. You need to provide a valid [PoiDetailData] object.
         *
         * @param poiDetailData [PoiDetailData] to be applied to the dialog content.
         */
        @JvmStatic
        fun newInstance(poiDetailData: PoiDetailData): PoiDetailBottomDialogFragment = PoiDetailBottomDialogFragment().apply {
            arguments = Bundle().apply {
                putParcelable(POI_DETAIL_DATA, poiDetailData)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        preferencesManager = PreferencesManager(requireContext())

        viewModel = ViewModelProviders.of(
            this,
            PoiDetailInternalViewModel.ViewModelFactory(
                arguments?.getParcelable(POI_DETAIL_DATA)!!,
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
        binding.lifecycleOwner = this
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