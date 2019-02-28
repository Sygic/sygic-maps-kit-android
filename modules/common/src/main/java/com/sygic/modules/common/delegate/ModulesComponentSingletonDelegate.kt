package com.sygic.modules.common.delegate

import com.sygic.modules.common.di.DaggerModulesComponent
import com.sygic.modules.common.di.ModulesComponent
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

internal class ModulesComponentSingletonDelegate : ReadOnlyProperty<Any, ModulesComponent> {

    companion object {
        private val component: ModulesComponent by lazy {
            DaggerModulesComponent.builder()
                .build()
        }
    }

    override fun getValue(thisRef: Any, property: KProperty<*>): ModulesComponent = component
}