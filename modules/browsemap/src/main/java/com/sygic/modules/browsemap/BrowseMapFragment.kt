package com.sygic.modules.browsemap

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sygic.modules.browsemap.databinding.LayoutBrowseMapBinding
import com.sygic.sdk.map.MapFragment
import com.sygic.sdk.map.MapView
import com.sygic.sdk.map.listeners.OnMapInitListener
import androidx.lifecycle.ViewModelProviders
import com.sygic.modules.browsemap.viewmodel.BrowseMapFragmentViewModel

@Suppress("unused", "MemberVisibilityCanBePrivate")
class BrowseMapFragment : MapFragment() {

    private lateinit var viewModel: BrowseMapFragmentViewModel

    var compassEnabled: Boolean
        get() {
            return viewModel.compassEnabled.value!!
        }
        set(value) {
            viewModel.compassEnabled.value = value
        }

    var positionLockFabEnabled: Boolean
        get() {
            return viewModel.positionLockFabEnabled.value!!
        }
        set(value) {
            viewModel.positionLockFabEnabled.value = value
        }

    var zoomControlsEnabled: Boolean
        get() {
            return viewModel.zoomControlsEnabled.value!!
        }
        set(value) {
            viewModel.zoomControlsEnabled.value = value
        }

    override fun onInflate(context: Context, attrs: AttributeSet?, savedInstanceState: Bundle?) {
        super.onInflate(context, attrs, savedInstanceState)
        //todo: MS-4507
        viewModel = ViewModelProviders.of(this, BrowseMapFragmentViewModel.ViewModelFactory(requireActivity().application, attrs))
                .get(BrowseMapFragmentViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding: LayoutBrowseMapBinding = LayoutBrowseMapBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        (binding.root as ViewGroup).addView(super.onCreateView(inflater, container, savedInstanceState), 0)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //todo: MS-4508
        getMapAsync(object : OnMapInitListener {
            override fun onMapReady(mapView: MapView) {
                Log.d("BrowseMapFragment", "onMapReady()")
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
}