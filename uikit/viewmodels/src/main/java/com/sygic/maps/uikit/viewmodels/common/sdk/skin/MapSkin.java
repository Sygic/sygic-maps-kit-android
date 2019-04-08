package com.sygic.maps.uikit.viewmodels.common.sdk.skin;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@StringDef({MapSkin.DEFAULT, MapSkin.DAY, MapSkin.NIGHT})
@Retention(RetentionPolicy.SOURCE)
public @interface MapSkin {
    String DEFAULT = "default";
    String DAY = "day";
    String NIGHT = "night";
}
