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

package com.sygic.maps.module.browsemap

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.sygic.maps.module.browsemap.databinding.LayoutBrowseMapBinding
import com.sygic.maps.module.browsemap.di.BrowseMapComponent
import com.sygic.maps.module.browsemap.di.DaggerBrowseMapComponent
import com.sygic.maps.module.browsemap.viewmodel.BrowseMapFragmentViewModel
import com.sygic.maps.module.common.MapFragmentWrapper
import com.sygic.maps.module.common.detail.DetailsViewFactory
import com.sygic.maps.module.common.mapinteraction.MapSelectionMode
import com.sygic.maps.module.common.listener.OnMapClickListener
import com.sygic.maps.uikit.views.compass.CompassView
import com.sygic.maps.uikit.views.poidetail.PoiDetailBottomDialogFragment
import com.sygic.maps.uikit.views.positionlockfab.PositionLockFab
import com.sygic.maps.uikit.views.zoomcontrols.ZoomControlsMenu
import com.sygic.maps.uikit.viewmodels.compass.CompassViewModel
import com.sygic.maps.uikit.viewmodels.positionlockfab.PositionLockFabViewModel
import com.sygic.maps.uikit.viewmodels.zoomcontrols.ZoomControlsViewModel
import com.sygic.maps.uikit.views.poidetail.data.PoiDetailData
import com.sygic.maps.uikit.views.poidetail.listener.DialogFragmentListener
import com.sygic.sdk.map.`object`.MapMarker

/**
 * A *[BrowseMapFragment]* is the most basic component from our portfolio. It can be easily used to display view objects
 * on the map and interact with them. It also offers traffic information or can be simply used to display the actual
 * location. It comes with several pre build-in elements such as [CompassView], [ZoomControlsMenu], [PositionLockFab] or
 * [PoiDetailBottomDialogFragment].
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
open class BrowseMapFragment : MapFragmentWrapper<BrowseMapFragmentViewModel>() {

    override lateinit var fragmentViewModel: BrowseMapFragmentViewModel
    private lateinit var compassViewModel: CompassViewModel
    private lateinit var positionLockFabViewModel: PositionLockFabViewModel
    private lateinit var zoomControlsViewModel: ZoomControlsViewModel

    override fun executeInjector() =
        injector<BrowseMapComponent, BrowseMapComponent.Builder>(DaggerBrowseMapComponent.builder()) { it.inject(this) }

    /**
     * A *[MapSelectionMode]* defines the three available [BrowseMapFragment] selection modes.
     *
     * [MapSelectionMode.FULL] -> Enable an unlimited selection mode on the map, every click will be processed.
     *
     * [MapSelectionMode.MARKERS_ONLY] (default) -> Limit selection mode to the custom [MapMarker]s only, see [addMapMarker] or [addMapMarkers] methods.
     *
     * [MapSelectionMode.NONE] -> Disable any selection on the map.
     */
    @MapSelectionMode
    var mapSelectionMode: Int
        get() = if (::fragmentViewModel.isInitialized) {
            fragmentViewModel.mapSelectionMode
        } else mapFragmentInitComponent.mapSelectionMode
        set(value) {
            if (::fragmentViewModel.isInitialized) {
                fragmentViewModel.mapSelectionMode = value
            } else mapFragmentInitComponent.mapSelectionMode = value
        }

    /**
     * A *[compassEnabled]* modifies the [CompassView] visibility.
     *
     * @param [Boolean] true to enable the [CompassView], false otherwise.
     *
     * @return whether the [CompassView] is on or off.
     */
    var compassEnabled: Boolean
        get() = if (::fragmentViewModel.isInitialized) {
            fragmentViewModel.compassEnabled.value!!
        } else mapFragmentInitComponent.compassEnabled
        set(value) {
            if (::fragmentViewModel.isInitialized) {
                fragmentViewModel.compassEnabled.value = value
            } else mapFragmentInitComponent.compassEnabled = value
        }

    /**
     * A *[compassHideIfNorthUp]* modifies the [CompassView] auto hide behaviour.
     *
     * @param [Boolean] true to hide the [CompassView] automatically when it points northwards, false otherwise.
     *
     * @return whether the [CompassView] auto hide behaviour is on or off.
     */
    var compassHideIfNorthUp: Boolean
        get() = if (::fragmentViewModel.isInitialized) {
            fragmentViewModel.compassHideIfNorthUp.value!!
        } else mapFragmentInitComponent.compassHideIfNorthUp
        set(value) {
            if (::fragmentViewModel.isInitialized) {
                fragmentViewModel.compassHideIfNorthUp.value = value
            } else mapFragmentInitComponent.compassHideIfNorthUp = value
        }

    /**
     * A *[positionOnMapEnabled]* modifies the "my position" indicator visibility.
     *
     * @param [Boolean] true to enable the "my position" indicator, false otherwise.
     *
     * @return whether the "my position" indicator is on or off.
     */
    var positionOnMapEnabled: Boolean
        get() = if (::fragmentViewModel.isInitialized) {
            fragmentViewModel.positionOnMapEnabled
        } else mapFragmentInitComponent.positionOnMapEnabled
        set(value) {
            if (::fragmentViewModel.isInitialized) {
                fragmentViewModel.positionOnMapEnabled = value
            } else mapFragmentInitComponent.positionOnMapEnabled = value
        }

    /**
     * A *[positionLockFabEnabled]* modifies the [PositionLockFab] visibility.
     *
     * @param [Boolean] true to enable the [PositionLockFab], false otherwise.
     *
     * @return whether the [PositionLockFab] is on or off.
     */
    var positionLockFabEnabled: Boolean
        get() = if (::fragmentViewModel.isInitialized) {
            fragmentViewModel.positionLockFabEnabled.value!!
        } else mapFragmentInitComponent.positionLockFabEnabled
        set(value) {
            if (::fragmentViewModel.isInitialized) {
                fragmentViewModel.positionLockFabEnabled.value = value
            } else mapFragmentInitComponent.positionLockFabEnabled = value
        }

    /**
     * A *[zoomControlsEnabled]* modifies the [ZoomControlsMenu] visibility.
     *
     * @param [Boolean] true to enable the [ZoomControlsMenu], false otherwise.
     *
     * @return whether the [ZoomControlsMenu] is on or off.
     */
    var zoomControlsEnabled: Boolean
        get() = if (::fragmentViewModel.isInitialized) {
            fragmentViewModel.zoomControlsEnabled.value!!
        } else mapFragmentInitComponent.zoomControlsEnabled
        set(value) {
            if (::fragmentViewModel.isInitialized) {
                fragmentViewModel.zoomControlsEnabled.value = value
            } else mapFragmentInitComponent.zoomControlsEnabled = value
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fragmentViewModel = viewModelOf(BrowseMapFragmentViewModel::class.java, mapFragmentInitComponent).apply {
            this.poiDetailObservable.observe(
                this@BrowseMapFragment,
                Observer<Any> { showPoiDetail() })
            this.poiDetailDataObservable.observe(
                this@BrowseMapFragment,
                Observer<PoiDetailData> { setPoiDetailData(it) })
            this.poiDetailListenerObservable.observe(
                this@BrowseMapFragment,
                Observer<DialogFragmentListener> { setPoiDetailListener(it) })
        }

        compassViewModel = viewModelOf(CompassViewModel::class.java)
        positionLockFabViewModel = viewModelOf(PositionLockFabViewModel::class.java)
        zoomControlsViewModel = viewModelOf(ZoomControlsViewModel::class.java)

        lifecycle.addObserver(fragmentViewModel)
        lifecycle.addObserver(compassViewModel)
        lifecycle.addObserver(positionLockFabViewModel)
        lifecycle.addObserver(zoomControlsViewModel)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding: LayoutBrowseMapBinding = LayoutBrowseMapBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.browseMapFragmentViewModel = fragmentViewModel
        binding.compassViewModel = compassViewModel
        binding.positionLockFabViewModel = positionLockFabViewModel
        binding.zoomControlsViewModel = zoomControlsViewModel
        val root = binding.root as ViewGroup
        super.onCreateView(inflater, root, savedInstanceState)?.let {
            root.addView(it, 0)
        }
        return root
    }

    /**
     * Register a custom callback to be invoked when a click to the map has been made. If null, default callback
     * is executed showing details about the selected point.
     *
     * @param onMapClickListener [OnMapClickListener] callback to invoke on map click.
     */
    fun setOnMapClickListener(onMapClickListener: OnMapClickListener?) {
        if (::fragmentViewModel.isInitialized) {
            fragmentViewModel.onMapClickListener = onMapClickListener
        } else {
            mapFragmentInitComponent.onMapClickListener = onMapClickListener
        }
    }

    /**
     * Set a factory for details window generation. If non-null this factory will be used
     * to show info about selected point instead of default implementation.
     *
     * @param factory [DetailsViewFactory] used to generate details window.
     */
    fun setDetailsViewFactory(factory: DetailsViewFactory?) {
        if (::fragmentViewModel.isInitialized) {
            fragmentViewModel.detailsViewFactory = factory
        } else {
            mapFragmentInitComponent.detailsViewFactory = factory
        }
    }

    private fun showPoiDetail() {
        PoiDetailBottomDialogFragment.newInstance().apply {
            setListener(fragmentViewModel.dialogFragmentListener)
            showNow(this@BrowseMapFragment.fragmentManager, PoiDetailBottomDialogFragment.TAG)
        }
    }

    private fun setPoiDetailListener(listener: DialogFragmentListener) {
        fragmentManager?.findFragmentByTag(PoiDetailBottomDialogFragment.TAG)?.let { fragment ->
            (fragment as PoiDetailBottomDialogFragment).setListener(listener)
        }
    }

    private fun setPoiDetailData(data: PoiDetailData) {
        fragmentManager?.findFragmentByTag(PoiDetailBottomDialogFragment.TAG)?.let { fragment ->
            (fragment as PoiDetailBottomDialogFragment).setData(data)
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        lifecycle.removeObserver(fragmentViewModel)
        lifecycle.removeObserver(compassViewModel)
        lifecycle.removeObserver(positionLockFabViewModel)
        lifecycle.removeObserver(zoomControlsViewModel)
    }
}
