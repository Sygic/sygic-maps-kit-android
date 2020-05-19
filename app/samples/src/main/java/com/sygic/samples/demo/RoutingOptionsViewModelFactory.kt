package com.sygic.samples.demo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sygic.samples.demo.viewmodels.RoutingOptionsViewModel
import javax.inject.Inject

class RoutingOptionsViewModelFactory @Inject constructor(
    private val routingOptionsManager: RoutingOptionsManager
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T =
        if (modelClass.isAssignableFrom(RoutingOptionsViewModel::class.java)) {
            RoutingOptionsViewModel(routingOptionsManager) as T
        } else {
            throw IllegalArgumentException("ViewModel not found")
        }
}