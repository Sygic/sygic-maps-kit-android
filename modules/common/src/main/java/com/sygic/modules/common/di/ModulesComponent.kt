package com.sygic.modules.common.di

import android.content.Context
import com.sygic.modules.common.initialization.manager.SdkInitializationManager
import com.sygic.modules.common.mapinteraction.manager.MapInteractionManager
import com.sygic.modules.common.poi.manager.PoiDataManager
import com.sygic.ui.common.sdk.model.ExtendedMapDataModel
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        ContextModule::class,
        MapDataModelModule::class,
        PoiDataManagerModule::class,
        MapInteractionManagerModule::class,
        SdkInitializationManagerModule::class
    ]
)
interface ModulesComponent {
    val context: Context
    val poiDataManager: PoiDataManager
    val extendedMapDataModel: ExtendedMapDataModel
    val mapInteractionManager: MapInteractionManager
    val sdkInitializationManager: SdkInitializationManager
}

interface ModuleBuilder<out T> {
    fun plus(component: ModulesComponent): ModuleBuilder<T>
    fun build(): T
}