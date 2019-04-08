package com.sygic.maps.module.common.di.module;

import com.sygic.maps.module.common.poi.manager.PoiDataManager;
import com.sygic.maps.module.common.poi.manager.PoiDataManagerImpl;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

@Module
public class PoiDataManagerModule {

    @Singleton
    @Provides
    PoiDataManager providePoiDataManager() {
        return new PoiDataManagerImpl();
    }
}
