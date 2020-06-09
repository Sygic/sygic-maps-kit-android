package com.sygic.samples.demo

import com.sygic.sdk.route.RoutingOptions

interface RoutingOptionsManager {
    var isHighwayAvoided: Boolean
    var isTollRoadAvoided: Boolean
    var routingService: Int
    var transportMode: Int
    var hazardousMaterialClass: Int
    var routingType: Int
    var tunnelRestriction: Int
    var vehicleFuelType: Int
    var dimensionalRestrictions: Set<Pair<Int, Int>>

    fun getRoutingOptions(): RoutingOptions
    fun resetToDefaults()
}