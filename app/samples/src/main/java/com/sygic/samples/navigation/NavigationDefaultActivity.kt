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

package com.sygic.samples.navigation

import android.os.Bundle
import android.util.Log
import com.sygic.maps.module.navigation.NavigationFragment
import com.sygic.samples.R
import com.sygic.samples.app.activities.CommonSampleActivity
import com.sygic.sdk.position.GeoCoordinates
import com.sygic.sdk.route.RouteInfo
import com.sygic.sdk.route.RoutePlan
import com.sygic.sdk.route.Router

class NavigationDefaultActivity : CommonSampleActivity() { //todo

    override val wikiModulePath: String = "Module-Navigation#navigation---default"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_navigation_default)

        val routePlan = RoutePlan().apply {
            //todo
            //start = GeoCoordinates(48.146523, 17.123961)
            //destination = GeoCoordinates(49.190767, 16.611238)

            setStart(GeoCoordinates(48.146523, 17.123961))
            setDestination(GeoCoordinates(49.190767, 16.611238))
        }

        val router = Router()
        router.computeRoute(routePlan, object : Router.RouteComputeAdapter() {
            override fun onPrimaryComputeFinished(router: Router, routes: RouteInfo) {
                Log.d("Tomas", "onPrimaryComputeFinished() called with: router = [$router], routes = [$routes]")

                (supportFragmentManager.findFragmentById(R.id.navigationFragment) as NavigationFragment).routeInfo = routes
            }

            override fun onComputeError(router: Router, @Router.RouteComputeError error: Int) {
                Log.d("Tomas", "onComputeError() called with: router = [$router], error = [$error]")
            }
        })
    }
}