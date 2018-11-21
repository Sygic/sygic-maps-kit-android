package com.sygic.modules.common.mapinteraction;

import androidx.annotation.IntDef;
import androidx.annotation.RestrictTo;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@IntDef({MapInteractionMode.NONE, MapInteractionMode.MARKERS_ONLY, MapInteractionMode.FULL})
@Retention(RetentionPolicy.SOURCE)
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
public @interface MapInteractionMode {
    int NONE = 0;
    int MARKERS_ONLY = 1;
    int FULL = 2;
}