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

package com.sygic.samples.demo

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.sygic.maps.module.browsemap.BROWSE_MAP_FRAGMENT_TAG
import com.sygic.maps.module.browsemap.BrowseMapFragment
import com.sygic.maps.module.common.extensions.*
import com.sygic.maps.module.navigation.NAVIGATION_FRAGMENT_TAG
import com.sygic.maps.module.navigation.NavigationFragment
import com.sygic.maps.uikit.viewmodels.common.sdk.skin.VehicleSkin
import com.sygic.maps.uikit.views.common.extensions.isPermissionNotGranted
import com.sygic.maps.uikit.views.common.extensions.longToast
import com.sygic.maps.uikit.views.common.extensions.requestPermission
import com.sygic.samples.R
import com.sygic.samples.app.activities.CommonSampleActivity
import com.sygic.samples.demo.states.BrowseMapDemoDefaultState
import com.sygic.samples.demo.viewmodels.ComplexDemoActivityViewModel
import com.sygic.samples.demo.viewmodels.ComplexDemoViewModelFactory
import com.sygic.samples.utils.getLastValidLocation
import com.sygic.samples.utils.getPrimaryRoute
import com.sygic.samples.utils.hasFragmentWithTag
import com.sygic.sdk.position.GeoCoordinates
import com.sygic.sdk.route.Route
import com.sygic.sdk.route.RoutePlan
import com.sygic.sdk.route.RoutingOptions
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_complex_demo.*
import javax.inject.Inject

private const val REQUEST_CODE_PERMISSION_ACCESS_FINE_LOCATION = 7001
private const val REQUEST_CODE_GOOGLE_API_CLIENT = 7002
private const val REQUEST_CODE_SETTING_ACTIVITY = 7003

class ComplexDemoActivity : CommonSampleActivity() {
    override val wikiModulePath: String? = null

    @Inject
    lateinit var viewModelFactory: ComplexDemoViewModelFactory

    private lateinit var browseMapFragment: BrowseMapFragment
    private lateinit var viewModel: ComplexDemoActivityViewModel

    private val backStackChangedListener = {
        val displayed = supportFragmentManager.hasFragmentWithTag(ROUTING_OPTIONS_FRAGMENT_TAG)
        viewModel.onBackStackChanged(displayed)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_complex_demo)

        viewModel = ViewModelProviders.of(this, viewModelFactory)
            .get(ComplexDemoActivityViewModel::class.java).apply {
                hidePlaceDetailObservable.observe(
                    this@ComplexDemoActivity,
                    Observer { browseMapFragment.hidePlaceDetail() })
                computePrimaryRouteObservable.observe(
                    this@ComplexDemoActivity,
                    Observer { createRoutePlanAndComputeRoute(it.destination, it.options) })
                showRouteOptionsObservable.observe(this@ComplexDemoActivity, Observer {
                    browseMapFragment.hidePlaceDetail()
                    placeRoutingOptionsFragment()
                })
                restoreDefaultStateObservable.observe(
                    this@ComplexDemoActivity,
                    Observer { BrowseMapDemoDefaultState.setTo(browseMapFragment) }
                )
                showPlaceDetailObservable.observe(
                    this@ComplexDemoActivity,
                    Observer {
                        if (!viewModel.routingOptionsDisplayed) {
                            browseMapFragment.showPlaceDetail(it.first, it.second)
                        }
                    }
                )
                routeComputeProgressVisibilityObservable.observe(
                    this@ComplexDemoActivity,
                    Observer { routeComputeProgressBarContainer.visibility = it }
                )
            }

        browseMapFragment = if (savedInstanceState == null) {
            placeBrowseMapFragment().apply { BrowseMapDemoDefaultState.setTo(this) }
        } else {
            supportFragmentManager.findFragmentByTag(BROWSE_MAP_FRAGMENT_TAG) as BrowseMapFragment
        }

        browseMapFragment.setOnMapClickListener(viewModel.onMapClickListener)
        browseMapFragment.setSearchConnectionProvider(viewModel.searchModuleConnectionProvider)

        supportFragmentManager.addOnBackStackChangedListener(backStackChangedListener)
    }

    private fun placeBrowseMapFragment(): BrowseMapFragment {
        val browseMapFragment = BrowseMapFragment()
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragmentContainer, browseMapFragment, BROWSE_MAP_FRAGMENT_TAG)
            .runOnCommit {
                viewModel.mapDataModel = browseMapFragment.mapDataModel
                viewModel.cameraDataModel = browseMapFragment.cameraDataModel
            }
            .commit()
        return browseMapFragment
    }

    private fun placeNavigationFragment(route: Route) {
        val navigationFragment = NavigationFragment().apply {
            setVehicleSkin(VehicleSkin.CAR)
            this.route = route
        }
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragmentContainer, navigationFragment, NAVIGATION_FRAGMENT_TAG)
            .addToBackStack(null)
            .commit()
    }

    private fun placeRoutingOptionsFragment() {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragmentContainer, RoutingOptionsFragment(), ROUTING_OPTIONS_FRAGMENT_TAG)
            .addToBackStack(null)
            .commit()
    }

    private fun createRoutePlanAndComputeRoute(destination: GeoCoordinates, options: RoutingOptions) {
        requestLastValidLocation { lastValidLocation ->
            val routePlan = RoutePlan().apply {
                setStart(lastValidLocation)
                setDestination(destination)
                routingOptions = options
            }

            routePlan.getPrimaryRoute {
                routeComputeProgressBarContainer.visibility = View.GONE
                placeNavigationFragment(it)
            }
        }
    }

    private fun requestLastValidLocation(currentLocationCallback: (GeoCoordinates) -> Unit) {
        if (isPermissionNotGranted(Manifest.permission.ACCESS_FINE_LOCATION)) {
            requestPermission(
                Manifest.permission.ACCESS_FINE_LOCATION,
                REQUEST_CODE_PERMISSION_ACCESS_FINE_LOCATION
            )
            return
        }

        if (isGpsNotEnabled()) {
            if (isGooglePlayServicesAvailable()) {
                createGoogleApiLocationRequest(REQUEST_CODE_GOOGLE_API_CLIENT)
            } else {
                showGenericNoGpsDialog(REQUEST_CODE_SETTING_ACTIVITY)
            }
            return
        }

        getLastValidLocation(currentLocationCallback)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_CODE_PERMISSION_ACCESS_FINE_LOCATION -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    viewModel.targetPosition?.let {
                        createRoutePlanAndComputeRoute(it, viewModel.routingOptions)
                    }
                } else {
                    longToast("Sorry, location permission is needed!")
                    finish()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            REQUEST_CODE_SETTING_ACTIVITY, REQUEST_CODE_GOOGLE_API_CLIENT -> {
                if (isGpsEnabled()) {
                    viewModel.targetPosition?.let {
                        createRoutePlanAndComputeRoute(it, viewModel.routingOptions)
                    }
                } else {
                    longToast("GPS module is not enabled :(")
                    finish()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        supportFragmentManager.removeOnBackStackChangedListener(backStackChangedListener)
    }
}