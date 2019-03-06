package com.sygic.samples.idling

import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.sygic.samples.CommonSampleActivity
import com.sygic.ui.view.poidetail.PoiDetailBottomDialogFragment

class PoiDetailVisibilityIdlingResource(
    activity: CommonSampleActivity,
    @BottomSheetBehavior.State private val expectedBottomSheetState: Int
) : BaseIdlingResource(activity) {

    private val poiDetailBottomDialogFragment
        get() = activity.supportFragmentManager?.findFragmentByTag(PoiDetailBottomDialogFragment.TAG)

    override fun getName(): String = "PoiDetailVisibilityIdlingResource"

    override fun isIdleNow(): Boolean {
        when (expectedBottomSheetState) {
            BottomSheetBehavior.STATE_EXPANDED,
            BottomSheetBehavior.STATE_HALF_EXPANDED,
            BottomSheetBehavior.STATE_COLLAPSED,
            BottomSheetBehavior.STATE_SETTLING,
            BottomSheetBehavior.STATE_DRAGGING -> {
                poiDetailBottomDialogFragment?.let { fragment ->
                    if ((fragment as PoiDetailBottomDialogFragment).currentState == expectedBottomSheetState) {
                        callback?.onTransitionToIdle()
                        return true
                    }
                }
            }
            BottomSheetBehavior.STATE_HIDDEN -> {
                if (poiDetailBottomDialogFragment == null) {
                    callback?.onTransitionToIdle()
                    return true
                }
            }
        }

        return false
    }
}