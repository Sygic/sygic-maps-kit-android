package com.sygic.modules.common.di;

import com.sygic.modules.common.di.scope.ActivityScope;
import com.sygic.sdk.map.Camera;
import com.sygic.sdk.map.MapView;
import com.sygic.sdk.map.data.SimpleCameraDataModel;
import com.sygic.sdk.map.data.SimpleMapDataModel;
import dagger.Module;
import dagger.Provides;


@Module
public class MapModule {

    @ActivityScope
    @Provides
    MapView.MapDataModel provideDataModel() {
        return new SimpleMapDataModel();
    }

    @ActivityScope
    @Provides
    Camera.CameraModel provideCameraModel() {
        return new SimpleCameraDataModel();
    }
}
