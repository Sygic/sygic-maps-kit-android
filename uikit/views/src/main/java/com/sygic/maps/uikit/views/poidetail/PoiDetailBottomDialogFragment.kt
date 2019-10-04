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
import android.widget.ProgressBar
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.sygic.maps.uikit.views.R
import com.sygic.maps.uikit.views.common.BaseBottomDialogFragment
import com.sygic.maps.uikit.views.common.extensions.getParcelableValue
import com.sygic.maps.uikit.views.databinding.LayoutPoiDetailInternalBinding
import com.sygic.maps.uikit.views.common.BottomSheetDialog
import com.sygic.maps.uikit.views.poidetail.component.PoiDetailComponent
import com.sygic.maps.uikit.views.poidetail.listener.DialogFragmentListener
import com.sygic.maps.uikit.views.poidetail.manager.PreferencesManager
import com.sygic.maps.uikit.views.poidetail.viewmodel.DEFAULT_BEHAVIOR_STATE
import com.sygic.maps.uikit.views.poidetail.viewmodel.PoiDetailInternalViewModel
import com.sygic.maps.uikit.views.poidetail.viewmodel.SHOWCASE_BEHAVIOR_STATE

private const val POI_DETAIL_COMPONENT = "poi_detail_component"

/**
 * A [PoiDetailBottomDialogFragment] is a custom version of the [DialogFragment] that shows a bottom sheet using custom
 * [BottomSheetDialog] instead of a floating dialog. It can be used for a visual representation of the [PoiDetailComponent] object.
 *
 * You can register an [Listener] using [setListener] method. Then you will be notified when dialog is dismissed.
 *
 * Content colors can be changed with the standard _colorBackground_, _textColorPrimary_, _textColorSecondary_ or
 * _colorAccent_ attribute.
 */
open class PoiDetailBottomDialogFragment : BaseBottomDialogFragment() {

    private var viewModel: PoiDetailInternalViewModel? = null

    private lateinit var preferencesManager: PreferencesManager
    private lateinit var binding: LayoutPoiDetailInternalBinding

    /**
     * Set a [PoiDetailComponent] for [PoiDetailBottomDialogFragment] content generation. If null, the loading [ProgressBar] view
     * will be displayed.
     *
     * @param [PoiDetailComponent] which will be used for fulfillment the [PoiDetailBottomDialogFragment] content.
     */
    var component: PoiDetailComponent?
        get() = arguments?.getParcelable(POI_DETAIL_COMPONENT)
        set(value) {
            arguments?.putParcelable(POI_DETAIL_COMPONENT, value)
            viewModel?.onComponentChanged(value)
        }

    /**
     * Register a callback to be invoked when a [PoiDetailBottomDialogFragment] is dismissed.
     *
     * @param [PoiDetailBottomDialogFragment.Listener] callback to invoke [PoiDetailBottomDialogFragment] dismiss.
     */
    var listener: Listener? = null
        get() = viewModel?.listener ?: field
        set(value) {
            field = value
            viewModel?.let { it.listener = value }
        }

    /**
     * @see PoiDetailBottomDialogFragment
     */
    companion object {

        const val TAG = "poi_detail_bottom_dialog_fragment"

        /**
         * Allows you to simply create new instance of [PoiDetailBottomDialogFragment]. You can provide a [PoiDetailComponent] object.
         *
         * @param component [PoiDetailComponent] to be applied to the dialog content.
         */
        @JvmStatic
        fun newInstance(component: PoiDetailComponent? = null): PoiDetailBottomDialogFragment =
            PoiDetailBottomDialogFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(POI_DETAIL_COMPONENT, component)
                }
            }
    }

    interface Listener : DialogFragmentListener {
        fun onNavigationButtonClick()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        preferencesManager = PreferencesManager(requireContext())

        viewModel = ViewModelProviders.of(
            this, PoiDetailInternalViewModel.Factory(requireActivity().application, preferencesManager)
        )[PoiDetailInternalViewModel::class.java].apply {
            this.dialogStateObservable.observe(
                this@PoiDetailBottomDialogFragment,
                Observer<Int> { setState(it) })

            this.onComponentChanged(arguments.getParcelableValue(POI_DETAIL_COMPONENT))
            this.listener = this@PoiDetailBottomDialogFragment.listener
            this@PoiDetailBottomDialogFragment.listener = null
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

        viewModel?.let { dialog.behavior?.addStateListener(it) }
    }

    override fun onDestroy() {
        super.onDestroy()

        listener = null
    }
}