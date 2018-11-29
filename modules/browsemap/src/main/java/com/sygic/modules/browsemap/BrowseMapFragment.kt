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
import com.sygic.modules.browsemap.di.BrowseMapComponent
import com.sygic.modules.browsemap.di.DaggerBrowseMapComponent
import com.sygic.modules.browsemap.viewmodel.BrowseMapFragmentViewModel
import com.sygic.modules.common.MapFragmentWrapper
import com.sygic.modules.common.mapinteraction.MapInteractionMode
import com.sygic.ui.viewmodel.compass.CompassViewModel
import com.sygic.ui.viewmodel.poidetail.PoiDetailViewModel
import com.sygic.ui.viewmodel.positionlockfab.PositionLockFabViewModel
import com.sygic.ui.viewmodel.zoomcontrols.ZoomControlsViewModel

@Suppress("unused", "MemberVisibilityCanBePrivate")
class BrowseMapFragment : MapFragmentWrapper() {

    private var attributesTypedArray: TypedArray? = null

    private lateinit var browseMapFragmentViewModel: BrowseMapFragmentViewModel
    private lateinit var compassViewModel: CompassViewModel
    private lateinit var poiDetailViewModel: PoiDetailViewModel
    private lateinit var positionLockFabViewModel: PositionLockFabViewModel
    private lateinit var zoomControlsViewModel: ZoomControlsViewModel

    @MapInteractionMode
    var mapInteractionMode: Int
        get() = browseMapFragmentViewModel.mapInteractionMode.value!!
        set(value) {
            browseMapFragmentViewModel.mapInteractionMode.value = value
        }

    var compassEnabled: Boolean
        get() = browseMapFragmentViewModel.compassEnabled.value!!
        set(value) {
            browseMapFragmentViewModel.compassEnabled.value = value
        }

    var compassHideIfNorthUp: Boolean
        get() = browseMapFragmentViewModel.compassHideIfNorthUp.value!!
        set(value) {
            browseMapFragmentViewModel.compassHideIfNorthUp.value = value
        }

    var positionLockFabEnabled: Boolean
        get() = browseMapFragmentViewModel.positionLockFabEnabled.value!!
        set(value) {
            browseMapFragmentViewModel.positionLockFabEnabled.value = value
        }

    var zoomControlsEnabled: Boolean
        get() = browseMapFragmentViewModel.zoomControlsEnabled.value!!
        set(value) {
            browseMapFragmentViewModel.zoomControlsEnabled.value = value
        }

    override fun onInflate(context: Context, attrs: AttributeSet?, savedInstanceState: Bundle?) {
        super.onInflate(context, attrs, savedInstanceState)

        attributesTypedArray = context.obtainStyledAttributes(attrs, R.styleable.BrowseMapFragment)
    }

    override fun onAttach(context: Context) {
        injector<BrowseMapComponent, BrowseMapComponent.Builder>(DaggerBrowseMapComponent.builder()) { it.inject(this) }
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        browseMapFragmentViewModel = ViewModelProviders.of(
            this,
            viewModelFactory.with(attributesTypedArray)
        )[BrowseMapFragmentViewModel::class.java]

        compassViewModel = viewModelOf(CompassViewModel::class.java)
        poiDetailViewModel = viewModelOf(PoiDetailViewModel::class.java)

        positionLockFabViewModel = ViewModelProviders.of( //todo: use viewModelOf
            this,
            viewModelFactory.with(locationManager, permissionManager)
        )[PositionLockFabViewModel::class.java]

        zoomControlsViewModel = viewModelOf(ZoomControlsViewModel::class.java)

        lifecycle.addObserver(compassViewModel)
        lifecycle.addObserver(poiDetailViewModel)
        lifecycle.addObserver(positionLockFabViewModel)
        lifecycle.addObserver(zoomControlsViewModel)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding: LayoutBrowseMapBinding = LayoutBrowseMapBinding.inflate(inflater, container, false)
        binding.setLifecycleOwner(this)
        binding.browseMapFragmentViewModel = browseMapFragmentViewModel
        binding.compassViewModel = compassViewModel
        binding.poiDetailViewModel = poiDetailViewModel
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