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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import com.sygic.maps.module.common.delegate.ModulesComponentDelegate
import com.sygic.maps.module.common.di.util.ModuleBuilder
import com.sygic.maps.module.common.extensions.createGoogleApiLocationRequest
import com.sygic.maps.module.common.extensions.isGooglePlayServicesAvailable
import com.sygic.maps.module.common.extensions.showGenericNoGpsDialog
import com.sygic.maps.module.common.mapinteraction.manager.MapInteractionManager
import com.sygic.maps.module.common.poi.manager.PoiDataManager
import com.sygic.maps.module.common.theme.ThemeManager
import com.sygic.maps.module.common.viewmodel.ThemeSupportedViewModel
import com.sygic.maps.tools.viewmodel.factory.ViewModelFactory
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
import com.sygic.maps.uikit.views.common.extensions.getStringFromAttr
import com.sygic.sdk.map.MapFragment
import com.sygic.sdk.map.MapView
import com.sygic.sdk.map.`object`.MapMarker
import com.sygic.sdk.map.listeners.OnMapInitListener
import com.sygic.sdk.online.OnlineManager
import javax.inject.Inject

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
@Suppress("unused", "MemberVisibilityCanBePrivate")
abstract class MapFragmentWrapper<T: ThemeSupportedViewModel> : MapFragment(), SdkInitializationManager.Callback, OnMapInitListener {

    protected abstract fun executeInjector()
    protected abstract fun resolveAttributes(attributes: AttributeSet)

    protected val modulesComponent = ModulesComponentDelegate()

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    internal lateinit var poiDataManager: PoiDataManager
    @Inject
    internal lateinit var mapInteractionManager: MapInteractionManager
    @Inject
    internal lateinit var sdkInitializationManager: SdkInitializationManager
    @Inject
    internal lateinit var permissionManager: PermissionsManager
    @Inject
    internal lateinit var locationManager: LocationManager

    protected abstract var fragmentViewModel: T

    private var locationRequesterCallback: LocationManager.LocationRequesterCallback? = null
    private var permissionsRequesterCallback: PermissionsManager.PermissionsRequesterCallback? = null

    protected var injected = false

    protected inline fun <reified T, B : ModuleBuilder<T>> injector(builder: B, block: (T) -> Unit) {
        if (!injected) {
            block(
                builder
                    .plus(modulesComponent.getInstance(this))
                    .build()
            )
        }
        injected = true
    }

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

    final override fun getMapDataModel() = ExtendedMapDataModel
    final override fun getCameraDataModel() = ExtendedCameraModel

    override fun onInflate(context: Context, attrs: AttributeSet, savedInstanceState: Bundle?) {
        executeInjector()
        super.onInflate(context, attrs, savedInstanceState)
        resolveAttributes(attrs)
    }

    override fun onAttach(context: Context) {
        executeInjector()
        super.onAttach(context)

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

        context.getStringFromAttr(R.attr.sygicMapSkin).let { if (isMapSkinValid(it)) setMapSkin(it) }
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
        mapDataModel.addMapMarker(marker)
    }

    /**
     * Remove a single [MapMarker] from the map. This is useful if you want remove only one specific object from the map,
     * otherwise you can use [removeAllMapMarkers] method.
     *
     * @param marker [MapMarker] object to remove.
     */
    fun removeMapMarker(marker: MapMarker) {
        mapDataModel.removeMapMarker(marker)
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
        mapDataModel.removeAllMapMarkers()
    }

    /**
     * Allows you to change the look of the map. The default value is [MapSkin.DAY].
     *
     * @param mapSkin [MapSkin] to be applied to the map.
     */
    fun setMapSkin(@MapSkin mapSkin: String) {
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
}