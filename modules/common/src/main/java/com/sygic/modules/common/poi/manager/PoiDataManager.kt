package com.sygic.modules.common.poi.manager

import androidx.annotation.RestrictTo
import com.sygic.sdk.map.`object`.ViewObject
import com.sygic.sdk.map.`object`.data.ViewObjectData
import com.sygic.sdk.places.LocationInfo
import com.sygic.sdk.places.Place
import com.sygic.sdk.places.Places
import com.sygic.sdk.position.GeoCoordinates
import com.sygic.sdk.search.ReverseGeocoder
import com.sygic.sdk.search.ReverseSearchResult
import com.sygic.ui.common.sdk.data.PoiData
import com.sygic.ui.common.sdk.extension.getFirst

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
interface PoiDataManager {

    fun getPayloadData(viewObject: ViewObject, callback: Callback)

    abstract class Callback : Places.PlaceListener, ReverseGeocoder.ReverseSearchResultsListener {
        abstract fun onDataLoaded(data: ViewObjectData)

        final override fun onPlaceLoaded(place: Place) {
            onDataLoaded(
                ViewObjectData(
                    place.coordinates,
                    PoiData(
                        name = place.name,
                        iso = place.iso,
                        poiCategory = place.category,
                        poiGroup = place.group,
                        city = place.locationInfo.getFirst(LocationInfo.LocationType.City),
                        street = place.locationInfo.getFirst(LocationInfo.LocationType.Street),
                        houseNumber = place.locationInfo.getFirst(LocationInfo.LocationType.HouseNum),
                        postal = place.locationInfo.getFirst(LocationInfo.LocationType.Postal),
                        phone = place.locationInfo.getFirst(LocationInfo.LocationType.Phone),
                        email = place.locationInfo.getFirst(LocationInfo.LocationType.Mail),
                        url = place.locationInfo.getFirst(LocationInfo.LocationType.Url)
                    )
                )
            )
        }

        final override fun onSearchResults(results: List<ReverseSearchResult>, position: GeoCoordinates) {
            if (results.isEmpty()) {
                onDataLoaded(ViewObjectData(position, PoiData()))
                return
            }

            val reverseSearchResult = results.first()
            onDataLoaded(
                ViewObjectData(
                    reverseSearchResult.position,
                    PoiData(
                        iso = reverseSearchResult.names.countryIso,
                        city = reverseSearchResult.names.city,
                        street = reverseSearchResult.names.street,
                        houseNumber = reverseSearchResult.names.houseNumber
                    )
                )
            )
        }
    }
}