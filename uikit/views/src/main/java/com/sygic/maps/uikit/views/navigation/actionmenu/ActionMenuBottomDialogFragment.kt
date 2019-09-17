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

package com.sygic.maps.uikit.views.navigation.actionmenu

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.sygic.maps.uikit.views.common.BottomSheetDialog
import com.sygic.maps.uikit.views.common.extensions.getInt
import com.sygic.maps.uikit.views.common.extensions.getParcelableValue
import com.sygic.maps.uikit.views.databinding.LayoutActionMenuInternalBinding
import com.sygic.maps.uikit.views.navigation.actionmenu.data.ActionMenuData
import com.sygic.maps.uikit.views.navigation.actionmenu.data.ActionMenuItem
import com.sygic.maps.uikit.views.navigation.actionmenu.listener.ActionMenuItemClickListener
import com.sygic.maps.uikit.views.navigation.actionmenu.viewmodel.ActionMenuInternalViewModel

internal const val DEFAULT_SPAN_COUNT = 3
private const val ACTION_MENU_DATA = "action_menu_data"
private const val ACTION_MENU_SPAN_COUNT = "action_menu_span_count"

/**
 * A [ActionMenuBottomDialogFragment] is a custom version of the [DialogFragment] that shows a bottom sheet using custom
 * [BottomSheetDialog] instead of a floating dialog. It allows you to simply add [ActionMenuItem]'s and set the
 * [ActionMenuItemClickListener].
 *
 * Content colors can be changed with the standard _colorBackground_, _textColorPrimary_, _textColorSecondary_ attribute
 * or directly inside the [ActionMenuItem].
 */
@Suppress("MemberVisibilityCanBePrivate", "unused")
open class ActionMenuBottomDialogFragment : AppCompatDialogFragment() {

    private var viewModel: ActionMenuInternalViewModel? = null

    private lateinit var binding: LayoutActionMenuInternalBinding

    /**
     * Set a [ActionMenuData] for the [ActionMenuBottomDialogFragment] content fulfillment.
     *
     * @param [ActionMenuData] which will be used for fulfillment the [ActionMenuBottomDialogFragment] content.
     */
    var data: ActionMenuData
        get() = arguments?.getParcelable(ACTION_MENU_DATA) ?: ActionMenuData.empty
        set(value) {
            arguments?.putParcelable(ACTION_MENU_DATA, value)
            viewModel?.actionMenuData = value
        }

    /**
     * Register a callback to be invoked when a [ActionMenuBottomDialogFragment] menu item click has been made.
     *
     * @param [ActionMenuItemClickListener] callback to invoke [ActionMenuBottomDialogFragment] item click.
     */
    var itemClickListener: ActionMenuItemClickListener? = null
        get() = viewModel?.listener ?: field
        set(value) {
            field = value
            viewModel?.let { it.listener = value }
        }

    /**
     * A *[spanCount]* define the number of columns to be used. The default value is [DEFAULT_SPAN_COUNT].
     *
     * @param [Int] wanted number of columns.
     *
     * @return [Int] current columns value.
     */
    var spanCount: Int
        get() = arguments?.getInt(ACTION_MENU_SPAN_COUNT) ?: DEFAULT_SPAN_COUNT
        set(value) {
            arguments?.putInt(ACTION_MENU_SPAN_COUNT, value)
            viewModel?.spanCount!!.value = value
        }

    /**
     * @see ActionMenuBottomDialogFragment
     */
    companion object {

        const val TAG = "action_menu_bottom_dialog_fragment"

        /**
         * Allows you to simply create new instance of [ActionMenuBottomDialogFragment]. You can provide a [ActionMenuData]
         * object and spanCount value.
         *
         * @param actionMenuData [ActionMenuData] to be applied to the dialog content.
         * @param spanCount [Int] to be applied to the dialog content.
         */
        @JvmStatic
        fun newInstance(
            actionMenuData: ActionMenuData,
            spanCount: Int = DEFAULT_SPAN_COUNT
        ): ActionMenuBottomDialogFragment = ActionMenuBottomDialogFragment().apply {
            arguments = Bundle().apply {
                putInt(ACTION_MENU_SPAN_COUNT, spanCount)
                putParcelable(ACTION_MENU_DATA, actionMenuData)
            }
        }
    }

    override fun getDialog(): BottomSheetDialog = super.getDialog() as BottomSheetDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel =
            ViewModelProviders.of(this@ActionMenuBottomDialogFragment).get(ActionMenuInternalViewModel::class.java)
                .apply {
                    this.spanCount.value = arguments.getInt(ACTION_MENU_SPAN_COUNT, DEFAULT_SPAN_COUNT)
                    this.actionMenuData = arguments.getParcelableValue(ACTION_MENU_DATA) ?: ActionMenuData.empty
                    this.listener = this@ActionMenuBottomDialogFragment.itemClickListener
                }
        this.itemClickListener = null
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(
            this.requireContext(),
            this.theme,
            initialState = BottomSheetBehavior.STATE_EXPANDED
        )
    }

    // Using LayoutInflater from AppCompatActivity instead of provided inflater from onCreateView fix DialogFragment
    // styling problem (https://stackoverflow.com/q/32784009/3796931 or https://issuetracker.google.com/issues/37042151)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = LayoutActionMenuInternalBinding.inflate(requireActivity().layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.actionMenuInternalViewModel = viewModel
    }

    override fun onDestroy() {
        super.onDestroy()

        itemClickListener = null
    }
}