package com.sygic.modules.common.poi.manager

import androidx.annotation.RestrictTo
import com.sygic.sdk.map.`object`.ViewObject
import com.sygic.sdk.map.`object`.data.MarkerData
import com.sygic.sdk.places.LocationInfo
import com.sygic.sdk.places.Place
import com.sygic.sdk.places.Places
import com.sygic.sdk.position.GeoCoordinates
import com.sygic.sdk.search.ReverseGeocoder
import com.sygic.sdk.search.ReverseSearchResult
import com.sygic.ui.common.sdk.data.PoiMarkerData
import com.sygic.ui.common.sdk.extension.getFirst

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
interface PoiDataManager {

    fun getPayloadData(viewObject: ViewObject, callback: Callback)

    abstract class Callback : Places.PlaceListener, ReverseGeocoder.ReverseSearchResultsListener {
        abstract fun onDataLoaded(data: MarkerData)

        final override fun onPlaceLoaded(place: Place) {
            onDataLoaded(
                PoiMarkerData(
                    place.coordinates,
                    place.name,
                    place.iso,
                    place.category,
                    place.group,
                    place.locationInfo.getFirst(LocationInfo.LocationType.City),
                    place.locationInfo.getFirst(LocationInfo.LocationType.Street),
                    place.locationInfo.getFirst(LocationInfo.LocationType.HouseNum),
                    place.locationInfo.getFirst(LocationInfo.LocationType.Postal),
                    place.locationInfo.getFirst(LocationInfo.LocationType.Phone),
                    place.locationInfo.getFirst(LocationInfo.LocationType.Mail),
                    place.locationInfo.getFirst(LocationInfo.LocationType.Url)
                )
            )
        }

        final override fun onSearchResults(results: List<ReverseSearchResult>, position: GeoCoordinates) {
            if (results.isEmpty()) {
                onDataLoaded(PoiMarkerData(position))
                return
            }

            val reverseSearchResult = results.first()
            onDataLoaded(
                PoiMarkerData(reverseSearchResult.position,
                    iso = reverseSearchResult.names.countryIso,
                    city = reverseSearchResult.names.city,
                    street = reverseSearchResult.names.street,
                    houseNumber = reverseSearchResult.names.houseNumber)
            )
        }
    }
}