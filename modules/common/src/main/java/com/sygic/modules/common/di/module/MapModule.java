package com.sygic.modules.common.di.module;

import com.sygic.modules.common.di.scope.ActivityScope;
import com.sygic.modules.common.mapinteraction.manager.MapInteractionManager;
import com.sygic.modules.common.mapinteraction.manager.MapInteractionManagerImpl;
import com.sygic.sdk.map.Camera;
import com.sygic.sdk.map.data.SimpleCameraDataModel;
import com.sygic.ui.common.sdk.model.ExtendedMapDataModel;
import dagger.Module;
import dagger.Provides;

@Module
public class MapModule {

    @ActivityScope
    @Provides
    ExtendedMapDataModel provideDataModel() {
        return new ExtendedMapDataModel();
    }

    @ActivityScope
    @Provides
    Camera.CameraModel provideCameraModel() {
        return new SimpleCameraDataModel();
    }

    @ActivityScope
    @Provides
    MapInteractionManager provideMapInteractionManager() {
        return new MapInteractionManagerImpl();
    }
}
