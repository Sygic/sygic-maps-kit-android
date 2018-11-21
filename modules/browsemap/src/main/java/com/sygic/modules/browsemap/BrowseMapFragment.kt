package com.sygic.modules.browsemap

import android.content.Context
import android.content.res.TypedArray
import android.os.Bundle
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import com.sygic.modules.browsemap.databinding.LayoutBrowseMapBinding
import com.sygic.modules.browsemap.viewmodel.BrowseMapFragmentViewModel
import com.sygic.modules.common.MapFragmentWrapper
import com.sygic.ui.viewmodel.compass.CompassViewModel
import com.sygic.ui.viewmodel.placedetail.PlaceDetailViewModel
import com.sygic.ui.viewmodel.positionlockfab.PositionLockFabViewModel
import com.sygic.ui.viewmodel.zoomcontrols.ZoomControlsViewModel

@Suppress("unused", "MemberVisibilityCanBePrivate")
class BrowseMapFragment : MapFragmentWrapper() {

    private var attributesTypedArray: TypedArray? = null

    private lateinit var browseMapFragmentViewModel: BrowseMapFragmentViewModel
    private lateinit var compassViewModel: CompassViewModel
    private lateinit var positionLockFabViewModel: PositionLockFabViewModel
    private lateinit var zoomControlsViewModel: ZoomControlsViewModel

    var compassEnabled: Boolean
        get() { return browseMapFragmentViewModel.compassEnabled.value!! }
        set(value) { browseMapFragmentViewModel.compassEnabled.value = value }

    var compassHideIfNorthUp: Boolean
        get() { return browseMapFragmentViewModel.compassHideIfNorthUp.value!! }
        set(value) { browseMapFragmentViewModel.compassHideIfNorthUp.value = value }

    var mapClickEnabled: Boolean
        get() { return browseMapFragmentViewModel.mapClickEnabled.value!! }
        set(value) { browseMapFragmentViewModel.mapClickEnabled.value = value }

    var positionLockFabEnabled: Boolean
        get() { return browseMapFragmentViewModel.positionLockFabEnabled.value!! }
        set(value) { browseMapFragmentViewModel.positionLockFabEnabled.value = value }

    var zoomControlsEnabled: Boolean
        get() { return browseMapFragmentViewModel.zoomControlsEnabled.value!! }
        set(value) { browseMapFragmentViewModel.zoomControlsEnabled.value = value }

    override fun onInflate(context: Context, attrs: AttributeSet?, savedInstanceState: Bundle?) {
        super.onInflate(context, attrs, savedInstanceState)

        attributesTypedArray = context.obtainStyledAttributes(attrs, R.styleable.BrowseMapFragment)
    }

    override fun onCreate(savedInstanceState: Bundle?) { //todo: all VMs not working after rotation
        super.onCreate(savedInstanceState)

        browseMapFragmentViewModel = ViewModelProviders.of(this,
            BrowseMapFragmentViewModel.ViewModelFactory(attributesTypedArray, mapDataModel, mapInteractionManager)
        ).get(BrowseMapFragmentViewModel::class.java)

        compassViewModel = ViewModelProviders.of(this, CompassViewModel.ViewModelFactory(cameraDataModel))
            .get(CompassViewModel::class.java)
        lifecycle.addObserver(compassViewModel)

        positionLockFabViewModel = ViewModelProviders.of(this,
            PositionLockFabViewModel.ViewModelFactory(cameraDataModel, locationManager, permissionManager))
            .get(PositionLockFabViewModel::class.java)
        lifecycle.addObserver(positionLockFabViewModel)

        zoomControlsViewModel = ViewModelProviders.of(this,
            ZoomControlsViewModel.ViewModelFactory(cameraDataModel)).get(ZoomControlsViewModel::class.java)
        lifecycle.addObserver(zoomControlsViewModel)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding: LayoutBrowseMapBinding = LayoutBrowseMapBinding.inflate(inflater, container, false)
        binding.setLifecycleOwner(this)
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

    override fun onDestroy() {
        super.onDestroy()

        lifecycle.removeObserver(compassViewModel)
        lifecycle.removeObserver(positionLockFabViewModel)
        lifecycle.removeObserver(zoomControlsViewModel)
    }
}