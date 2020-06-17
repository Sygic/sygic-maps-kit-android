package com.sygic.samples.demo.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sygic.maps.module.common.routingoptions.RoutingOptionsManager
import javax.inject.Inject

class ComplexDemoViewModelFactory @Inject constructor(
    private val routingOptionsManager: RoutingOptionsManager
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T =
        if (modelClass.isAssignableFrom(ComplexDemoActivityViewModel::class.java)) {
            ComplexDemoActivityViewModel(routingOptionsManager) as T
        } else {
            throw IllegalArgumentException("ViewModel not found")
        }
}