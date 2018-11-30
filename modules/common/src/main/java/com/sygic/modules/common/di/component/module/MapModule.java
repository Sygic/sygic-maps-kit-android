package com.sygic.modules.common.di.component.module;

import com.sygic.modules.common.mapinteraction.manager.MapInteractionManager;
import com.sygic.modules.common.mapinteraction.manager.MapInteractionManagerImpl;
import com.sygic.sdk.map.Camera;
import com.sygic.sdk.map.data.SimpleCameraDataModel;
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
    Camera.CameraModel provideCameraModel() {
        return new SimpleCameraDataModel();
    }

    @Singleton
    @Provides
    MapInteractionManager provideMapInteractionManager() {
        return new MapInteractionManagerImpl();
    }
}
