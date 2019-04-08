package com.sygic.maps.uikit.viewmodels.common.sdk.skin;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@StringDef({VehicleSkin.CAR, VehicleSkin.CAR_NO_SIGNAL,
        VehicleSkin.PEDESTRIAN, VehicleSkin.PEDESTRIAN_NO_SIGNAL})
@Retention(RetentionPolicy.SOURCE)
public @interface VehicleSkin {
    String CAR = "car";
    String CAR_NO_SIGNAL = "car_no_signal";
    String PEDESTRIAN = "pedestrian";
    String PEDESTRIAN_NO_SIGNAL = "pedestrian_no_signal";
}
