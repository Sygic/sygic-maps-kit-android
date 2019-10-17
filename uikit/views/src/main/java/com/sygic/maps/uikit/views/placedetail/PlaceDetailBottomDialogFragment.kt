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

package com.sygic.maps.uikit.views.placedetail

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
import com.sygic.maps.uikit.views.common.BottomSheetDialog
import com.sygic.maps.uikit.views.common.extensions.getParcelableValue
import com.sygic.maps.uikit.views.databinding.LayoutPlaceDetailInternalBinding
import com.sygic.maps.uikit.views.placedetail.PlaceDetailBottomDialogFragment.Listener
import com.sygic.maps.uikit.views.placedetail.component.PlaceDetailComponent
import com.sygic.maps.uikit.views.placedetail.listener.DialogFragmentListener
import com.sygic.maps.uikit.views.placedetail.manager.PreferencesManager
import com.sygic.maps.uikit.views.placedetail.viewmodel.DEFAULT_BEHAVIOR_STATE
import com.sygic.maps.uikit.views.placedetail.viewmodel.PlaceDetailInternalViewModel
import com.sygic.maps.uikit.views.placedetail.viewmodel.SHOWCASE_BEHAVIOR_STATE

private const val PLACE_DETAIL_COMPONENT = "place_detail_component"

/**
 * A [PlaceDetailBottomDialogFragment] is a custom version of the [DialogFragment] that shows a bottom sheet using custom
 * [BottomSheetDialog] instead of a floating dialog. It can be used for a visual representation of the [PlaceDetailComponent] object.
 *
 * You can register an [Listener] using [setListener] method. Then you will be notified when dialog is dismissed.
 *
 * Content colors can be changed with the standard _colorBackground_, _textColorPrimary_, _textColorSecondary_ or
 * _colorAccent_ attribute.
 */
open class PlaceDetailBottomDialogFragment : BaseBottomDialogFragment() {

    private var viewModel: PlaceDetailInternalViewModel? = null

    private lateinit var preferencesManager: PreferencesManager
    private lateinit var binding: LayoutPlaceDetailInternalBinding

    /**
     * Set a [PlaceDetailComponent] for [PlaceDetailBottomDialogFragment] content generation. If null, the loading [ProgressBar] view
     * will be displayed.
     *
     * @param [PlaceDetailComponent] which will be used for fulfillment the [PlaceDetailBottomDialogFragment] content.
     */
    var component: PlaceDetailComponent?
        get() = arguments?.getParcelable(PLACE_DETAIL_COMPONENT)
        set(value) {
            arguments?.putParcelable(PLACE_DETAIL_COMPONENT, value)
            viewModel?.onComponentChanged(value)
        }

    /**
     * Register a callback to be invoked when a [PlaceDetailBottomDialogFragment] is dismissed.
     *
     * @param [PlaceDetailBottomDialogFragment.Listener] callback to invoke [PlaceDetailBottomDialogFragment] dismiss.
     */
    var listener: Listener? = null
        get() = viewModel?.listener ?: field
        set(value) {
            field = value
            viewModel?.let { it.listener = value }
        }

    /**
     * @see PlaceDetailBottomDialogFragment
     */
    companion object {

        const val TAG = "place_detail_bottom_dialog_fragment"

        /**
         * Allows you to simply create new instance of [PlaceDetailBottomDialogFragment]. You can provide a [PlaceDetailComponent] object.
         *
         * @param component [PlaceDetailComponent] to be applied to the dialog content.
         */
        @JvmStatic
        fun newInstance(component: PlaceDetailComponent? = null): PlaceDetailBottomDialogFragment =
            PlaceDetailBottomDialogFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(PLACE_DETAIL_COMPONENT, component)
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
            this, PlaceDetailInternalViewModel.Factory(requireActivity().application, preferencesManager)
        )[PlaceDetailInternalViewModel::class.java].apply {
            dialogStateObservable.observe(this@PlaceDetailBottomDialogFragment, Observer { setState(it) })

            this.onComponentChanged(arguments.getParcelableValue(PLACE_DETAIL_COMPONENT))
            this.listener = this@PlaceDetailBottomDialogFragment.listener
            this@PlaceDetailBottomDialogFragment.listener = null
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
        binding = LayoutPlaceDetailInternalBinding.inflate(requireActivity().layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewModel = viewModel
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