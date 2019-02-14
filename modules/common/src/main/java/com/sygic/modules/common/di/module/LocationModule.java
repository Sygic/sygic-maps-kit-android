package com.sygic.modules.common.di.module;

import com.sygic.ui.common.sdk.location.LocationManager;
import com.sygic.ui.common.sdk.location.LocationManagerImpl;
import com.sygic.ui.common.sdk.model.ExtendedMapDataModel;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

@Module
public class LocationModule {

    @Singleton
    @Provides
    LocationManager provideLocationManager(final ExtendedMapDataModel extendedMapDataModel) {
        return new LocationManagerImpl(extendedMapDataModel);
    }
}
