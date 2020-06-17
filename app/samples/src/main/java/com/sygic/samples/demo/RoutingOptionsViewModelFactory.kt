package com.sygic.samples.demo

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sygic.maps.module.common.routingoptions.RoutingOptionsManager
import com.sygic.samples.demo.viewmodels.RoutingOptionsViewModel
import javax.inject.Inject

class RoutingOptionsViewModelFactory @Inject constructor(
    private val application: Application,
    private val routingOptionsManager: RoutingOptionsManager
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T =
        if (modelClass.isAssignableFrom(RoutingOptionsViewModel::class.java)) {
            RoutingOptionsViewModel(application, routingOptionsManager) as T
        } else {
            throw IllegalArgumentException("ViewModel not found")
        }
}