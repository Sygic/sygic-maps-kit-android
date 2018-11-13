package com.sygic.ui.view.zoomcontrols;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@IntDef({TiltType.TILT_2D, TiltType.TILT_3D})
@Retention(RetentionPolicy.SOURCE)
public @interface TiltType {
    int TILT_2D = 0;
    int TILT_3D = 1;
}
