package com.sygic.modules.browsemap.viewmodel

import android.content.res.TypedArray
import android.util.Log
import androidx.lifecycle.*
import com.sygic.modules.browsemap.R
import com.sygic.modules.browsemap.detail.DetailsViewFactory
import com.sygic.modules.browsemap.detail.PoiDataDetailsFactory
import com.sygic.modules.common.mapinteraction.MapSelectionMode
import com.sygic.modules.common.mapinteraction.manager.MapInteractionManager
import com.sygic.modules.common.poi.manager.PoiDataManager
import com.sygic.sdk.map.`object`.UiObject
import com.sygic.sdk.map.`object`.ViewObject
import com.sygic.tools.annotations.Assisted
import com.sygic.tools.annotations.AutoFactory
import com.sygic.ui.common.extensions.asSingleEvent
import com.sygic.ui.common.listeners.DialogFragmentListener
import com.sygic.ui.common.livedata.SingleLiveEvent
import com.sygic.ui.common.sdk.data.PoiData
import com.sygic.ui.common.sdk.listener.OnMapClickListener
import com.sygic.ui.common.sdk.mapobject.MapMarker
import com.sygic.ui.common.sdk.model.ExtendedMapDataModel

@AutoFactory
class BrowseMapFragmentViewModel internal constructor(
    @Assisted attributesTypedArray: TypedArray?,
    private val poiDataManager: PoiDataManager,
    private val extendedMapDataModel: ExtendedMapDataModel,
    private val mapInteractionManager: MapInteractionManager
) : ViewModel(), MapInteractionManager.Listener, DefaultLifecycleObserver {

    @MapSelectionMode
    var mapSelectionMode: Int = MapSelectionMode.MARKERS_ONLY
    val compassEnabled: MutableLiveData<Boolean> = MutableLiveData()
    val compassHideIfNorthUp: MutableLiveData<Boolean> = MutableLiveData()
    val positionLockFabEnabled: MutableLiveData<Boolean> = MutableLiveData()
    val zoomControlsEnabled: MutableLiveData<Boolean> = MutableLiveData()

    val poiDataObservable: LiveData<PoiData> = SingleLiveEvent()

    val dialogFragmentListener: DialogFragmentListener = object : DialogFragmentListener {
        override fun onDismiss() {
            extendedMapDataModel.removeOnClickMapMarker()
        }
    }

    private var onMapClickListener: OnMapClickListener? = null
    private var detailsViewFactory: DetailsViewFactory? = null
    private var poiDetailsView: UiObject? = null

    init {
        attributesTypedArray?.let {
            mapSelectionMode =
                    it.getInt(R.styleable.BrowseMapFragment_sygic_map_selectionMode, MapSelectionMode.MARKERS_ONLY)
            compassEnabled.value = it.getBoolean(R.styleable.BrowseMapFragment_sygic_compass_enabled, false)
            compassHideIfNorthUp.value = it.getBoolean(R.styleable.BrowseMapFragment_sygic_compass_hideIfNorthUp, false)
            positionLockFabEnabled.value =
                    it.getBoolean(R.styleable.BrowseMapFragment_sygic_positionLockFab_enabled, false)
            zoomControlsEnabled.value = it.getBoolean(R.styleable.BrowseMapFragment_sygic_zoomControls_enabled, false)
            it.recycle()
        }

        mapInteractionManager.addOnMapClickListener(this)
    }

    override fun onMapObjectsRequestStarted() {
        extendedMapDataModel.removeOnClickMapMarker()
    }

    override fun onMapObjectsReceived(viewObjects: List<ViewObject>) {
        poiDetailsView?.let { extendedMapDataModel.removeMapObject(it) }
        poiDetailsView = null

        when (mapSelectionMode) {
            MapSelectionMode.NONE -> {
                logWarning("NONE")
            }
            MapSelectionMode.MARKERS_ONLY -> {
                val firstViewObject = viewObjects.first()
                if (firstViewObject !is MapMarker) {
                    return
                }

                getPoiDataAndNotifyObservers(firstViewObject)
            }
            MapSelectionMode.FULL -> {
                val firstViewObject = viewObjects.first()
                if (firstViewObject !is MapMarker && onMapClickListener == null) {
                    extendedMapDataModel.addOnClickMapMarker(MapMarker(firstViewObject))
                }

                getPoiDataAndNotifyObservers(firstViewObject)
            }
        }
    }

    private fun logWarning(mode: String) {
        onMapClickListener?.let { Log.w("OnMapClickListener", "The listener is set, but map selection mode is $mode.") }
    }

    private fun getPoiDataAndNotifyObservers(viewObject: ViewObject) {
        poiDataManager.getPoiData(viewObject, object : PoiDataManager.Callback() {
            override fun onDataLoaded(poiData: PoiData) {
                onMapClickListener?.let {
                    it.onMapClick(poiData)
                    return
                }

                val factory = detailsViewFactory
                if (factory != null) {
                    poiDetailsView = object : UiObject(poiData.coordinates, PoiDataDetailsFactory(factory, poiData)) {
                        override fun onMeasured(width: Int, height: Int) {
                            super.onMeasured(width, height)

                            val markerHeight: Int = if (viewObject is MapMarker)
                                viewObject.getBitmap(null)?.height ?: 0 else 0

                            setAnchor(0.5f - (factory.getXOffset() / width), 1f + ((markerHeight + factory.getYOffset()) / height))
                        }
                    }.also {
                        extendedMapDataModel.addMapObject(it)
                    }
                } else {
                    poiDataObservable.asSingleEvent().value = poiData
                }
            }
        })
    }

    fun setOnMapClickListener(onMapClickListener: OnMapClickListener?) {
        this.onMapClickListener = onMapClickListener
    }

    fun setDetailsViewFactory(factory: DetailsViewFactory?) {
        this.detailsViewFactory = factory
    }

    override fun onDestroy(owner: LifecycleOwner) {
        onMapClickListener = null
    }

    override fun onCleared() {
        super.onCleared()
        mapInteractionManager.removeOnMapClickListener(this)
    }
}