package com.sygic.samples.fragments

import com.sygic.modules.browsemap.BrowseMapFragment
import com.sygic.sdk.map.data.SimpleCameraDataModel
import com.sygic.sdk.map.data.SimpleMapDataModel

class BrowseMapIndependentFragment : BrowseMapFragment() { //todo

    override fun getMapDataModel() = SimpleMapDataModel()
    override fun getCameraDataModel() = SimpleCameraDataModel()

}