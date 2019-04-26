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

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import android.provider.Settings
import android.util.AttributeSet
import android.util.Log
import androidx.annotation.CallSuper
import androidx.annotation.RestrictTo
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsStatusCodes
import com.sygic.maps.module.common.component.MapFragmentInitComponent
import com.sygic.maps.module.common.delegate.ModulesComponentDelegate
import com.sygic.maps.module.common.di.util.ModuleBuilder
import com.sygic.maps.uikit.viewmodels.common.initialization.SdkInitializationManager
import com.sygic.maps.module.common.mapinteraction.manager.MapInteractionManager
import com.sygic.maps.module.common.poi.manager.PoiDataManager
import com.sygic.maps.module.common.theme.ThemeManager
import com.sygic.maps.module.common.theme.ThemeSupportedViewModel
import com.sygic.maps.tools.viewmodel.factory.ViewModelFactory
import com.sygic.sdk.map.MapFragment
import com.sygic.sdk.map.MapView
import com.sygic.sdk.map.listeners.OnMapInitListener
import com.sygic.sdk.online.OnlineManager
import com.sygic.maps.uikit.views.common.extensions.getStringFromAttr
import com.sygic.maps.uikit.viewmodels.common.location.GOOGLE_API_CLIENT_REQUEST_CODE
import com.sygic.maps.uikit.viewmodels.common.location.LocationManager
import com.sygic.maps.uikit.viewmodels.common.location.SETTING_ACTIVITY_REQUEST_CODE
import com.sygic.maps.uikit.viewmodels.common.sdk.mapobject.MapMarker
import com.sygic.maps.uikit.viewmodels.common.sdk.model.ExtendedCameraModel
import com.sygic.maps.uikit.viewmodels.common.sdk.model.ExtendedMapDataModel
import com.sygic.maps.uikit.viewmodels.common.permission.PERMISSIONS_REQUEST_CODE
import com.sygic.maps.uikit.viewmodels.common.permission.PermissionsManager
import com.sygic.maps.uikit.viewmodels.common.sdk.skin.MapSkin
import com.sygic.maps.uikit.viewmodels.common.sdk.skin.VehicleSkin
import com.sygic.maps.uikit.viewmodels.common.sdk.skin.isMapSkinValid
import javax.inject.Inject

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
@Suppress("unused", "MemberVisibilityCanBePrivate")
abstract class MapFragmentWrapper<T: ThemeSupportedViewModel> : MapFragment(), SdkInitializationManager.Callback, OnMapInitListener {

    protected abstract fun executeInjector()

    protected val mapFragmentInitComponent = MapFragmentInitComponent()
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
        getMapAsync(this)
    }

    override fun getMapDataModel() = ExtendedMapDataModel
    override fun getCameraDataModel() = ExtendedCameraModel

    override fun onInflate(context: Context, attrs: AttributeSet?, savedInstanceState: Bundle?) {
        executeInjector()
        super.onInflate(context, attrs, savedInstanceState)
        mapFragmentInitComponent.attributes = attrs
    }

    override fun onAttach(context: Context) {
        executeInjector()
        super.onAttach(context)

        sdkInitializationManager.initialize((context as Activity).application, this)
        permissionManager.observe(this, Observer {
            permissionsRequesterCallback = it.callback
            requestPermissions(it.permissions, PERMISSIONS_REQUEST_CODE)
        })
        locationManager.observe(this, Observer {
            locationRequesterCallback = it
            if (isGooglePlayServicesAvailable()) {
                createGoogleApiLocationRequest()
            } else {
                showNoGoogleApiDialog()
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

    private fun isGooglePlayServicesAvailable(): Boolean {
        return context?.let {
            GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context) == ConnectionResult.SUCCESS
        } ?: false
    }

    private fun createGoogleApiLocationRequest() {
        activity?.let {
            val locationRequest = LocationRequest.create()
            locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

            val locationSettingsRequestBuilder = LocationSettingsRequest.Builder()
                .setAlwaysShow(true)
                .addLocationRequest(locationRequest)

            val responseTask = LocationServices.getSettingsClient(it)
                .checkLocationSettings(locationSettingsRequestBuilder.build())
            responseTask.addOnCompleteListener(it) { task ->
                try {
                    task.getResult(ApiException::class.java)
                } catch (exception: ApiException) {
                    when (exception.statusCode) {
                        LocationSettingsStatusCodes.RESOLUTION_REQUIRED ->
                            try {
                                startIntentSenderForResult(
                                    (exception as ResolvableApiException).resolution.intentSender,
                                    GOOGLE_API_CLIENT_REQUEST_CODE,
                                    null,
                                    0,
                                    0,
                                    0,
                                    null
                                )
                            } catch (ignored: IntentSender.SendIntentException) {
                                Log.e("RequesterWrapper", "SendIntentException")
                            } catch (ignored: ClassCastException) {
                                Log.e("RequesterWrapper", "ClassCastException")
                            }
                    }
                }
            }
        }
    }

    private fun showNoGoogleApiDialog() {
        context.let {
            AlertDialog.Builder(it)
                .setTitle(R.string.enable_gps_dialog_title)
                .setMessage(R.string.enable_gps_dialog_text)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(
                    R.string.settings
                ) { _, _ ->
                    startActivityForResult(
                        Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS),
                        SETTING_ACTIVITY_REQUEST_CODE
                    )
                }
                .show()
        }
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
        mapDataModel.addMapObject(marker)
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
     * Allows you to change the look of the map. The default value is [MapSkin.DAY].
     *
     * @param mapSkin [MapSkin] to be applied to the map.
     */
    fun setMapSkin(@MapSkin mapSkin: String) {
        try {
            fragmentViewModel.setSkinAtLayer(ThemeManager.SkinLayer.DayNight, mapSkin)
        } catch (e: UninitializedPropertyAccessException) {
            mapFragmentInitComponent.skins[ThemeManager.SkinLayer.DayNight] = mapSkin
        }
    }

    /**
     * Allows you to change the vehicle indicator look. The default value is [VehicleSkin.CAR].
     *
     * @param vehicleSkin [VehicleSkin] to be applied to the vehicle indicator.
     */
    fun setVehicleSkin(@VehicleSkin vehicleSkin: String) {
        try {
            fragmentViewModel.setSkinAtLayer(ThemeManager.SkinLayer.Vehicle, vehicleSkin)
        } catch (e: UninitializedPropertyAccessException) {
            mapFragmentInitComponent.skins[ThemeManager.SkinLayer.Vehicle] = vehicleSkin
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        lifecycle.removeObserver(mapDataModel)
        lifecycle.removeObserver(cameraDataModel)
    }
}