package com.sygic.modules.common.di.component.module;

import com.sygic.modules.common.poi.manager.PoiDataManager;
import com.sygic.modules.common.poi.manager.PoiDataManagerImpl;
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
