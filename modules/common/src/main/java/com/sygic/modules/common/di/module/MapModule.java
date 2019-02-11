package com.sygic.modules.common.di.module;

import com.sygic.modules.common.mapinteraction.manager.MapInteractionManager;
import com.sygic.modules.common.mapinteraction.manager.MapInteractionManagerImpl;
import com.sygic.ui.common.sdk.model.ExtendedCameraModel;
import com.sygic.ui.common.sdk.model.ExtendedMapDataModel;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

@Module
public class MapModule {

    @Singleton
    @Provides
    ExtendedMapDataModel provideDataModel() {
        return new ExtendedMapDataModel();
    }

    @Singleton
    @Provides
    ExtendedCameraModel provideCameraModel() {
        return new ExtendedCameraModel();
    }

    @Singleton
    @Provides
    MapInteractionManager provideMapInteractionManager() {
        return new MapInteractionManagerImpl();
    }
}
