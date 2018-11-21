package com.sygic.modules.common.di

import android.content.Context
import dagger.Component
import javax.inject.Singleton


@Singleton
@Component(
    modules = [
        ContextModule::class
    ]
)
interface ModulesComponent {
    val context: Context
}

interface ModuleBuilder<out T> {
    fun plus(component: ModulesComponent): ModuleBuilder<T>
    fun build(): T
}