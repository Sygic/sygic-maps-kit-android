package com.sygic.modules.browsemap.detail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sygic.modules.common.detail.DetailsViewFactory
import com.sygic.sdk.map.`object`.UiObject
import com.sygic.sdk.map.`object`.data.ViewObjectData
import kotlinx.android.parcel.Parcelize

@Parcelize
internal class PoiDataDetailsFactory(
    private val factory: DetailsViewFactory,
    private val data: ViewObjectData
) : UiObject.ViewFactory {

    override fun createView(inflater: LayoutInflater, container: ViewGroup): View {
        return factory.getDetailsView(inflater, container, data)
    }
}