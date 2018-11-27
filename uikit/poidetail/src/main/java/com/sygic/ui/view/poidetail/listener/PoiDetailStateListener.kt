package com.sygic.ui.view.poidetail.listener

import com.google.android.material.bottomsheet.BottomSheetBehavior

interface PoiDetailStateListener {
    fun onPoiDetailStateChanged(@BottomSheetBehavior.State state: Int)
}