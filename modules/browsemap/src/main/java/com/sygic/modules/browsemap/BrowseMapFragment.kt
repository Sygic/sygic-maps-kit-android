package com.sygic.modules.browsemap

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sygic.modules.browsemap.databinding.LayoutBrowseMapBinding
import com.sygic.sdk.map.listeners.OnMapInitListener
import androidx.lifecycle.ViewModelProviders
import com.sygic.modules.browsemap.viewmodel.BrowseMapFragmentViewModel
import com.sygic.sdk.map.*
import com.sygic.ui.viewmodel.compass.CompassViewModel

@Suppress("unused", "MemberVisibilityCanBePrivate")
class BrowseMapFragment : MapFragment() {

    private lateinit var browseMapFragmentViewModel: BrowseMapFragmentViewModel
    private lateinit var compassViewModel: CompassViewModel

    var compassEnabled: Boolean
        get() { return browseMapFragmentViewModel.compassEnabled.value!! }
        set(value) { browseMapFragmentViewModel.compassEnabled.value = value }

    var compassHideIfNorthUp: Boolean
        get() { return browseMapFragmentViewModel.compassHideIfNorthUp.value!! }
        set(value) { browseMapFragmentViewModel.compassHideIfNorthUp.value = value }

    var positionLockFabEnabled: Boolean
        get() { return browseMapFragmentViewModel.positionLockFabEnabled.value!! }
        set(value) { browseMapFragmentViewModel.positionLockFabEnabled.value = value }

    var zoomControlsEnabled: Boolean
        get() { return browseMapFragmentViewModel.zoomControlsEnabled.value!! }
        set(value) { browseMapFragmentViewModel.zoomControlsEnabled.value = value }

    override fun onInflate(context: Context, attrs: AttributeSet?, savedInstanceState: Bundle?) {
        super.onInflate(context, attrs, savedInstanceState)
        //todo: MS-4507
        val application = requireActivity().application
        browseMapFragmentViewModel = ViewModelProviders.of(
            this, BrowseMapFragmentViewModel.ViewModelFactory(application, attrs)
        ).get(BrowseMapFragmentViewModel::class.java)

        compassViewModel = ViewModelProviders.of(this,
            CompassViewModel.ViewModelFactory(cameraDataModel)).get(CompassViewModel::class.java)
        lifecycle.addObserver(compassViewModel)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding: LayoutBrowseMapBinding = LayoutBrowseMapBinding.inflate(inflater, container, false)
        binding.setLifecycleOwner(this)
        binding.browseMapFragmentViewModel = browseMapFragmentViewModel
        binding.compassViewModel = compassViewModel
        (binding.root as ViewGroup).addView(super.onCreateView(inflater, container, savedInstanceState), 0)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //todo: MS-4508
        getMapAsync(object : OnMapInitListener {
            override fun onMapReady(mapView: MapView) {
                /*if (cameraInitialLatitude != -1f && cameraInitialLongitude != -1f) {
                    mapView.cameraModel.position =
                            GeoCoordinates(cameraInitialLatitude.toDouble(), cameraInitialLongitude.toDouble())
                }
                if (cameraInitialLatitude != -1f) {
                    mapView.cameraModel.zoomLevel = cameraInitialZoom
                }*/
            }

            override fun onMapInitializationInterrupted() {}
        })
    }

    override fun onDestroy() {
        super.onDestroy()

        lifecycle.removeObserver(compassViewModel)
    }
}