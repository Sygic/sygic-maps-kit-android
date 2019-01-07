package com.sygic.modules.routeplanner.viewmodel

import android.content.res.TypedArray
import androidx.lifecycle.ViewModel
import com.sygic.tools.annotations.Assisted
import com.sygic.tools.annotations.AutoFactory

@AutoFactory
class RoutePlannerFragmentViewModel internal constructor(
    @Assisted attributesTypedArray: TypedArray?
) : ViewModel() {

    init {
        attributesTypedArray?.let {
            it.recycle()
        }
    }
}