package com.sygic.maps.uikit.viewmodels.common.location;

import androidx.annotation.IntDef;
import androidx.annotation.RestrictTo;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@IntDef({EnableGpsResult.ENABLED, EnableGpsResult.DENIED})
@Retention(RetentionPolicy.SOURCE)
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
public @interface EnableGpsResult {
    int ENABLED = 0;
    int DENIED = 1;
}