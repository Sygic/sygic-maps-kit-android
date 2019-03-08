package com.sygic.modules.common.delegate

import androidx.fragment.app.Fragment
import com.sygic.modules.common.di.DaggerModulesComponent
import com.sygic.modules.common.di.ModulesComponent
import com.sygic.modules.common.di.module.AppModule

class ModulesComponentDelegate {

    companion object {
        private var component: ModulesComponent? = null
    }

    fun getInstance(fragment: Fragment): ModulesComponent = component?.let {
        it
    } ?: DaggerModulesComponent.builder()
        .appModule(AppModule(fragment))
        .build()
        .also { component = it }
}