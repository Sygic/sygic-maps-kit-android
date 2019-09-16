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

import android.os.Bundle
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.sygic.maps.module.common.MapFragmentWrapper
import com.sygic.maps.module.navigation.component.*
import com.sygic.maps.module.navigation.databinding.LayoutNavigationBinding
import com.sygic.maps.module.navigation.di.DaggerNavigationComponent
import com.sygic.maps.module.navigation.di.NavigationComponent
import com.sygic.maps.module.navigation.types.SignpostType
import com.sygic.maps.module.navigation.viewmodel.NavigationFragmentViewModel
import com.sygic.maps.uikit.viewmodels.common.regional.units.DistanceUnit
import com.sygic.maps.uikit.viewmodels.navigation.lanes.LanesViewModel
import com.sygic.maps.uikit.viewmodels.navigation.infobar.text.InfobarTextType
import com.sygic.maps.uikit.views.common.units.DistanceUnit
import com.sygic.maps.uikit.viewmodels.navigation.infobar.InfobarViewModel
import com.sygic.maps.uikit.viewmodels.navigation.infobar.button.InfobarButtonType
import com.sygic.maps.uikit.viewmodels.navigation.infobar.button.OnInfobarButtonClickListener
import com.sygic.maps.uikit.viewmodels.navigation.infobar.button.OnInfobarButtonClickListenerWrapper
import com.sygic.maps.uikit.viewmodels.navigation.infobar.text.InfobarTextDataWrapper
import com.sygic.maps.uikit.viewmodels.navigation.preview.RoutePreviewControlsViewModel
import com.sygic.maps.uikit.viewmodels.navigation.signpost.FullSignpostViewModel
import com.sygic.maps.uikit.viewmodels.navigation.signpost.SimplifiedSignpostViewModel
import com.sygic.maps.uikit.views.common.extensions.asMutable
import com.sygic.maps.uikit.views.common.extensions.finish
import com.sygic.maps.uikit.views.common.extensions.getBoolean
import com.sygic.maps.uikit.views.common.extensions.getParcelableValue
import com.sygic.maps.uikit.views.navigation.lanes.SimpleLanesView
import com.sygic.maps.uikit.views.navigation.infobar.Infobar
import com.sygic.maps.uikit.views.navigation.infobar.items.InfobarTextData
import com.sygic.maps.uikit.views.navigation.preview.RoutePreviewControls
import com.sygic.maps.uikit.views.navigation.signpost.FullSignpostView
import com.sygic.maps.uikit.views.navigation.signpost.SimplifiedSignpostView
import com.sygic.sdk.route.RouteInfo

const val NAVIGATION_FRAGMENT_TAG = "navigation_fragment_tag"
internal const val KEY_DISTANCE_UNITS = "distance_units"
internal const val KEY_SIGNPOST_ENABLED = "signpost_enabled"
internal const val KEY_SIGNPOST_TYPE = "signpost_type"
internal const val KEY_LANES_VIEW_ENABLED = "lanes_view_enabled"
internal const val KEY_PREVIEW_CONTROLS_ENABLED = "preview_controls_enabled"
internal const val KEY_PREVIEW_MODE = "preview_mode"
internal const val KEY_INFOBAR_ENABLED = "infobar_enabled"
internal const val KEY_ROUTE_INFO = "route_info"

/**
 * A *[NavigationFragment]* is the core component for any navigation operation. It can be easily used for the navigation
 * purposes. By setting the [routeInfo] object will start the navigation process. Any pre build-in element such as
 * [FullSignpostView], [SimplifiedSignpostView], [Infobar], [CurrentSpeed] or [SpeedLimit] may be activated or deactivated and styled.
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
class NavigationFragment : MapFragmentWrapper<NavigationFragmentViewModel>(),
    OnInfobarButtonClickListenerWrapper, InfobarTextDataWrapper {

    override lateinit var fragmentViewModel: NavigationFragmentViewModel
    private lateinit var routePreviewControlsViewModel: RoutePreviewControlsViewModel
    private lateinit var infobarViewModel: InfobarViewModel

    override val infobarTextDataProvider: LiveData<InfobarTextDataWrapper.ProviderComponent> = MutableLiveData()
    override val infobarButtonClickListenerProvider: LiveData<OnInfobarButtonClickListenerWrapper.ProviderComponent> = MutableLiveData()

    override fun executeInjector() =
        injector<NavigationComponent, NavigationComponent.Builder>(DaggerNavigationComponent.builder()) { it.inject(this) }

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
     * If not-null *[routeInfo]* is defined, then it will be used as an navigation routeInfo.
     *
     * @param [RouteInfo] route info object to be processed.
     *
     * @return [RouteInfo] the current route info value or `null` if not yet defined.
     */
    var routeInfo: RouteInfo?
        get() = if (::fragmentViewModel.isInitialized) {
            fragmentViewModel.routeInfo.value
        } else arguments.getParcelableValue(KEY_ROUTE_INFO)
        set(value) {
            arguments = Bundle(arguments).apply { putParcelable(KEY_ROUTE_INFO, value) }
            if (::fragmentViewModel.isInitialized && value != null) {
                fragmentViewModel.routeInfo.value = value
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fragmentViewModel = viewModelOf(NavigationFragmentViewModel::class.java, arguments).apply {
            this.activityFinishObservable.observe(
                this@NavigationFragment,
                Observer<Any> { finish() })
        }
        infobarViewModel = viewModelOf(InfobarViewModel::class.java)
        routePreviewControlsViewModel = viewModelOf(RoutePreviewControlsViewModel::class.java)
        lifecycle.addObserver(fragmentViewModel)
        lifecycle.addObserver(infobarViewModel)
        lifecycle.addObserver(routePreviewControlsViewModel)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        LayoutNavigationBinding.inflate(inflater, container, false).apply {
            navigationFragmentViewModel = fragmentViewModel
            infobarViewModel = this@NavigationFragment.infobarViewModel
            routePreviewControlsViewModel = this@NavigationFragment.routePreviewControlsViewModel
            lifecycleOwner = this@NavigationFragment

            signpostView.setOnInflateListener { _, view ->
                DataBindingUtil.bind<ViewDataBinding>(view)?.let {
                    when (view) {
                        is FullSignpostView -> {
                            it.setVariable(
                                BR.signpostViewModel, viewModelOf(FullSignpostViewModel::class.java)
                            )
                            it.setVariable(
                                BR.lanesViewModel, viewModelOf(LanesViewModel::class.java)
                            )
                        }
                        is SimplifiedSignpostView -> it.setVariable(
                            BR.signpostViewModel, viewModelOf(SimplifiedSignpostViewModel::class.java)
                        )
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
                super.onCreateView(inflater, this, savedInstanceState)?.let { addView(it, 0) }
            }
        }.root

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
        infobarButtonClickListenerProvider.asMutable().value =
            OnInfobarButtonClickListenerWrapper.ProviderComponent(onClickListener, buttonType)
    }

    /**
     * Sets the [InfobarTextData] which will be converted to the formatted text for the specified [InfobarTextType].
     *
     * @param textType [InfobarTextType] infobar text type to be used.
     * @param textData [InfobarTextData] infobar text data with valid data, empty list otherwise.
     */
    fun setInfobarTextData(textType: InfobarTextType, textData: InfobarTextData) {
        infobarTextDataProvider.asMutable().value =
            InfobarTextDataWrapper.ProviderComponent(textData, textType)
    }

    override fun onDestroy() {
        super.onDestroy()

        lifecycle.removeObserver(fragmentViewModel)
        lifecycle.removeObserver(infobarViewModel)
        lifecycle.removeObserver(routePreviewControlsViewModel)
    }

    override fun resolveAttributes(attributes: AttributeSet) {
        with(requireContext().obtainStyledAttributes(attributes, R.styleable.NavigationFragment)) {
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

            recycle()
        }
    }
}
