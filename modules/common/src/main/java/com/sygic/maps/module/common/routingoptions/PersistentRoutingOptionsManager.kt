/*
 * Copyright (c) 2020 Sygic a.s. All rights reserved.
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

package com.sygic.maps.module.common.routingoptions

import android.content.Context
import android.content.SharedPreferences
import com.sygic.maps.uikit.views.common.extensions.get
import com.sygic.maps.uikit.views.common.extensions.set
import com.sygic.sdk.route.RoutingOptions
import javax.inject.Inject

class PersistentRoutingOptionsManager(private val preferences: SharedPreferences) : RoutingOptionsManager {
    override var isHighwayAvoided: Boolean
        get() = preferences[HIGHWAY_AVOIDED_KEY, false]
        set(value) {
            preferences[HIGHWAY_AVOIDED_KEY] = value
        }

    override var isTollRoadAvoided: Boolean
        get() = preferences[TOLL_ROAD_AVOIDED_KEY, false]
        set(value) {
            preferences[TOLL_ROAD_AVOIDED_KEY] = value
        }

    override var routingService: Int
        get() = preferences[ROUTING_SERVICE_KEY, RoutingOptions.RoutingService.Automatic]
        set(value) {
            preferences[ROUTING_SERVICE_KEY] = value
        }

    override var transportMode: Int
        get() = preferences[TRANSPORT_MODE_KEY, RoutingOptions.TransportMode.Car]
        set(value) {
            preferences[TRANSPORT_MODE_KEY] = value
        }

    override var hazardousMaterialClass: Int
        get() = preferences[HAZARDOUS_MATERIAL_CLASS_KEY, RoutingOptions.HazardousMaterialClass.None]
        set(value) {
            preferences[HAZARDOUS_MATERIAL_CLASS_KEY] = value
        }

    override var routingType: Int
        get() = preferences[ROUTING_TYPE_KEY, RoutingOptions.RoutingType.Economic]
        set(value) {
            preferences[ROUTING_TYPE_KEY] = value
        }

    override var tunnelRestriction: Int
        get() = preferences[TUNNEL_RESTRICTION_KEY, RoutingOptions.ADRTunnelType.Unknown]
        set(value) {
            preferences[TUNNEL_RESTRICTION_KEY] = value
        }

    override var vehicleFuelType: Int
        get() = preferences[VEHICLE_FUEL_TYPE_KEY, RoutingOptions.VehicleFuelType.Petrol]
        set(value) {
            preferences[VEHICLE_FUEL_TYPE_KEY] = value
        }

    override var dimensionalRestrictions: Set<Pair<Int, Int>>
        get() = preferences.getStringSet(DIMENSIONAL_RESTRICTIONS_KEY, setOf())!!.map { it.toRestriction() }.toSet()
        set(value) {
            preferences.edit().putStringSet(DIMENSIONAL_RESTRICTIONS_KEY, value.map { it.toPersistableString() }.toSet()).apply()
        }

    @Inject
    constructor(context: Context) : this(
        context.getSharedPreferences(
            PREFERENCES_NAME,
            Context.MODE_PRIVATE
        )
    )

    override fun getRoutingOptions() = RoutingOptions().apply {
        isHighwayAvoided = this@PersistentRoutingOptionsManager.isHighwayAvoided
        isTollRoadAvoided = this@PersistentRoutingOptionsManager.isTollRoadAvoided
        routingService = this@PersistentRoutingOptionsManager.routingService
        transportMode = this@PersistentRoutingOptionsManager.transportMode
        hazardousMaterialClass = this@PersistentRoutingOptionsManager.hazardousMaterialClass
        routingType = this@PersistentRoutingOptionsManager.routingType
        tunnelRestriction = this@PersistentRoutingOptionsManager.tunnelRestriction
        vehicleFuelType = this@PersistentRoutingOptionsManager.vehicleFuelType
        dimensionalRestrictions.forEach {
            addDimensionalRestriction(it.first, it.second)
        }
    }

    override fun resetToDefaults() = preferences.edit().clear().apply()

    private fun String.toRestriction() = split(":").let { it[0].toInt() to it[1].toInt() }

    private fun Pair<Int, Int>.toPersistableString() = "$first:$second"

    companion object {
        private const val PREFERENCES_NAME = "routing_options_prefs"

        private const val HIGHWAY_AVOIDED_KEY = "ro_highway_avoided"
        private const val TOLL_ROAD_AVOIDED_KEY = "ro_toll_road_avoided"
        private const val ROUTING_SERVICE_KEY = "ro_routing_service"
        private const val TRANSPORT_MODE_KEY = "ro_transport_mode"
        private const val HAZARDOUS_MATERIAL_CLASS_KEY = "ro_hazardous_material_class"
        private const val ROUTING_TYPE_KEY = "ro_routing_type"
        private const val TUNNEL_RESTRICTION_KEY = "ro_tunnel_restriction"
        private const val VEHICLE_FUEL_TYPE_KEY = "ro_vehicle_fuel_type"
        private const val DIMENSIONAL_RESTRICTIONS_KEY = "ro_dimensional_restrictions"
    }
}