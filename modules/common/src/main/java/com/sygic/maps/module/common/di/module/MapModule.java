package com.sygic.maps.module.common.di.module;

import com.sygic.maps.module.common.mapinteraction.manager.MapInteractionManager;
import com.sygic.maps.module.common.mapinteraction.manager.MapInteractionManagerImpl;
import com.sygic.maps.uikit.viewmodels.common.sdk.model.ExtendedCameraModel;
import com.sygic.maps.uikit.viewmodels.common.sdk.model.ExtendedMapDataModel;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

@Module
public class MapModule {

    @Singleton
    @Provides
    ExtendedMapDataModel provideDataModel() {
        return ExtendedMapDataModel.INSTANCE;
    }

    @Singleton
    @Provides
    ExtendedCameraModel provideCameraModel() {
        return ExtendedCameraModel.INSTANCE;
    }

    @Singleton
    @Provides
    MapInteractionManager provideMapInteractionManager() {
        return new MapInteractionManagerImpl();
    }
}
