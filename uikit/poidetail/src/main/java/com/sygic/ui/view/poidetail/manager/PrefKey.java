package com.sygic.ui.view.poidetail.manager;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@IntDef({PrefKey.SHOWCASE_ALLOWED})
@Retention(RetentionPolicy.SOURCE)
@interface PrefKey {
    int SHOWCASE_ALLOWED = 0;
}
