package com.sygic.ui.common.sdk.location;

import androidx.annotation.IntDef;

import java.lang.annotation.RetentionPolicy;

@IntDef({EnableGpsResult.ENABLED, EnableGpsResult.DENIED})
@java.lang.annotation.Retention(RetentionPolicy.SOURCE)
public @interface EnableGpsResult {
    int ENABLED = 0;
    int DENIED = 1;
}