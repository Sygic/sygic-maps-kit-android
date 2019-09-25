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

package com.sygic.maps.module.common

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.AttributeSet
import androidx.annotation.CallSuper
import androidx.annotation.RestrictTo
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import com.sygic.maps.module.common.di.BaseFragmentComponent
import com.sygic.maps.module.common.di.util.ModuleBuilder
import com.sygic.maps.module.common.extensions.createGoogleApiLocationRequest
import com.sygic.maps.module.common.extensions.executeInjector
import com.sygic.maps.module.common.extensions.isGooglePlayServicesAvailable
import com.sygic.maps.module.common.extensions.showGenericNoGpsDialog
import com.sygic.maps.module.common.mapinteraction.manager.MapInteractionManager
import com.sygic.maps.module.common.theme.ThemeManager
import com.sygic.maps.module.common.utils.BackingCameraDataModel
import com.sygic.maps.module.common.utils.BackingMapDataModel
import com.sygic.maps.module.common.viewmodel.ThemeSupportedViewModel
import com.sygic.maps.tools.viewmodel.factory.ViewModelFactory
import com.sygic.maps.uikit.viewmodels.common.extensions.addMapMarker
import com.sygic.maps.uikit.viewmodels.common.extensions.removeAllMapMarkers
import com.sygic.maps.uikit.viewmodels.common.extensions.removeMapMarker
import com.sygic.maps.uikit.viewmodels.common.initialization.SdkInitializationManager
import com.sygic.maps.uikit.viewmodels.common.location.GOOGLE_API_CLIENT_REQUEST_CODE
import com.sygic.maps.uikit.viewmodels.common.location.LocationManager
import com.sygic.maps.uikit.viewmodels.common.location.SETTING_ACTIVITY_REQUEST_CODE
import com.sygic.maps.uikit.viewmodels.common.permission.PERMISSIONS_REQUEST_CODE
import com.sygic.maps.uikit.viewmodels.common.permission.PermissionsManager
import com.sygic.maps.uikit.viewmodels.common.sdk.model.ExtendedCameraModel
import com.sygic.maps.uikit.viewmodels.common.sdk.model.ExtendedMapDataModel
import com.sygic.maps.uikit.viewmodels.common.sdk.skin.MapSkin
import com.sygic.maps.uikit.viewmodels.common.sdk.skin.VehicleSkin
import com.sygic.maps.uikit.viewmodels.common.sdk.skin.isMapSkinValid
import com.sygic.maps.uikit.viewmodels.common.sdk.skin.isVehicleSkinValid
import com.sygic.maps.uikit.views.common.extensions.EMPTY_STRING
import com.sygic.maps.uikit.views.common.extensions.getString
import com.sygic.maps.uikit.views.common.extensions.getStringFromAttr
import com.sygic.maps.uikit.views.common.utils.logWarning
import com.sygic.sdk.map.MapFragment
import com.sygic.sdk.map.MapView
import com.sygic.sdk.map.`object`.MapMarker
import com.sygic.sdk.map.listeners.OnMapInitListener
import com.sygic.sdk.online.OnlineManager
import javax.inject.Inject

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
@Suppress("unused", "MemberVisibilityCanBePrivate")
abstract class MapFragmentWrapper<F : Fragment, C : BaseFragmentComponent<F>, B : ModuleBuilder<C>, T : ThemeSupportedViewModel>
    : MapFragment(), SdkInitializationManager.Callback, OnMapInitListener {

    protected abstract fun getModuleBuilder(): B
    protected abstract fun resolveAttributes(context: Context, attributes: AttributeSet)

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    internal lateinit var mapInteractionManager: MapInteractionManager
    @Inject
    internal lateinit var mapDataModel: ExtendedMapDataModel
    @Inject
    internal lateinit var cameraDataModel: ExtendedCameraModel
    @Inject
    internal lateinit var sdkInitializationManager: SdkInitializationManager
    @Inject
    internal lateinit var permissionManager: PermissionsManager
    @Inject
    internal lateinit var locationManager: LocationManager

    protected abstract var fragmentViewModel: T

    private var locationRequesterCallback: LocationManager.LocationRequesterCallback? = null
    private var permissionsRequesterCallback: PermissionsManager.PermissionsRequesterCallback? = null

    private val backingMapDataModel = lazy { BackingMapDataModel() }
    private val backingCameraModel = lazy { BackingCameraDataModel() }

    protected inline fun <reified T : ViewModel> viewModelOf(
        viewModelClass: Class<out T>,
        vararg assistedParams: Any? = emptyArray()
    ) = ViewModelProviders.of(this, viewModelFactory.with(*assistedParams))[viewModelClass]

    init {
        if (arguments == null) {
            arguments = Bundle.EMPTY
        }
        getMapAsync(this)
    }

    final override fun getMapDataModel() = if (::mapDataModel.isInitialized) mapDataModel else backingMapDataModel.value
    final override fun getCameraDataModel() = if(::cameraDataModel.isInitialized) cameraDataModel else backingCameraModel.value

    override fun onInflate(context: Context, attrs: AttributeSet, savedInstanceState: Bundle?) {
        super.onInflate(context, attrs, savedInstanceState)
        resolveAttributesInternal(context, attrs)
    }

    @CallSuper
    @Suppress("UNCHECKED_CAST")
    override fun onAttach(context: Context) {
        (this as F).executeInjector(getModuleBuilder())
        super.onAttach(context)

        if (backingMapDataModel.isInitialized()) {
            backingMapDataModel.value.dumpToModel(mapDataModel)
        }
        if (backingCameraModel.isInitialized()) {
            backingCameraModel.value.dumpToModel(cameraDataModel)
        }

        sdkInitializationManager.initialize(this)
        permissionManager.observe(this, Observer {
            permissionsRequesterCallback = it.callback
            requestPermissions(it.permissions, PERMISSIONS_REQUEST_CODE)
        })
        locationManager.observe(this, Observer {
            locationRequesterCallback = it
            if (isGooglePlayServicesAvailable()) {
                createGoogleApiLocationRequest(GOOGLE_API_CLIENT_REQUEST_CODE)
            } else {
                showGenericNoGpsDialog(SETTING_ACTIVITY_REQUEST_CODE)
            }
        })

        context.getStringFromAttr(R.attr.sygicMapSkin).let { if (it.isNotEmpty()) setMapSkin(it) }
        context.getStringFromAttr(R.attr.sygicVehicleSkin).let { if (it.isNotEmpty()) setVehicleSkin(it) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycle.addObserver(mapDataModel)
        lifecycle.addObserver(cameraDataModel)
    }

    @CallSuper
    override fun onSdkInitialized() {
        OnlineManager.getInstance().enableOnlineMapStreaming(true)
    }

    @CallSuper
    override fun onMapReady(mapView: MapView) {
        mapInteractionManager.onMapReady(mapView)
    }

    @CallSuper
    override fun onMapInitializationInterrupted() {
        /* Currently do nothing */
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        locationRequesterCallback?.onActivityResult(requestCode, resultCode)
        locationRequesterCallback = null
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        permissionsRequesterCallback?.onRequestPermissionsResult(permissions, grantResults)
        permissionsRequesterCallback = null
    }

    /**
     * Adds a single [MapMarker] to the map. This is useful if you want to add only one object to the map,
     * for example after a click.
     *
     * @param marker [MapMarker] object to be added.
     */
    fun addMapMarker(marker: MapMarker) {
        getMapDataModel().addMapMarker(marker)
    }

    /**
     * Remove a single [MapMarker] from the map. This is useful if you want remove only one specific object from the map,
     * otherwise you can use [removeAllMapMarkers] method.
     *
     * @param marker [MapMarker] object to remove.
     */
    fun removeMapMarker(marker: MapMarker) {
        getMapDataModel().removeMapMarker(marker)
    }

    /**
     * Adds a multiple [MapMarker]-s to the map at once. This is useful if you want to add multiple objects to the map,
     * for example nearby restaurants, company affiliates, etc.
     *
     * @param markers [List] of [MapMarker]-s objects to be added.
     */
    fun addMapMarkers(markers: List<MapMarker>) {
        markers.forEach { addMapMarker(it) }
    }

    /**
     * Remove all [MapMarker]-s from the map at once. This is useful if you want to remove all objects from the map.
     */
    fun removeAllMapMarkers() {
        getMapDataModel().removeAllMapMarkers()
    }

    /**
     * Allows you to change the look of the map. The default value is [MapSkin.DAY].
     *
     * @param mapSkin [MapSkin] to be applied to the map.
     */
    fun setMapSkin(@MapSkin mapSkin: String) {
        if (!isMapSkinValid(mapSkin)) {
            logWarning("MapSkin \"$mapSkin\" is not valid.")
            return
        }

        arguments = Bundle(arguments).apply { putString(ThemeManager.SkinLayer.DayNight.toString(), mapSkin) }
        try {
            fragmentViewModel.setSkinAtLayer(ThemeManager.SkinLayer.DayNight, mapSkin)
        } catch (ignored: UninitializedPropertyAccessException) { }
    }

    /**
     * Allows you to change the vehicle indicator look. The default value is [VehicleSkin.CAR].
     *
     * @param vehicleSkin [VehicleSkin] to be applied to the vehicle indicator.
     */
    fun setVehicleSkin(@VehicleSkin vehicleSkin: String) {
        if (!isVehicleSkinValid(vehicleSkin)) {
            logWarning("VehicleSkin \"$vehicleSkin\" is not valid.")
            return
        }

        arguments = Bundle(arguments).apply { putString(ThemeManager.SkinLayer.Vehicle.toString(), vehicleSkin) }
        try {
            fragmentViewModel.setSkinAtLayer(ThemeManager.SkinLayer.Vehicle, vehicleSkin)
        } catch (ignored: UninitializedPropertyAccessException) { }
    }

    override fun onDestroy() {
        super.onDestroy()

        lifecycle.removeObserver(mapDataModel)
        lifecycle.removeObserver(cameraDataModel)
    }

    private fun resolveAttributesInternal(context: Context, attrs: AttributeSet) {
        with(context.obtainStyledAttributes(attrs, R.styleable.MapFragmentWrapper)) {
            if (hasValue(R.styleable.MapFragmentWrapper_sygic_map_skin)) {
                setMapSkin(getString(R.styleable.MapFragmentWrapper_sygic_map_skin, EMPTY_STRING))
            }
            if (hasValue(R.styleable.MapFragmentWrapper_sygic_vehicle_skin)) {
                setVehicleSkin(getString(R.styleable.MapFragmentWrapper_sygic_vehicle_skin, EMPTY_STRING))
            }

            recycle()
        }

        resolveAttributes(context, attrs)
    }
}