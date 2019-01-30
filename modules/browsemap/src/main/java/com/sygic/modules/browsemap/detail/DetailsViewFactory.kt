package com.sygic.modules.browsemap.detail

import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sygic.ui.common.sdk.data.PoiData


abstract class DetailsViewFactory : Parcelable {

    abstract fun getDetailsView(inflater: LayoutInflater, container: ViewGroup, poiData: PoiData) : View

    open fun getXOffset() = 0f
    open fun getYOffset() = 0f
}