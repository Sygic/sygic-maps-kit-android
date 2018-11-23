package com.sygic.modules.common.di

import com.sygic.ui.common.sdk.model.ExtendedMapDataModel
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class MapDataModelModule {

    @Singleton
    @Provides
    fun provideExtendedMapDataModel(): ExtendedMapDataModel = ExtendedMapDataModel()
}