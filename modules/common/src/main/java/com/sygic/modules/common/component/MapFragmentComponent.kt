package com.sygic.modules.common.component

import androidx.annotation.RestrictTo
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sygic.modules.common.detail.DetailsViewFactory
import com.sygic.modules.common.mapinteraction.MapSelectionMode
import com.sygic.ui.common.extensions.asSingleEvent
import com.sygic.ui.common.livedata.SingleLiveEvent
import com.sygic.ui.common.sdk.listener.OnMapClickListener
import com.sygic.ui.common.sdk.model.ExtendedCameraModel
import com.sygic.ui.common.sdk.model.ExtendedMapDataModel
import com.sygic.ui.common.sdk.utils.POSITION_ON_MAP_ENABLED_BASE_DEFAULT_VALUE

const val MAP_SELECTION_MODE_DEFAULT_VALUE = MapSelectionMode.MARKERS_ONLY
const val POSITION_ON_MAP_ENABLED_DEFAULT_VALUE = POSITION_ON_MAP_ENABLED_BASE_DEFAULT_VALUE
const val COMPASS_ENABLED_DEFAULT_VALUE = false
const val COMPASS_HIDE_IF_NORTH_UP_DEFAULT_VALUE = false
const val POSITION_LOCK_FAB_ENABLED_DEFAULT_VALUE = false
const val ZOOM_CONTROLS_ENABLED_DEFAULT_VALUE = false

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
object MapFragmentComponent : DefaultLifecycleObserver {

    var mapDataModel = ExtendedMapDataModel
    var cameraDataModel = ExtendedCameraModel

    @MapSelectionMode
    var mapSelectionMode: Int = MapSelectionMode.MARKERS_ONLY

    val positionOnMapEnabledObservable : LiveData<Boolean> = SingleLiveEvent()
    var positionOnMapEnabled: Boolean
        get() = mapDataModel.positionOnMapEnabled
        set(value) {
            mapDataModel.positionOnMapEnabled = value
            positionOnMapEnabledObservable.asSingleEvent().value = value
        }

    val compassEnabled: MutableLiveData<Boolean> = MutableLiveData()
    val compassHideIfNorthUp: MutableLiveData<Boolean> = MutableLiveData()
    val positionLockFabEnabled: MutableLiveData<Boolean> = MutableLiveData()
    val zoomControlsEnabled: MutableLiveData<Boolean> = MutableLiveData()

    var onMapClickListener: OnMapClickListener? = null
    var detailsViewFactory: DetailsViewFactory? = null

    override fun onDestroy(owner: LifecycleOwner) {
        mapDataModel.onDestroy(owner)
        cameraDataModel.onDestroy(owner)

        onMapClickListener = null
        detailsViewFactory = null

        if (owner is Fragment) owner.activity?.run {
            if (isFinishing) {
                mapSelectionMode = MAP_SELECTION_MODE_DEFAULT_VALUE
                positionOnMapEnabled = POSITION_ON_MAP_ENABLED_DEFAULT_VALUE
                compassEnabled.value = COMPASS_ENABLED_DEFAULT_VALUE
                compassHideIfNorthUp.value = COMPASS_HIDE_IF_NORTH_UP_DEFAULT_VALUE
                positionLockFabEnabled.value = POSITION_LOCK_FAB_ENABLED_DEFAULT_VALUE
                zoomControlsEnabled.value = ZOOM_CONTROLS_ENABLED_DEFAULT_VALUE
            }
        }
    }
}