package com.sygic.modules.common.di

import com.sygic.modules.common.poi.manager.PoiDataManager
import com.sygic.modules.common.poi.manager.PoiDataManagerImpl
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class PoiDataManagerModule {

    @Singleton
    @Provides
    fun providePoiDataManager(): PoiDataManager = PoiDataManagerImpl()
}