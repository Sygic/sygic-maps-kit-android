package com.sygic.maps.uikit.viewmodels.common.location.livedata

import com.sygic.maps.uikit.views.common.livedata.SingleLiveEvent
import com.sygic.maps.uikit.viewmodels.common.location.LocationManager

class LocationRequestLiveEvent : SingleLiveEvent<LocationManager.LocationRequesterCallback>()