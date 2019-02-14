package com.sygic.modules.common.di.module;

import com.sygic.ui.common.sdk.location.LocationManager;
import com.sygic.ui.common.sdk.location.LocationManagerImpl;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

@Module
public class LocationModule {

    @Singleton
    @Provides
    LocationManager provideLocationManager() {
        return new LocationManagerImpl();
    }
}
