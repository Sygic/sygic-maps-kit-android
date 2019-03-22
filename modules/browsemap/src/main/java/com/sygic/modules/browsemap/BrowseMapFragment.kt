package com.sygic.modules.browsemap

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.sygic.modules.browsemap.databinding.LayoutBrowseMapBinding
import com.sygic.modules.browsemap.di.BrowseMapComponent
import com.sygic.modules.browsemap.di.DaggerBrowseMapComponent
import com.sygic.modules.browsemap.viewmodel.BrowseMapFragmentViewModel
import com.sygic.modules.common.MapFragmentWrapper
import com.sygic.modules.common.detail.DetailsViewFactory
import com.sygic.modules.common.mapinteraction.MapSelectionMode
import com.sygic.sdk.map.`object`.MapMarker
import com.sygic.sdk.map.`object`.data.MarkerData
import com.sygic.ui.common.sdk.listener.OnMapClickListener
import com.sygic.ui.view.compass.CompassView
import com.sygic.ui.view.poidetail.PoiDetailBottomDialogFragment
import com.sygic.ui.view.positionlockfab.PositionLockFab
import com.sygic.ui.view.zoomcontrols.ZoomControlsMenu
import com.sygic.ui.viewmodel.compass.CompassViewModel
import com.sygic.ui.viewmodel.positionlockfab.PositionLockFabViewModel
import com.sygic.ui.viewmodel.zoomcontrols.ZoomControlsViewModel

/**
 * A *[BrowseMapFragment]* is the most basic component from our portfolio. It can be easily used to display view objects
 * on the map and interact with them. It also offers traffic information or can be simply used to display the actual
 * location. It comes with several pre build-in elements such as [CompassView], [ZoomControlsMenu], [PositionLockFab] or
 * [PoiDetailBottomDialogFragment].
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
class BrowseMapFragment : MapFragmentWrapper() {

    private lateinit var browseMapFragmentViewModel: BrowseMapFragmentViewModel
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
        get() = if (::browseMapFragmentViewModel.isInitialized) {
            browseMapFragmentViewModel.mapSelectionMode
        } else mapFragmentInitComponent.mapSelectionMode
        set(value) {
            if (::browseMapFragmentViewModel.isInitialized) {
                browseMapFragmentViewModel.mapSelectionMode = value
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
        get() = if (::browseMapFragmentViewModel.isInitialized) {
            browseMapFragmentViewModel.compassEnabled.value!!
        } else mapFragmentInitComponent.compassEnabled
        set(value) {
            if (::browseMapFragmentViewModel.isInitialized) {
                browseMapFragmentViewModel.compassEnabled.value = value
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
        get() = if (::browseMapFragmentViewModel.isInitialized) {
            browseMapFragmentViewModel.compassHideIfNorthUp.value!!
        } else mapFragmentInitComponent.compassHideIfNorthUp
        set(value) {
            if (::browseMapFragmentViewModel.isInitialized) {
                browseMapFragmentViewModel.compassHideIfNorthUp.value = value
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
        get() = if (::browseMapFragmentViewModel.isInitialized) {
            browseMapFragmentViewModel.positionOnMapEnabled
        } else mapFragmentInitComponent.positionOnMapEnabled
        set(value) {
            if (::browseMapFragmentViewModel.isInitialized) {
                browseMapFragmentViewModel.positionOnMapEnabled = value
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
        get() = if (::browseMapFragmentViewModel.isInitialized) {
            browseMapFragmentViewModel.positionLockFabEnabled.value!!
        } else mapFragmentInitComponent.positionLockFabEnabled
        set(value) {
            if (::browseMapFragmentViewModel.isInitialized) {
                browseMapFragmentViewModel.positionLockFabEnabled.value = value
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
        get() = if (::browseMapFragmentViewModel.isInitialized) {
            browseMapFragmentViewModel.zoomControlsEnabled.value!!
        } else mapFragmentInitComponent.zoomControlsEnabled
        set(value) {
            if (::browseMapFragmentViewModel.isInitialized) {
                browseMapFragmentViewModel.zoomControlsEnabled.value = value
            } else mapFragmentInitComponent.zoomControlsEnabled = value
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        browseMapFragmentViewModel = viewModelOf(BrowseMapFragmentViewModel::class.java, mapFragmentInitComponent)
        browseMapFragmentViewModel.mapDataObservable.observe(this, Observer<MarkerData> { showPoiDetail(it) })
        savedInstanceState?.let { setPoiDetailListener() }

        compassViewModel = viewModelOf(CompassViewModel::class.java)
        positionLockFabViewModel = viewModelOf(PositionLockFabViewModel::class.java)
        zoomControlsViewModel = viewModelOf(ZoomControlsViewModel::class.java)

        lifecycle.addObserver(browseMapFragmentViewModel)
        lifecycle.addObserver(compassViewModel)
        lifecycle.addObserver(positionLockFabViewModel)
        lifecycle.addObserver(zoomControlsViewModel)
    }

    private fun setPoiDetailListener() {
        fragmentManager?.findFragmentByTag(PoiDetailBottomDialogFragment.TAG)?.let { fragment ->
            (fragment as PoiDetailBottomDialogFragment).setListener(browseMapFragmentViewModel.dialogFragmentListener)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding: LayoutBrowseMapBinding = LayoutBrowseMapBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.browseMapFragmentViewModel = browseMapFragmentViewModel
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
     * Register a custom callback to be invoked when a click to the map has been made.
     *
     * @param onMapClickListener [OnMapClickListener] callback to invoke on map click.
     */
    fun setOnMapClickListener(onMapClickListener: (data: MarkerData) -> Boolean) {
        setOnMapClickListener(object : OnMapClickListener {
            override fun onMapClick(data: MarkerData) = onMapClickListener(data)
        })
    }

    /**
     * Register a custom callback to be invoked when a click to the map has been made. If null, default callback
     * is executed showing details about the selected point.
     *
     * @param onMapClickListener [OnMapClickListener] callback to invoke on map click.
     */
    fun setOnMapClickListener(onMapClickListener: OnMapClickListener?) {
        if (::browseMapFragmentViewModel.isInitialized) {
            browseMapFragmentViewModel.onMapClickListener = onMapClickListener
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
        if (::browseMapFragmentViewModel.isInitialized) {
            browseMapFragmentViewModel.detailsViewFactory = factory
        } else {
            mapFragmentInitComponent.detailsViewFactory = factory
        }
    }

    private fun showPoiDetail(data: MarkerData) {
        val dialog = PoiDetailBottomDialogFragment.newInstance(data)
        dialog.setListener(browseMapFragmentViewModel.dialogFragmentListener)
        dialog.show(fragmentManager, PoiDetailBottomDialogFragment.TAG)
    }

    override fun onDestroy() {
        super.onDestroy()

        lifecycle.removeObserver(browseMapFragmentViewModel)
        lifecycle.removeObserver(compassViewModel)
        lifecycle.removeObserver(positionLockFabViewModel)
        lifecycle.removeObserver(zoomControlsViewModel)
    }
}
