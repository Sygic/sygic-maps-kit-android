package com.sygic.modules.common.di

import com.sygic.modules.common.mapinteraction.manager.MapInteractionManager
import com.sygic.modules.common.mapinteraction.manager.MapInteractionManagerImpl
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class MapInteractionManagerModule {

    @Singleton
    @Provides
    fun provideMapInteractionManager(): MapInteractionManager = MapInteractionManagerImpl()
}