package com.sygic.maps.module.common.mapinteraction;

import androidx.annotation.IntDef;
import androidx.annotation.RestrictTo;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@IntDef({MapSelectionMode.NONE, MapSelectionMode.MARKERS_ONLY, MapSelectionMode.FULL})
@Retention(RetentionPolicy.SOURCE)
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
public @interface MapSelectionMode {
    int NONE = 0;
    int MARKERS_ONLY = 1;
    int FULL = 2;
}