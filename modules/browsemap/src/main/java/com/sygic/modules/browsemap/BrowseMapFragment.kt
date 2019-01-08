package com.sygic.modules.browsemap

import android.content.Context
import android.content.res.TypedArray
import android.os.Bundle
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.sygic.modules.browsemap.databinding.LayoutBrowseMapBinding
import com.sygic.modules.browsemap.di.BrowseMapComponent
import com.sygic.modules.browsemap.di.DaggerBrowseMapComponent
import com.sygic.modules.browsemap.viewmodel.BrowseMapFragmentViewModel
import com.sygic.modules.common.MapFragmentWrapper
import com.sygic.modules.common.mapinteraction.MapSelectionMode
import com.sygic.ui.common.sdk.data.PoiData
import com.sygic.ui.common.sdk.listener.OnMapClickListener
import com.sygic.ui.view.poidetail.PoiDetailBottomDialogFragment
import com.sygic.ui.viewmodel.compass.CompassViewModel
import com.sygic.ui.viewmodel.positionlockfab.PositionLockFabViewModel
import com.sygic.ui.viewmodel.zoomcontrols.ZoomControlsViewModel

@Suppress("unused", "MemberVisibilityCanBePrivate")
class BrowseMapFragment : MapFragmentWrapper() {

    private var attributesTypedArray: TypedArray? = null

    private lateinit var browseMapFragmentViewModel: BrowseMapFragmentViewModel
    private lateinit var compassViewModel: CompassViewModel
    private lateinit var positionLockFabViewModel: PositionLockFabViewModel
    private lateinit var zoomControlsViewModel: ZoomControlsViewModel

    @MapSelectionMode
    var mapSelectionMode: Int
        get() = browseMapFragmentViewModel.mapSelectionMode
        set(value) {
            browseMapFragmentViewModel.mapSelectionMode = value
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
        injector<BrowseMapComponent, BrowseMapComponent.Builder>(DaggerBrowseMapComponent.builder()) { it.inject(this) }
        super.onInflate(context, attrs, savedInstanceState)

        attributesTypedArray = context.obtainStyledAttributes(attrs, R.styleable.BrowseMapFragment)
    }

    override fun onAttach(context: Context) {
        injector<BrowseMapComponent, BrowseMapComponent.Builder>(DaggerBrowseMapComponent.builder()) { it.inject(this) }
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        browseMapFragmentViewModel = viewModelOf(BrowseMapFragmentViewModel::class.java, attributesTypedArray)
        browseMapFragmentViewModel.poiDataObservable.observe(this, Observer<PoiData> { showPoiDetail(it) })
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

    fun addOnMapClickListener(onMapClickListener: (poiData: PoiData) -> Unit) {
        addOnMapClickListener(object : OnMapClickListener {
            override fun onMapClick(poiData: PoiData) = onMapClickListener(poiData)
        })
    }

    fun addOnMapClickListener(onMapClickListener: OnMapClickListener) {
        browseMapFragmentViewModel.addOnMapClickListener(onMapClickListener)
    }

    fun removeOnMapMarkerClickListener(onMapClickListener: OnMapClickListener) {
        browseMapFragmentViewModel.removeOnMapClickListener(onMapClickListener)
    }

    private fun showPoiDetail(poiData: PoiData) {
        val dialog = PoiDetailBottomDialogFragment.newInstance(poiData)
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