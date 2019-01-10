package com.sygic.modules.browsemap

import android.content.Context
import android.content.res.TypedArray
import android.os.Bundle
import android.util.AttributeSet
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.sygic.modules.browsemap.databinding.LayoutBrowseMapBinding
import com.sygic.modules.browsemap.di.BrowseMapComponent
import com.sygic.modules.browsemap.di.DaggerBrowseMapComponent
import com.sygic.modules.browsemap.viewmodel.BrowseMapFragmentViewModel
import com.sygic.modules.common.MapFragmentWrapper
import com.sygic.modules.common.mapinteraction.MapInteractionMode
import com.sygic.ui.common.sdk.data.PoiData
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

        setHasOptionsMenu(true)

        browseMapFragmentViewModel = viewModelOf(BrowseMapFragmentViewModel::class.java, attributesTypedArray)
        browseMapFragmentViewModel.poiDataObservable.observe(this, Observer<PoiData> { showPoiDetail(it) })
        savedInstanceState?.let { setPoiDetailListener() }

        compassViewModel = viewModelOf(CompassViewModel::class.java)
        positionLockFabViewModel = viewModelOf(PositionLockFabViewModel::class.java)
        zoomControlsViewModel = viewModelOf(ZoomControlsViewModel::class.java)

        lifecycle.addObserver(compassViewModel)
        lifecycle.addObserver(positionLockFabViewModel)
        lifecycle.addObserver(zoomControlsViewModel)
    }

    //tmp code for second module
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_browse_map, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.route_planning -> requireFragmentManager()
                .beginTransaction()
                .replace((view?.parent as View).id, Class.forName("com.sygic.modules.routeplanner.RoutePlannerFragment").newInstance() as Fragment, "rotePlanner")
                .addToBackStack("rotePlanner")
                .commit()
        }

        return super.onOptionsItemSelected(item)
    }
    // -------------------------------

    private fun setPoiDetailListener() {
        fragmentManager?.findFragmentByTag(PoiDetailBottomDialogFragment.TAG)?.let { fragment ->
            (fragment as PoiDetailBottomDialogFragment).setListener(browseMapFragmentViewModel)
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

    private fun showPoiDetail(poiData: PoiData) {
        val dialog = PoiDetailBottomDialogFragment.newInstance(poiData)
        dialog.setListener(browseMapFragmentViewModel)
        dialog.show(fragmentManager, PoiDetailBottomDialogFragment.TAG)
    }

    override fun onDestroy() {
        super.onDestroy()

        lifecycle.removeObserver(compassViewModel)
        lifecycle.removeObserver(positionLockFabViewModel)
        lifecycle.removeObserver(zoomControlsViewModel)
    }
}