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
import com.sygic.sdk.places.PlaceCategories

@ColorRes
internal fun List<String>.toColorRes(): Int = when (this.firstOrNull()) {
    PlaceCategories.Wikipedia -> R.color.guides

    PlaceCategories.HotelOrMotel -> R.color.accommodation

    PlaceCategories.PetrolStation -> R.color.petrol_station

    PlaceCategories.Bank, PlaceCategories.ATM -> R.color.bank_atm

    PlaceCategories.RentACarParking, PlaceCategories.ParkingGarage, PlaceCategories.CoachAndLorryParking,
    PlaceCategories.OpenParkingArea -> R.color.parking

    PlaceCategories.Food, PlaceCategories.RoadSideDiner, PlaceCategories.Winery,
    PlaceCategories.Restaurant, PlaceCategories.RestaurantArea, PlaceCategories.CafePub,
    PlaceCategories.PastryAndSweets -> R.color.food_and_drink

    PlaceCategories.Airport, PlaceCategories.SkiLiftStation, PlaceCategories.CarShippingTerminal,
    PlaceCategories.BusStation, PlaceCategories.Port, PlaceCategories.FerryTerminal,
    PlaceCategories.AirlineAccess, PlaceCategories.RailwayStation, PlaceCategories.PublicTransportStop,
    PlaceCategories.Metro -> R.color.transportation

    PlaceCategories.HospitalPolyclinic, PlaceCategories.PoliceStation, PlaceCategories.FirstAidPost,
    PlaceCategories.Pharmacy, PlaceCategories.EmergencyCallStation, PlaceCategories.EmergencyMedicalService,
    PlaceCategories.FireBrigade, PlaceCategories.Doctor, PlaceCategories.Dentist, PlaceCategories.Veterinarian,
    PlaceCategories.BreakdownService -> R.color.emergency

    PlaceCategories.BovagGarage, PlaceCategories.VehicleEquipmentProvider, PlaceCategories.TrafficLights,
    PlaceCategories.MotoringOrganizationOffice, PlaceCategories.RentACarFacility, PlaceCategories.CarRepairFacility,
    PlaceCategories.RestArea, PlaceCategories.ParkAndRide, PlaceCategories.CarDealer, PlaceCategories.SpeedCameras,
    PlaceCategories.CarServices, PlaceCategories.ChevroletCarDealer, PlaceCategories.ChevroletCarRepair -> R.color.vehicle_services

    PlaceCategories.ShoppingCentre, PlaceCategories.HairAndBeauty, PlaceCategories.Shop,
    PlaceCategories.Supermarket, PlaceCategories.DepartmentStore, PlaceCategories.Warehouse,
    PlaceCategories.TraditionalFashion, PlaceCategories.FashionMixed, PlaceCategories.FashionAccessories,
    PlaceCategories.ChildrensFashion, PlaceCategories.LadiesFashion, PlaceCategories.MensFashion,
    PlaceCategories.ElectronicsMobiles, PlaceCategories.MobileShop, PlaceCategories.AccessoriesFurniture,
    PlaceCategories.BooksCards, PlaceCategories.ChildrenToys, PlaceCategories.CosmeticsPerfumes,
    PlaceCategories.GiftsAntiques, PlaceCategories.JewelleryWatches, PlaceCategories.LifestyleFitness,
    PlaceCategories.ShoesBags, PlaceCategories.OpticiansSunglasses, PlaceCategories.Groceries,
    PlaceCategories.Sports -> R.color.shopping

    PlaceCategories.SportsCentre, PlaceCategories.Stadium, PlaceCategories.Hippodrome,
    PlaceCategories.IceSkatingRink, PlaceCategories.SportsHall, PlaceCategories.CarRacetrack, PlaceCategories.TennisCourt,
    PlaceCategories.SwimmingPool, PlaceCategories.WaterSport, PlaceCategories.YachtBasin, PlaceCategories.GolfCourse,
    PlaceCategories.SkatingRink -> R.color.sport

    PlaceCategories.TouristInformationOffice, PlaceCategories.RecreationFacility, PlaceCategories.Beach,
    PlaceCategories.HolidayArea, PlaceCategories.MountainPeak, PlaceCategories.Monument, PlaceCategories.NaturalReserve,
    PlaceCategories.TravelAgency, PlaceCategories.Zoo, PlaceCategories.MountainPass, PlaceCategories.ScenicPanoramicView,
    PlaceCategories.Museum, PlaceCategories.CampingGround, PlaceCategories.Castle, PlaceCategories.Fortress,
    PlaceCategories.NativesReservation, PlaceCategories.BuildingFootprint, PlaceCategories.Lighthouse,
    PlaceCategories.Rocks, PlaceCategories.Windmill, PlaceCategories.WalkingArea, PlaceCategories.WaterMill,
    PlaceCategories.Archeology, PlaceCategories.ImportantTouristAttraction, PlaceCategories.ParkAndRecreationArea,
    PlaceCategories.ForestArea, PlaceCategories.CaravanSite -> R.color.tourism

    PlaceCategories.Casino, PlaceCategories.Cinema, PlaceCategories.Opera, PlaceCategories.ConcertHall,
    PlaceCategories.MusicCentre, PlaceCategories.CulturalCentre, PlaceCategories.Theatre, PlaceCategories.LeisureCentre,
    PlaceCategories.Nightlife, PlaceCategories.Abbey, PlaceCategories.AmusementPark, PlaceCategories.ArtsCentre,
    PlaceCategories.Church, PlaceCategories.Monastery, PlaceCategories.Library, PlaceCategories.EcotourismSites,
    PlaceCategories.HuntingShop, PlaceCategories.KidsPlace, PlaceCategories.Mosque, PlaceCategories.PlaceOfWorship,
    PlaceCategories.Entertainment -> R.color.social_life

    PlaceCategories.CityHall, PlaceCategories.PostOffice, PlaceCategories.Embassy, PlaceCategories.PublicPhone,
    PlaceCategories.TransportCompany, PlaceCategories.CargoCentre, PlaceCategories.CommunityCentre,
    PlaceCategories.School, PlaceCategories.CollegeUniversity, PlaceCategories.Toll, PlaceCategories.BusinessFacility,
    PlaceCategories.ExhibitionCentre, PlaceCategories.GovernmentOffice, PlaceCategories.Company, PlaceCategories.Freeport,
    PlaceCategories.Kindergarten, PlaceCategories.Courthouse, PlaceCategories.ConventionCentre,
    PlaceCategories.CommercialBuilding, PlaceCategories.Condominium, PlaceCategories.RealEstate, PlaceCategories.Cemetery,
    PlaceCategories.MilitaryCemetery, PlaceCategories.Prison, PlaceCategories.StatePoliceOffice, PlaceCategories.Factories,
    PlaceCategories.AgriculturalIndustry, PlaceCategories.IndustrialBuilding, PlaceCategories.FactoryGroundPhilips,
    PlaceCategories.MedicalMaterial, PlaceCategories.PersonalServices, PlaceCategories.Media, PlaceCategories.Construction,
    PlaceCategories.MoneyTransfer, PlaceCategories.Exchange, PlaceCategories.Professionals, PlaceCategories.CashDispenser,
    PlaceCategories.Services, PlaceCategories.Squares, PlaceCategories.LocalNames, PlaceCategories.Customs,
    PlaceCategories.FrontierCrossing, PlaceCategories.BorderPoint -> R.color.services_and_education

    else -> R.color.all_places
}

@DrawableRes
internal fun List<String>.toCategoryIconDrawableRes(): Int = when (this.firstOrNull()) {
    PlaceCategories.TrafficLights -> R.drawable.ic_traffic_lights
    PlaceCategories.Winery -> R.drawable.ic_wine
    PlaceCategories.Museum -> R.drawable.ic_museum
    PlaceCategories.CityHall, PlaceCategories.Embassy -> R.drawable.ic_citty_hall
    PlaceCategories.PostOffice -> R.drawable.ic_post
    PlaceCategories.Bank -> R.drawable.ic_bank
    PlaceCategories.TravelAgency -> R.drawable.ic_attractions
    PlaceCategories.PublicPhone -> R.drawable.ic_phone
    PlaceCategories.SkiLiftStation -> R.drawable.ic_cableway
    PlaceCategories.Zoo -> R.drawable.ic_zoo
    PlaceCategories.TransportCompany -> R.drawable.ic_transport_company
    PlaceCategories.Casino -> R.drawable.ic_casino
    PlaceCategories.Cinema -> R.drawable.ic_cinema
    PlaceCategories.CargoCentre, PlaceCategories.Warehouse -> R.drawable.ic_box
    PlaceCategories.CampingGround -> R.drawable.ic_camping
    PlaceCategories.CaravanSite -> R.drawable.ic_caravan
    PlaceCategories.RecreationFacility, PlaceCategories.LeisureCentre -> R.drawable.ic_spa
    PlaceCategories.Food, PlaceCategories.RoadSideDiner -> R.drawable.ic_eat_food
    PlaceCategories.School, PlaceCategories.CollegeUniversity -> R.drawable.ic_school
    PlaceCategories.ShoppingCentre, PlaceCategories.HairAndBeauty, PlaceCategories.Shop,
    PlaceCategories.OpticiansSunglasses -> R.drawable.ic_shopping_bag
    PlaceCategories.Toll -> R.drawable.ic_toll
    PlaceCategories.BusinessFacility, PlaceCategories.CommunityCentre, PlaceCategories.ExhibitionCentre,
    PlaceCategories.GovernmentOffice -> R.drawable.ic_bureau
    PlaceCategories.Airport -> R.drawable.ic_plane
    PlaceCategories.BusStation -> R.drawable.ic_bus
    PlaceCategories.Kindergarten -> R.drawable.ic_kindergarten
    PlaceCategories.EmergencyCallStation -> R.drawable.ic_emergency_phone
    PlaceCategories.FirstAidPost, PlaceCategories.EmergencyMedicalService -> R.drawable.ic_first_aid
    PlaceCategories.FireBrigade -> R.drawable.ic_fire
    PlaceCategories.ATM -> R.drawable.ic_atm
    PlaceCategories.Beach, PlaceCategories.HolidayArea -> R.drawable.ic_beach
    PlaceCategories.Courthouse -> R.drawable.ic_court
    PlaceCategories.MountainPeak -> R.drawable.ic_peak
    PlaceCategories.Opera -> R.drawable.ic_opera
    PlaceCategories.ConcertHall, PlaceCategories.MusicCentre -> R.drawable.ic_philharmonic
    PlaceCategories.ParkingGarage, PlaceCategories.BovagGarage -> R.drawable.ic_parking_garage_house
    PlaceCategories.TennisCourt -> R.drawable.ic_tennis
    PlaceCategories.HospitalPolyclinic, PlaceCategories.Doctor -> R.drawable.ic_hospital
    PlaceCategories.Dentist -> R.drawable.ic_dentist
    PlaceCategories.Veterinarian -> R.drawable.ic_vet
    PlaceCategories.CafePub -> R.drawable.ic_cafe
    PlaceCategories.ConventionCentre -> R.drawable.ic_conference
    PlaceCategories.Nightlife -> R.drawable.ic_bar
    PlaceCategories.Port, PlaceCategories.YachtBasin -> R.drawable.ic_dock
    PlaceCategories.CommercialBuilding, PlaceCategories.Condominium, PlaceCategories.RealEstate -> R.drawable.ic_apartment_house
    PlaceCategories.NativesReservation, PlaceCategories.BuildingFootprint, PlaceCategories.Lighthouse,
    PlaceCategories.Rocks, PlaceCategories.Windmill, PlaceCategories.WalkingArea, PlaceCategories.WaterMill,
    PlaceCategories.Archeology, PlaceCategories.ImportantTouristAttraction -> R.drawable.ic_attractions
    PlaceCategories.Cemetery, PlaceCategories.MilitaryCemetery -> R.drawable.ic_cemetery
    PlaceCategories.CarServices, PlaceCategories.CarRepairFacility, PlaceCategories.ChevroletCarRepair,
    PlaceCategories.VehicleEquipmentProvider, PlaceCategories.BreakdownService -> R.drawable.ic_car_service
    PlaceCategories.Church, PlaceCategories.Abbey, PlaceCategories.Monastery -> R.drawable.ic_church
    PlaceCategories.AmusementPark -> R.drawable.ic_amusement_park
    PlaceCategories.ArtsCentre -> R.drawable.ic_gallery
    PlaceCategories.Castle, PlaceCategories.Fortress -> R.drawable.ic_castle
    PlaceCategories.GolfCourse -> R.drawable.ic_golf
    PlaceCategories.Library -> R.drawable.ic_library
    PlaceCategories.Monument -> R.drawable.ic_monument
    PlaceCategories.NaturalReserve, PlaceCategories.EcotourismSites, PlaceCategories.ForestArea -> R.drawable.ic_tree
    PlaceCategories.Prison -> R.drawable.ic_jail_prison
    PlaceCategories.StatePoliceOffice, PlaceCategories.PoliceStation -> R.drawable.ic_police_station
    PlaceCategories.MountainPass, PlaceCategories.ScenicPanoramicView -> R.drawable.ic_mountines
    PlaceCategories.SwimmingPool, PlaceCategories.WaterSport -> R.drawable.ic_pool_swim
    PlaceCategories.AgriculturalIndustry -> R.drawable.ic_tractor
    PlaceCategories.Factories, PlaceCategories.IndustrialBuilding, PlaceCategories.FactoryGroundPhilips -> R.drawable.ic_factory
    PlaceCategories.MedicalMaterial, PlaceCategories.Pharmacy -> R.drawable.ic_drugs
    PlaceCategories.PersonalServices -> R.drawable.ic_account
    PlaceCategories.Groceries -> R.drawable.ic_groceries
    PlaceCategories.MoneyTransfer, PlaceCategories.Exchange -> R.drawable.ic_exchange_money
    PlaceCategories.PastryAndSweets -> R.drawable.ic_cake
    PlaceCategories.KidsPlace -> R.drawable.ic_playground
    PlaceCategories.ElectronicsMobiles, PlaceCategories.MobileShop -> R.drawable.ic_mobile_phone
    PlaceCategories.Mosque -> R.drawable.ic_mosque
    PlaceCategories.PlaceOfWorship -> R.drawable.ic_pray
    PlaceCategories.FerryTerminal, PlaceCategories.CarShippingTerminal -> R.drawable.ic_ferry_terminal
    PlaceCategories.AirlineAccess -> R.drawable.ic_terminal
    PlaceCategories.RailwayStation -> R.drawable.ic_train_station
    PlaceCategories.RestArea -> R.drawable.ic_resting_bench
    PlaceCategories.ParkAndRecreationArea -> R.drawable.ic_park
    PlaceCategories.PublicTransportStop -> R.drawable.ic_bus_stop
    PlaceCategories.ParkAndRide, PlaceCategories.CoachAndLorryParking, PlaceCategories.OpenParkingArea -> R.drawable.ic_parking
    PlaceCategories.PetrolStation -> R.drawable.ic_petrol_station
    PlaceCategories.HotelOrMotel -> R.drawable.ic_accomodation
    PlaceCategories.Restaurant, PlaceCategories.RestaurantArea -> R.drawable.ic_restaurant
    PlaceCategories.CashDispenser -> R.drawable.ic_money
    PlaceCategories.SpeedCameras -> R.drawable.ic_speedcam
    PlaceCategories.Supermarket, PlaceCategories.DepartmentStore, PlaceCategories.HuntingShop -> R.drawable.ic_shopping_cart
    PlaceCategories.AccessoriesFurniture -> R.drawable.ic_furniture_sofa
    PlaceCategories.BooksCards -> R.drawable.ic_book
    PlaceCategories.ChildrenToys -> R.drawable.ic_toys
    PlaceCategories.CosmeticsPerfumes -> R.drawable.ic_parfumes
    PlaceCategories.TraditionalFashion, PlaceCategories.FashionMixed, PlaceCategories.FashionAccessories,
    PlaceCategories.ChildrensFashion, PlaceCategories.LadiesFashion, PlaceCategories.MensFashion -> R.drawable.ic_fashion
    PlaceCategories.GiftsAntiques -> R.drawable.ic_present
    PlaceCategories.JewelleryWatches -> R.drawable.ic_jewelery
    PlaceCategories.LifestyleFitness -> R.drawable.ic_fitness
    PlaceCategories.ShoesBags -> R.drawable.ic_shoes
    PlaceCategories.Sports, PlaceCategories.SkatingRink -> R.drawable.ic_sport
    PlaceCategories.Metro -> R.drawable.ic_metro_station
    PlaceCategories.Wikipedia, PlaceCategories.TouristInformationOffice -> R.drawable.ic_info_point
    PlaceCategories.CulturalCentre, PlaceCategories.Theatre, PlaceCategories.Entertainment -> R.drawable.ic_theatre
    PlaceCategories.Customs, PlaceCategories.FrontierCrossing, PlaceCategories.BorderPoint -> R.drawable.ic_flags
    PlaceCategories.CarDealer, PlaceCategories.RentACarParking, PlaceCategories.ChevroletCarDealer,
    PlaceCategories.MotoringOrganizationOffice, PlaceCategories.RentACarFacility -> R.drawable.ic_car
    PlaceCategories.Stadium, PlaceCategories.SportsCentre, PlaceCategories.Hippodrome,
    PlaceCategories.IceSkatingRink, PlaceCategories.SportsHall, PlaceCategories.CarRacetrack -> R.drawable.ic_stadium
    else -> R.drawable.ic_location
}