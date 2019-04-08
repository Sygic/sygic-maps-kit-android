package com.sygic.maps.module.common.di.module;

import com.sygic.maps.uikit.viewmodels.common.location.LocationManager;
import com.sygic.maps.uikit.viewmodels.common.location.LocationManagerImpl;
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
