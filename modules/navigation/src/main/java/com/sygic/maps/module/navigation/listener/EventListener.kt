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

package com.sygic.maps.module.navigation.listener

import androidx.annotation.RestrictTo
import androidx.lifecycle.LiveData
import com.sygic.maps.module.navigation.NavigationFragment
import com.sygic.sdk.route.RouteInfo

/**
 * Interface definition for a callback to be invoked when a navigation event occurs.
 */
interface EventListener {

    /**
     * Called when the [NavigationFragment] has been created and is waiting for the valid [RouteInfo].
     */
    fun onNavigationCreated() {}

    /**
     * Called when navigation has been started. Note: Even if the [NavigationFragment.previewMode] is set to true,
     * this event will also be called.
     *
     * @param routeInfo [RouteInfo] the route info object used for the navigation.
     *
     */
    fun onNavigationStarted(routeInfo: RouteInfo?) {}

    /**
     * Called when the SDK `onRouteChanged()` event occurs.
     *
     * @param routeInfo [RouteInfo] the new route info object.
     *
     */
    fun onRouteChanged(routeInfo: RouteInfo?) {}

    /**
     * Called when navigation reach the target destination.
     */
    fun onRouteFinishReached() {}

    /**
     * Called when navigation has been finished (including `onFinishReached()`) and if the [NavigationFragment.previewMode]
     * is set to true, then this event will also be called when the route preview finished.
     */
    fun onNavigationFinished() {}

    /**
     * Called when the [NavigationFragment] has done its hard work and is going to be destroyed.
     */
    fun onNavigationDestroyed() {}
}

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
interface EventListenerWrapper {
    val eventListenerProvider: LiveData<EventListener>
}