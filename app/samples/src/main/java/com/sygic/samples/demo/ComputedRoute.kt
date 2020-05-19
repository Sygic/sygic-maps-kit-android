package com.sygic.samples.demo

import com.sygic.sdk.position.GeoCoordinates
import com.sygic.sdk.route.RoutingOptions

data class ComputedRoute(val destination: GeoCoordinates, val options: RoutingOptions)