package com.sygic.maps.uikit.views.zoomcontrols;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@IntDef({ZoomType.IN, ZoomType.OUT})
@Retention(RetentionPolicy.SOURCE)
public @interface ZoomType {
    int IN = 0;
    int OUT = 1;
}