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

package com.sygic.maps.module.navigation

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.sygic.maps.module.common.KEY_DISTANCE_UNITS
import com.sygic.maps.module.common.MapFragmentWrapper
import com.sygic.maps.module.common.component.DISTANCE_UNITS_DEFAULT_VALUE
import com.sygic.maps.module.common.di.util.ModuleBuilder
import com.sygic.maps.module.navigation.component.*
import com.sygic.maps.module.navigation.databinding.LayoutNavigationBinding
import com.sygic.maps.module.navigation.di.DaggerNavigationComponent
import com.sygic.maps.module.navigation.di.NavigationComponent
import com.sygic.maps.module.navigation.listener.EventListener
import com.sygic.maps.module.navigation.listener.EventListenerWrapper
import com.sygic.maps.module.navigation.types.SignpostType
import com.sygic.maps.module.navigation.viewmodel.NavigationFragmentViewModel
import com.sygic.maps.uikit.viewmodels.navigation.infobar.InfobarViewModel
import com.sygic.maps.uikit.viewmodels.navigation.infobar.button.InfobarButtonType
import com.sygic.maps.uikit.viewmodels.navigation.infobar.button.OnInfobarButtonClickListener
import com.sygic.maps.uikit.viewmodels.navigation.infobar.button.OnInfobarButtonClickListenerWrapper
import com.sygic.maps.uikit.viewmodels.navigation.infobar.text.InfobarTextDataWrapper
import com.sygic.maps.uikit.viewmodels.navigation.infobar.text.InfobarTextType
import com.sygic.maps.uikit.viewmodels.navigation.lanes.LanesViewModel
import com.sygic.maps.uikit.viewmodels.navigation.preview.RoutePreviewControlsViewModel
import com.sygic.maps.uikit.viewmodels.navigation.signpost.FullSignpostViewModel
import com.sygic.maps.uikit.viewmodels.navigation.signpost.SimplifiedSignpostViewModel
import com.sygic.maps.uikit.viewmodels.navigation.speed.CurrentSpeedViewModel
import com.sygic.maps.uikit.viewmodels.navigation.speed.SpeedLimitViewModel
import com.sygic.maps.uikit.views.common.extensions.*
import com.sygic.maps.uikit.views.common.units.DistanceUnit
import com.sygic.maps.uikit.views.navigation.actionmenu.ActionMenuBottomDialogFragment
import com.sygic.maps.uikit.views.navigation.actionmenu.data.ActionMenuData
import com.sygic.maps.uikit.views.navigation.actionmenu.listener.ActionMenuItemClickListener
import com.sygic.maps.uikit.views.navigation.actionmenu.listener.ActionMenuItemsProviderWrapper
import com.sygic.maps.uikit.views.navigation.infobar.Infobar
import com.sygic.maps.uikit.views.navigation.infobar.items.InfobarTextData
import com.sygic.maps.uikit.views.navigation.lanes.SimpleLanesView
import com.sygic.maps.uikit.views.navigation.preview.RoutePreviewControls
import com.sygic.maps.uikit.views.navigation.signpost.FullSignpostView
import com.sygic.maps.uikit.views.navigation.signpost.SimplifiedSignpostView
import com.sygic.maps.uikit.views.navigation.speed.CurrentSpeedView
import com.sygic.maps.uikit.views.navigation.speed.SpeedLimitView
import com.sygic.sdk.route.Route

const val NAVIGATION_FRAGMENT_TAG = "navigation_fragment_tag"
internal const val KEY_SIGNPOST_ENABLED = "signpost_enabled"
internal const val KEY_SIGNPOST_TYPE = "signpost_type"
internal const val KEY_LANES_VIEW_ENABLED = "lanes_view_enabled"
internal const val KEY_PREVIEW_CONTROLS_ENABLED = "preview_controls_enabled"
internal const val KEY_PREVIEW_MODE = "preview_mode"
internal const val KEY_INFOBAR_ENABLED = "infobar_enabled"
internal const val KEY_CURRENT_SPEED_ENABLED = "current_speed_enabled"
internal const val KEY_SPEED_LIMIT_ENABLED = "speed_limit_enabled"
internal const val KEY_ROUTE = "route"

/**
 * A *[NavigationFragment]* is the core component for any navigation operation. It can be easily used for the navigation
 * purposes. By setting the [route] object will start the navigation process. Any pre build-in element such as
 * [FullSignpostView], [SimplifiedSignpostView], [Infobar], [RoutePreviewControls], [CurrentSpeedView] or [SpeedLimitView]
 * may be activated or deactivated and styled.
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
class NavigationFragment : MapFragmentWrapper<NavigationFragment, NavigationComponent, ModuleBuilder<NavigationComponent>, NavigationFragmentViewModel>(DaggerNavigationComponent.builder()),
    EventListenerWrapper, OnInfobarButtonClickListenerWrapper, InfobarTextDataWrapper, ActionMenuItemsProviderWrapper {

    override lateinit var fragmentViewModel: NavigationFragmentViewModel
    private lateinit var routePreviewControlsViewModel: RoutePreviewControlsViewModel
    private lateinit var infobarViewModel: InfobarViewModel
    private lateinit var currentSpeedViewModel: CurrentSpeedViewModel
    private lateinit var speedLimitViewModel: SpeedLimitViewModel

    override val infobarTextDataProvider: LiveData<Map<InfobarTextType, InfobarTextData>> = MutableLiveData(mutableMapOf())
    override val infobarButtonClickListenerProvidersMap: LiveData<Map<InfobarButtonType, OnInfobarButtonClickListener?>> = MutableLiveData(mutableMapOf())
    override val actionMenuItemsProvider: LiveData<ActionMenuItemsProviderWrapper.ProviderComponent> = MutableLiveData()
    override val eventListenerProvider: LiveData<EventListener> = MutableLiveData()

    /**
     * A *[distanceUnit]* defines all available [DistanceUnit]'s type.
     *
     * [DistanceUnit.KILOMETERS] (default) -> Kilometers/meters are used as the distance unit.
     *
     * [DistanceUnit.MILES_YARDS] -> Miles/yards are used as the distance unit.
     *
     * [DistanceUnit.MILES_FEETS] -> Miles/feets are used as the distance unit.
     */
    var distanceUnit: DistanceUnit
        get() = if (::fragmentViewModel.isInitialized) {
            fragmentViewModel.distanceUnit
        } else arguments.getParcelableValue(KEY_DISTANCE_UNITS) ?: DISTANCE_UNITS_DEFAULT_VALUE
        set(value) {
            arguments = Bundle(arguments).apply { putParcelable(KEY_DISTANCE_UNITS, value) }
            if (::fragmentViewModel.isInitialized) {
                fragmentViewModel.distanceUnit = value
            }
        }

    /**
     * A *[signpostEnabled]* modifies the SignpostView ([FullSignpostView] or [SimplifiedSignpostView]) visibility.
     *
     * @param [Boolean] true to enable the SignpostView, false otherwise.
     *
     * @return whether the SignpostView is on or off.
     */
    var signpostEnabled: Boolean
        get() = if (::fragmentViewModel.isInitialized) {
            fragmentViewModel.signpostEnabled.value!!
        } else arguments.getBoolean(KEY_SIGNPOST_ENABLED, SIGNPOST_ENABLED_DEFAULT_VALUE)
        set(value) {
            arguments = Bundle(arguments).apply { putBoolean(KEY_SIGNPOST_ENABLED, value) }
            if (::fragmentViewModel.isInitialized) {
                fragmentViewModel.signpostEnabled.value = value
            }
        }

    /**
     * A *[previewMode]* modifies whether the preview mode is on or off.
     *
     * @param [Boolean] true to enable the [previewMode], false otherwise.
     *
     * @return whether the [previewMode] is on or off.
     */
    var previewMode: Boolean
        get() = if (::fragmentViewModel.isInitialized) {
            fragmentViewModel.previewMode.value!!
        } else arguments.getBoolean(KEY_PREVIEW_MODE, PREVIEW_MODE_DEFAULT_VALUE)
        set(value) {
            arguments = Bundle(arguments).apply { putBoolean(KEY_PREVIEW_MODE, value) }
            if (::fragmentViewModel.isInitialized) {
                fragmentViewModel.previewMode.value = value
            }
        }

    /**
     * A *[lanesViewEnabled]* modifies the [SimpleLanesView] visibility.
     *
     * @param [Boolean] true to enable the [SimpleLanesView], false otherwise.
     *
     * @return whether the [SimpleLanesView] is on or off.
     */
    var lanesViewEnabled: Boolean
    get() = if (::fragmentViewModel.isInitialized) {
        fragmentViewModel.lanesViewEnabled.value!!
    } else arguments.getBoolean(KEY_LANES_VIEW_ENABLED, LANES_VIEW_ENABLED_DEFAULT_VALUE)
        set(value) {
            arguments = Bundle(arguments).apply { putBoolean(KEY_LANES_VIEW_ENABLED, value) }
            if (::fragmentViewModel.isInitialized) {
                fragmentViewModel.lanesViewEnabled.value = value
            }
        }

    /**
     * A *[previewControlsEnabled]* modifies the [RoutePreviewControls] visibility.
     *
     * @param [Boolean] true to enable the [RoutePreviewControls], false otherwise.
     *
     * @return whether the [RoutePreviewControls] is on or off.
     */
    var previewControlsEnabled: Boolean
        get() = if (::fragmentViewModel.isInitialized) {
            fragmentViewModel.previewControlsEnabled.value!!
        } else arguments.getBoolean(KEY_PREVIEW_CONTROLS_ENABLED, PREVIEW_CONTROLS_ENABLED_DEFAULT_VALUE)
        set(value) {
            arguments = Bundle(arguments).apply { putBoolean(KEY_PREVIEW_CONTROLS_ENABLED, value) }
            if (::fragmentViewModel.isInitialized) {
                fragmentViewModel.previewControlsEnabled.value = value
            }
        }

    /**
     * A *[infobarEnabled]* modifies the [Infobar] view visibility.
     *
     * @param [Boolean] true to enable the [Infobar] view, false otherwise.
     *
     * @return whether the [Infobar] view is on or off.
     */
    var infobarEnabled: Boolean
        get() = if (::fragmentViewModel.isInitialized) {
            fragmentViewModel.infobarEnabled.value!!
        } else arguments.getBoolean(KEY_INFOBAR_ENABLED, INFOBAR_ENABLED_DEFAULT_VALUE)
        set(value) {
            arguments = Bundle(arguments).apply { putBoolean(KEY_INFOBAR_ENABLED, value) }
            if (::fragmentViewModel.isInitialized) {
                fragmentViewModel.infobarEnabled.value = value
            }
        }

    /**
     * A *[currentSpeedEnabled]* modifies the [CurrentSpeedView] visibility.
     *
     * @param [Boolean] true to enable the [CurrentSpeedView], false otherwise.
     *
     * @return whether the [CurrentSpeedView] is on or off.
     */
    var currentSpeedEnabled: Boolean
        get() = if (::fragmentViewModel.isInitialized) {
            fragmentViewModel.currentSpeedEnabled.value!!
        } else arguments.getBoolean(KEY_CURRENT_SPEED_ENABLED, CURRENT_SPEED_ENABLED_DEFAULT_VALUE)
        set(value) {
            arguments = Bundle(arguments).apply { putBoolean(KEY_CURRENT_SPEED_ENABLED, value) }
            if (::fragmentViewModel.isInitialized) {
                fragmentViewModel.currentSpeedEnabled.value = value
            }
        }

    /**
     * A *[speedLimitEnabled]* modifies the [SpeedLimitView] visibility.
     *
     * @param [Boolean] true to enable the [SpeedLimitView], false otherwise.
     *
     * @return whether the [SpeedLimitView] is on or off.
     */
    var speedLimitEnabled: Boolean
        get() = if (::fragmentViewModel.isInitialized) {
            fragmentViewModel.speedLimitEnabled.value!!
        } else arguments.getBoolean(KEY_SPEED_LIMIT_ENABLED, SPEED_LIMIT_ENABLED_DEFAULT_VALUE)
        set(value) {
            arguments = Bundle(arguments).apply { putBoolean(KEY_SPEED_LIMIT_ENABLED, value) }
            if (::fragmentViewModel.isInitialized) {
                fragmentViewModel.speedLimitEnabled.value = value
            }
        }

    /**
     * If not-null *[route]* is defined, then it will be used as an route for navigation.
     *
     * @param [Route] route object to be processed.
     *
     * @return [Route] the current route value or `null` if not yet defined.
     */
    var route: Route?
        get() = if (::fragmentViewModel.isInitialized) {
            fragmentViewModel.route
        } else arguments.getParcelableValue(KEY_ROUTE)
        set(value) {
            arguments = Bundle(arguments).apply { putParcelable(KEY_ROUTE, value) }
            if (::fragmentViewModel.isInitialized && value != null) {
                fragmentViewModel.route = value
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fragmentViewModel = viewModelOf(NavigationFragmentViewModel::class.java, arguments).apply {
            activityFinishObservable.observe(this@NavigationFragment, Observer { finish() })
            infoToastObservable.observe(this@NavigationFragment, Observer { showInfoToast(it) })
            actionMenuHideObservable.observe(this@NavigationFragment, Observer { hideActionMenu() })
            actionMenuShowObservable.observe(this@NavigationFragment, Observer { showActionMenu(it) })
            actionMenuItemClickListenerObservable.observe(
                this@NavigationFragment,
                Observer { setActionMenuItemClickListener(it) }
            )
        }
        infobarViewModel = viewModelOf(InfobarViewModel::class.java)
        speedLimitViewModel = viewModelOf(SpeedLimitViewModel::class.java)
        currentSpeedViewModel = viewModelOf(CurrentSpeedViewModel::class.java)
        routePreviewControlsViewModel = viewModelOf(RoutePreviewControlsViewModel::class.java)

        lifecycle.addObserver(infobarViewModel)
        lifecycle.addObserver(fragmentViewModel)
        lifecycle.addObserver(speedLimitViewModel)
        lifecycle.addObserver(currentSpeedViewModel)
        lifecycle.addObserver(routePreviewControlsViewModel)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        LayoutNavigationBinding.inflate(inflater, container, false).apply {
            navigationFragmentViewModel = fragmentViewModel
            infobarViewModel = this@NavigationFragment.infobarViewModel
            routePreviewControlsViewModel = this@NavigationFragment.routePreviewControlsViewModel
            currentSpeedViewModel = this@NavigationFragment.currentSpeedViewModel
            speedLimitViewModel = this@NavigationFragment.speedLimitViewModel
            lifecycleOwner = this@NavigationFragment

            signpostView.setOnInflateListener { _, view ->
                DataBindingUtil.bind<ViewDataBinding>(view)?.let {
                    when (view) {
                        is FullSignpostView -> {
                            it.setVariable(BR.signpostViewModel, viewModelOf(FullSignpostViewModel::class.java))
                            it.setVariable(BR.lanesViewModel, viewModelOf(LanesViewModel::class.java))
                        }
                        is SimplifiedSignpostView -> it.setVariable(BR.signpostViewModel, viewModelOf(SimplifiedSignpostViewModel::class.java))
                        else -> throw IllegalArgumentException("Unknown view in the SignpostView viewStub.")
                    }

                    it.lifecycleOwner = this@NavigationFragment
                }
            }

            lanesView.setOnInflateListener {_, view ->
                DataBindingUtil.bind<ViewDataBinding>(view)?.let {
                    it.setVariable(BR.lanesViewModel, viewModelOf(LanesViewModel::class.java))
                    it.lifecycleOwner = this@NavigationFragment
                }
            }

            with(root as ViewGroup) {
                super.onCreateView(inflater, this, savedInstanceState)?.let {
                    addView(it, 0, ConstraintLayout.LayoutParams(0, 0).apply {
                        topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                        bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
                        startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                        endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
                    })
                }
            }
        }.root

    private fun showActionMenu(actionMenuData: ActionMenuData) {
        ActionMenuBottomDialogFragment.newInstance(actionMenuData).apply {
            itemClickListener = fragmentViewModel.actionMenuItemClickListener
        }.show(fragmentManager, ActionMenuBottomDialogFragment.TAG)
    }

    private fun setActionMenuItemClickListener(listener: ActionMenuItemClickListener) {
        fragmentManager?.findFragmentByTag(ActionMenuBottomDialogFragment.TAG)?.let { fragment ->
            (fragment as ActionMenuBottomDialogFragment).itemClickListener = listener
        }
    }

    private fun hideActionMenu() {
        fragmentManager?.findFragmentByTag(ActionMenuBottomDialogFragment.TAG)?.let { fragment ->
            (fragment as DialogFragment).dismiss()
        }
    }

    /**
     * Register a custom callback to be invoked when a navigation event has been made.
     *
     * @param eventListener [EventListener] callback to invoke [EventListener] event.
     */
    fun setEventListener(eventListener: EventListener?) {
        eventListenerProvider.asMutable().value = eventListener
    }

    /**
     * Set a custom [ActionMenuData] and [ActionMenuItemClickListener] for the [ActionMenuBottomDialogFragment] content fulfillment.
     *
     * @param actionMenuData [ActionMenuData] which will be used for fulfillment the [ActionMenuBottomDialogFragment] content.
     * @param actionMenuItemClickListener [ActionMenuItemClickListener] callback to invoke action menu item click.
     *
     */
    fun setActionMenuItems(
        actionMenuData: ActionMenuData,
        actionMenuItemClickListener: ActionMenuItemClickListener
    ) {
        actionMenuItemsProvider.asMutable().value =
            ActionMenuItemsProviderWrapper.ProviderComponent(actionMenuData, actionMenuItemClickListener)
    }

    /**
     * Register a custom callback to be invoked when a click to the infobar button has been made.
     *
     * @param buttonType [InfobarButtonType] infobar button type to be used.
     * @param onClickListener [OnInfobarButtonClickListener] callback to invoke on infobar button click.
     *
     */
    fun setOnInfobarButtonClickListener(
        buttonType: InfobarButtonType,
        onClickListener: OnInfobarButtonClickListener?
    ) {
        infobarButtonClickListenerProvidersMap.asMutable().put(buttonType, onClickListener)
    }

    /**
     * Sets the [InfobarTextData] which will be converted to the formatted text for the specified [InfobarTextType].
     *
     * @param textType [InfobarTextType] infobar text type to be used.
     * @param textData [InfobarTextData] infobar text data with valid data, empty list otherwise.
     */
    fun setInfobarTextData(textType: InfobarTextType, textData: InfobarTextData) {
        infobarTextDataProvider.asMutable().put(textType, textData)
    }

    override fun onDestroy() {
        super.onDestroy()

        lifecycle.removeObserver(infobarViewModel)
        lifecycle.removeObserver(fragmentViewModel)
        lifecycle.removeObserver(speedLimitViewModel)
        lifecycle.removeObserver(currentSpeedViewModel)
        lifecycle.removeObserver(routePreviewControlsViewModel)
    }

    override fun resolveAttributes(context: Context, attributes: AttributeSet) {
        with(context.obtainStyledAttributes(attributes, R.styleable.NavigationFragment)) {
            if (hasValue(R.styleable.NavigationFragment_sygic_navigation_distanceUnit)) {
                distanceUnit = DistanceUnit.atIndex(
                    getInt(
                        R.styleable.NavigationFragment_sygic_navigation_distanceUnit,
                        DISTANCE_UNITS_DEFAULT_VALUE.ordinal
                    )
                )
            }
            if (hasValue(R.styleable.NavigationFragment_sygic_lanes_view_enabled)) {
                lanesViewEnabled =
                    getBoolean(
                        R.styleable.NavigationFragment_sygic_lanes_view_enabled,
                        LANES_VIEW_ENABLED_DEFAULT_VALUE
                    )
            }
            if (hasValue(R.styleable.NavigationFragment_sygic_signpost_enabled)) {
                signpostEnabled =
                    getBoolean(
                        R.styleable.NavigationFragment_sygic_signpost_enabled,
                        SIGNPOST_ENABLED_DEFAULT_VALUE
                    )
            }
            if (hasValue(R.styleable.NavigationFragment_sygic_signpost_type)) {
                arguments = Bundle(arguments).apply {
                    putParcelable(
                        KEY_SIGNPOST_TYPE, SignpostType.atIndex(
                            getInt(
                                R.styleable.NavigationFragment_sygic_signpost_type,
                                SIGNPOST_TYPE_DEFAULT_VALUE.ordinal
                            )
                        )
                    )
                }
            }
            if (hasValue(R.styleable.NavigationFragment_sygic_navigation_previewMode)) {
                previewMode =
                    getBoolean(
                        R.styleable.NavigationFragment_sygic_navigation_previewMode,
                        PREVIEW_MODE_DEFAULT_VALUE
                    )
            }
            if (hasValue(R.styleable.NavigationFragment_sygic_previewControls_enabled)) {
                previewControlsEnabled =
                    getBoolean(
                        R.styleable.NavigationFragment_sygic_previewControls_enabled,
                        PREVIEW_CONTROLS_ENABLED_DEFAULT_VALUE
                    )
            }
            if (hasValue(R.styleable.NavigationFragment_sygic_infobar_enabled)) {
                infobarEnabled =
                    getBoolean(
                        R.styleable.NavigationFragment_sygic_infobar_enabled,
                        INFOBAR_ENABLED_DEFAULT_VALUE
                    )
            }
            if (hasValue(R.styleable.NavigationFragment_sygic_current_speed_enabled)) {
                currentSpeedEnabled =
                    getBoolean(
                        R.styleable.NavigationFragment_sygic_current_speed_enabled,
                        CURRENT_SPEED_ENABLED_DEFAULT_VALUE
                    )
            }
            if (hasValue(R.styleable.NavigationFragment_sygic_speed_limit_enabled)) {
                speedLimitEnabled =
                    getBoolean(
                        R.styleable.NavigationFragment_sygic_speed_limit_enabled,
                        SPEED_LIMIT_ENABLED_DEFAULT_VALUE
                    )
            }

            recycle()
        }
    }
}
