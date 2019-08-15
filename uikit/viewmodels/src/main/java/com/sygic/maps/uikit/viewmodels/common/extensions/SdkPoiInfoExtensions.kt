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

package com.sygic.maps.uikit.viewmodels.common.extensions

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import com.sygic.maps.uikit.viewmodels.R
import com.sygic.sdk.places.PoiInfo

@ColorRes
internal fun Int.toColorRes(): Int {
    return when (this) {
        PoiInfo.PoiGroup.Unknown -> R.color.all_places
        PoiInfo.PoiGroup.Food_and_Drink -> R.color.food_and_drink
        PoiInfo.PoiGroup.Accommodation -> R.color.accommodation
        PoiInfo.PoiGroup.Shopping -> R.color.shopping
        PoiInfo.PoiGroup.Transportation -> R.color.transportation
        PoiInfo.PoiGroup.Tourism -> R.color.tourism
        PoiInfo.PoiGroup.Social_Life -> R.color.social_life
        PoiInfo.PoiGroup.Services_and_Education -> R.color.services_and_education
        PoiInfo.PoiGroup.Sport -> R.color.sport
        PoiInfo.PoiGroup.Vehicle_Services -> R.color.vehicle_services
        PoiInfo.PoiGroup.Emergency -> R.color.emergency
        PoiInfo.PoiGroup.Guides -> R.color.guides
        PoiInfo.PoiGroup.Parking -> R.color.parking
        PoiInfo.PoiGroup.Petrol_Station -> R.color.petrol_station
        PoiInfo.PoiGroup.BankATM -> R.color.bank_atm
        else -> R.color.all_places
    }
}

@DrawableRes
internal fun Int.toGroupIconDrawableRes(): Int {
    return when (this) {
        PoiInfo.PoiGroup.Food_and_Drink -> R.drawable.ic_eat_food
        PoiInfo.PoiGroup.Accommodation -> R.drawable.ic_accomodation
        PoiInfo.PoiGroup.Shopping -> R.drawable.ic_shopping_bag
        PoiInfo.PoiGroup.Transportation -> R.drawable.ic_plane
        PoiInfo.PoiGroup.Tourism -> R.drawable.ic_attractions
        PoiInfo.PoiGroup.Social_Life -> R.drawable.ic_theatre
        PoiInfo.PoiGroup.Services_and_Education -> R.drawable.ic_school
        PoiInfo.PoiGroup.Sport -> R.drawable.ic_sport
        PoiInfo.PoiGroup.Vehicle_Services -> R.drawable.ic_car
        PoiInfo.PoiGroup.Emergency -> R.drawable.ic_hospital
        PoiInfo.PoiGroup.Guides -> R.drawable.ic_info_point
        PoiInfo.PoiGroup.Parking -> R.drawable.ic_parking
        PoiInfo.PoiGroup.Petrol_Station -> R.drawable.ic_petrol_station
        PoiInfo.PoiGroup.BankATM -> R.drawable.ic_money
        else -> R.drawable.ic_location
    }
}

@DrawableRes
internal fun Int.toCategoryIconDrawableRes(): Int {
    return when (this) {
        PoiInfo.PoiCategory.TrafficLights -> R.drawable.ic_traffic_lights
        PoiInfo.PoiCategory.Winery -> R.drawable.ic_wine
        PoiInfo.PoiCategory.Museum -> R.drawable.ic_museum
        PoiInfo.PoiCategory.Sports_Centre -> R.drawable.ic_stadium
        PoiInfo.PoiCategory.Hospital_Polyclinic -> R.drawable.ic_hospital
        PoiInfo.PoiCategory.Police_Station -> R.drawable.ic_police_station
        PoiInfo.PoiCategory.City_Hall -> R.drawable.ic_citty_hall
        PoiInfo.PoiCategory.Post_Office -> R.drawable.ic_post
        PoiInfo.PoiCategory.First_Aid_Post -> R.drawable.ic_first_aid
        PoiInfo.PoiCategory.Pharmacy -> R.drawable.ic_drugs
        PoiInfo.PoiCategory.Department_Store -> R.drawable.ic_shopping_cart
        PoiInfo.PoiCategory.Bank -> R.drawable.ic_bank
        PoiInfo.PoiCategory.Travel_Agency -> R.drawable.ic_attractions
        PoiInfo.PoiCategory.Public_Phone -> R.drawable.ic_phone
        PoiInfo.PoiCategory.Warehouse -> R.drawable.ic_box
        PoiInfo.PoiCategory.Ski_Lift_Station -> R.drawable.ic_cableway
        PoiInfo.PoiCategory.Zoo -> R.drawable.ic_zoo
        PoiInfo.PoiCategory.Scenic_Panoramic_View -> R.drawable.ic_mountines
        PoiInfo.PoiCategory.Transport_Company -> R.drawable.ic_transport_company
        PoiInfo.PoiCategory.Casino -> R.drawable.ic_casino
        PoiInfo.PoiCategory.Cinema -> R.drawable.ic_cinema
        PoiInfo.PoiCategory.Cargo_Centre -> R.drawable.ic_box
        PoiInfo.PoiCategory.Car_Shipping_Terminal -> R.drawable.ic_ferry_terminal
        PoiInfo.PoiCategory.Camping_Ground -> R.drawable.ic_camping
        PoiInfo.PoiCategory.Caravan_Site -> R.drawable.ic_caravan
        PoiInfo.PoiCategory.Coach_and_Lorry_Parking -> R.drawable.ic_parking
        PoiInfo.PoiCategory.Community_Centre -> R.drawable.ic_bureau
        PoiInfo.PoiCategory.Embassy -> R.drawable.ic_citty_hall
        PoiInfo.PoiCategory.Recreation_Facility -> R.drawable.ic_spa
        PoiInfo.PoiCategory.Road_Side_Diner -> R.drawable.ic_eat_food
        PoiInfo.PoiCategory.School -> R.drawable.ic_school
        PoiInfo.PoiCategory.Shopping_Centre -> R.drawable.ic_shopping_bag
        PoiInfo.PoiCategory.Stadium -> R.drawable.ic_stadium
        PoiInfo.PoiCategory.Toll -> R.drawable.ic_toll
        PoiInfo.PoiCategory.College_University -> R.drawable.ic_school
        PoiInfo.PoiCategory.Business_Facility -> R.drawable.ic_bureau
        PoiInfo.PoiCategory.Airport -> R.drawable.ic_plane
        PoiInfo.PoiCategory.Bus_Station -> R.drawable.ic_bus
        PoiInfo.PoiCategory.Exhibition_Centre -> R.drawable.ic_bureau
        PoiInfo.PoiCategory.Kindergarten -> R.drawable.ic_kindergarten
        PoiInfo.PoiCategory.Emergency_Call_Station -> R.drawable.ic_emergency_phone
        PoiInfo.PoiCategory.Emergency_Medical_Service -> R.drawable.ic_first_aid
        PoiInfo.PoiCategory.Fire_Brigade -> R.drawable.ic_fire
        PoiInfo.PoiCategory.ATM -> R.drawable.ic_atm
        PoiInfo.PoiCategory.Hippodrome -> R.drawable.ic_stadium
        PoiInfo.PoiCategory.Beach -> R.drawable.ic_beach
        PoiInfo.PoiCategory.Restaurant_Area -> R.drawable.ic_restaurant
        PoiInfo.PoiCategory.Ice_Skating_Rink -> R.drawable.ic_stadium
        PoiInfo.PoiCategory.Courthouse -> R.drawable.ic_court
        PoiInfo.PoiCategory.Mountain_Peak -> R.drawable.ic_peak
        PoiInfo.PoiCategory.Opera -> R.drawable.ic_opera
        PoiInfo.PoiCategory.Concert_Hall -> R.drawable.ic_philharmonic
        PoiInfo.PoiCategory.Bovag_Garage -> R.drawable.ic_parking_garage_house
        PoiInfo.PoiCategory.Tennis_Court -> R.drawable.ic_tennis
        PoiInfo.PoiCategory.Skating_Rink -> R.drawable.ic_sport
        PoiInfo.PoiCategory.Water_Sport -> R.drawable.ic_pool_swim
        PoiInfo.PoiCategory.Music_Centre -> R.drawable.ic_philharmonic
        PoiInfo.PoiCategory.Doctor -> R.drawable.ic_hospital
        PoiInfo.PoiCategory.Dentist -> R.drawable.ic_dentist
        PoiInfo.PoiCategory.Veterinarian -> R.drawable.ic_vet
        PoiInfo.PoiCategory.Cafe_Pub -> R.drawable.ic_cafe
        PoiInfo.PoiCategory.Convention_Centre -> R.drawable.ic_conference
        PoiInfo.PoiCategory.Leisure_Centre -> R.drawable.ic_spa
        PoiInfo.PoiCategory.Nightlife -> R.drawable.ic_bar
        PoiInfo.PoiCategory.Yacht_Basin -> R.drawable.ic_dock
        PoiInfo.PoiCategory.Commercial_Building, PoiInfo.PoiCategory.Condominium -> R.drawable.ic_apartment_house
        PoiInfo.PoiCategory.Industrial_Building -> R.drawable.ic_factory
        PoiInfo.PoiCategory.Natives_Reservation -> R.drawable.ic_attractions
        PoiInfo.PoiCategory.Cemetery -> R.drawable.ic_cemetery
        PoiInfo.PoiCategory.Vehicle_Equipment_Provider, PoiInfo.PoiCategory.Breakdown_Service -> R.drawable.ic_car_service
        PoiInfo.PoiCategory.Abbey -> R.drawable.ic_church
        PoiInfo.PoiCategory.Amusement_Park -> R.drawable.ic_amusement_park
        PoiInfo.PoiCategory.Arts_Centre -> R.drawable.ic_gallery
        PoiInfo.PoiCategory.Building_Footprint -> R.drawable.ic_attractions
        PoiInfo.PoiCategory.Castle -> R.drawable.ic_castle
        PoiInfo.PoiCategory.Church -> R.drawable.ic_church
        PoiInfo.PoiCategory.Factory_Ground_Philips -> R.drawable.ic_factory
        PoiInfo.PoiCategory.Fortress -> R.drawable.ic_castle
        PoiInfo.PoiCategory.Golf_Course -> R.drawable.ic_golf
        PoiInfo.PoiCategory.Holiday_Area -> R.drawable.ic_beach
        PoiInfo.PoiCategory.Library -> R.drawable.ic_library
        PoiInfo.PoiCategory.Lighthouse -> R.drawable.ic_attractions
        PoiInfo.PoiCategory.Military_Cemetery -> R.drawable.ic_cemetery
        PoiInfo.PoiCategory.Monastery -> R.drawable.ic_church
        PoiInfo.PoiCategory.Monument -> R.drawable.ic_monument
        PoiInfo.PoiCategory.Natural_Reserve -> R.drawable.ic_tree
        PoiInfo.PoiCategory.Prison -> R.drawable.ic_jail_prison
        PoiInfo.PoiCategory.Rocks -> R.drawable.ic_attractions
        PoiInfo.PoiCategory.Sports_Hall -> R.drawable.ic_stadium
        PoiInfo.PoiCategory.State_Police_Office -> R.drawable.ic_police_station
        PoiInfo.PoiCategory.Windmill, PoiInfo.PoiCategory.Walking_Area, PoiInfo.PoiCategory.Water_Mill -> R.drawable.ic_attractions
        PoiInfo.PoiCategory.Car_Racetrack -> R.drawable.ic_stadium
        PoiInfo.PoiCategory.Mountain_Pass -> R.drawable.ic_mountines
        PoiInfo.PoiCategory.Swimming_Pool -> R.drawable.ic_pool_swim
        PoiInfo.PoiCategory.Government_Office -> R.drawable.ic_bureau
        PoiInfo.PoiCategory.Agricultural_Industry -> R.drawable.ic_tractor
        PoiInfo.PoiCategory.Factories -> R.drawable.ic_factory
        PoiInfo.PoiCategory.Medical_Material -> R.drawable.ic_drugs
        PoiInfo.PoiCategory.Personal_Services -> R.drawable.ic_account
        PoiInfo.PoiCategory.Real_Estate -> R.drawable.ic_apartment_house
        PoiInfo.PoiCategory.Hair_And_Beauty -> R.drawable.ic_shopping_bag
        PoiInfo.PoiCategory.Groceries -> R.drawable.ic_groceries
        PoiInfo.PoiCategory.Port -> R.drawable.ic_dock
        PoiInfo.PoiCategory.Money_Transfer, PoiInfo.PoiCategory.Exchange -> R.drawable.ic_exchange_money
        PoiInfo.PoiCategory.Pastry_and_Sweets -> R.drawable.ic_cake
        PoiInfo.PoiCategory.Archeology -> R.drawable.ic_attractions
        PoiInfo.PoiCategory.Ecotourism_Sites -> R.drawable.ic_tree
        PoiInfo.PoiCategory.Hunting_Shop -> R.drawable.ic_shopping_cart
        PoiInfo.PoiCategory.Kids_Place -> R.drawable.ic_playground
        PoiInfo.PoiCategory.Mobile_Shop -> R.drawable.ic_mobile_phone
        PoiInfo.PoiCategory.Mosque -> R.drawable.ic_mosque
        PoiInfo.PoiCategory.Parking_Garage -> R.drawable.ic_parking_garage_house
        PoiInfo.PoiCategory.Place_of_Worship -> R.drawable.ic_pray
        PoiInfo.PoiCategory.Ferry_Terminal -> R.drawable.ic_ferry_terminal
        PoiInfo.PoiCategory.Airline_Access -> R.drawable.ic_terminal
        PoiInfo.PoiCategory.Open_Parking_Area -> R.drawable.ic_parking
        PoiInfo.PoiCategory.Important_Tourist_Attraction -> R.drawable.ic_attractions
        PoiInfo.PoiCategory.Railway_Station -> R.drawable.ic_train_station
        PoiInfo.PoiCategory.Rest_Area -> R.drawable.ic_resting_bench
        PoiInfo.PoiCategory.Shop -> R.drawable.ic_shopping_bag
        PoiInfo.PoiCategory.Park_and_Recreation_Area -> R.drawable.ic_park
        PoiInfo.PoiCategory.Forest_Area -> R.drawable.ic_tree
        PoiInfo.PoiCategory.Public_Transport_Stop -> R.drawable.ic_bus_stop
        PoiInfo.PoiCategory.Park_And_Ride -> R.drawable.ic_parking
        PoiInfo.PoiCategory.Petrol_Station -> R.drawable.ic_petrol_station
        PoiInfo.PoiCategory.Hotel_or_Motel -> R.drawable.ic_accomodation
        PoiInfo.PoiCategory.Restaurant -> R.drawable.ic_restaurant
        PoiInfo.PoiCategory.Cash_Dispenser -> R.drawable.ic_money
        PoiInfo.PoiCategory.Food -> R.drawable.ic_eat_food
        PoiInfo.PoiCategory.Speed_Cameras -> R.drawable.ic_speedcam
        PoiInfo.PoiCategory.Supermarket -> R.drawable.ic_shopping_cart
        PoiInfo.PoiCategory.Accessories_Furniture -> R.drawable.ic_furniture_sofa
        PoiInfo.PoiCategory.Books_Cards -> R.drawable.ic_book
        PoiInfo.PoiCategory.Childrens_Fashion -> R.drawable.ic_fashion
        PoiInfo.PoiCategory.Children_Toys -> R.drawable.ic_toys
        PoiInfo.PoiCategory.Cosmetics_Perfumes -> R.drawable.ic_parfumes
        PoiInfo.PoiCategory.Electronics_Mobiles -> R.drawable.ic_mobile_phone
        PoiInfo.PoiCategory.Traditional_Fashion, PoiInfo.PoiCategory.Fashion_Mixed, PoiInfo.PoiCategory.Fashion_Accessories -> R.drawable.ic_fashion
        PoiInfo.PoiCategory.Gifts_Antiques -> R.drawable.ic_present
        PoiInfo.PoiCategory.Jewellery_Watches -> R.drawable.ic_jewelery
        PoiInfo.PoiCategory.Ladies_Fashion -> R.drawable.ic_fashion
        PoiInfo.PoiCategory.Lifestyle_Fitness -> R.drawable.ic_fitness
        PoiInfo.PoiCategory.Men_s_Fashion -> R.drawable.ic_fashion
        PoiInfo.PoiCategory.Opticians_Sunglasses -> R.drawable.ic_shopping_bag
        PoiInfo.PoiCategory.Shoes_Bags -> R.drawable.ic_shoes
        PoiInfo.PoiCategory.Sports -> R.drawable.ic_sport
        PoiInfo.PoiCategory.Metro -> R.drawable.ic_metro_station
        PoiInfo.PoiCategory.Wikipedia, PoiInfo.PoiCategory.Tourist_Information_Office -> R.drawable.ic_info_point
        PoiInfo.PoiCategory.Cultural_Centre, PoiInfo.PoiCategory.Theatre, PoiInfo.PoiCategory.Entertainment -> R.drawable.ic_theatre
        PoiInfo.PoiCategory.Customs, PoiInfo.PoiCategory.Frontier_Crossing, PoiInfo.PoiCategory.Border_Point -> R.drawable.ic_flags
        PoiInfo.PoiCategory.Car_Dealer, PoiInfo.PoiCategory.Rent_a_Car_Parking, PoiInfo.PoiCategory.Chevrolet_Car_Dealer,
        PoiInfo.PoiCategory.Motoring_Organization_Office, PoiInfo.PoiCategory.Rent_a_Car_Facility -> R.drawable.ic_car
        PoiInfo.PoiCategory.Car_Services, PoiInfo.PoiCategory.Car_Repair_Facility, PoiInfo.PoiCategory.Chevrolet_Car_Repair -> R.drawable.ic_car_service
        else -> R.drawable.ic_location
    }
}