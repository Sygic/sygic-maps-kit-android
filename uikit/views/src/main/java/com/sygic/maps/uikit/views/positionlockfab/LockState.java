package com.sygic.maps.uikit.views.positionlockfab;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@IntDef({LockState.UNLOCKED, LockState.LOCKED, LockState.LOCKED_AUTOROTATE})
@Retention(RetentionPolicy.SOURCE)
public @interface LockState {
    int UNLOCKED = 0;
    int LOCKED = 1;
    int LOCKED_AUTOROTATE = 2;
}
