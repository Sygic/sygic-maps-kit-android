package com.sygic.modules.common.di;

import com.sygic.modules.common.di.scope.ActivityScope;
import com.sygic.sdk.map.Camera;
import com.sygic.sdk.map.MapView;
import dagger.Component;


@ActivityScope
@Component(
        modules = {
                MapModule.class
        }
)
public interface ModulesComponent {
    MapView.MapDataModel getMapDataModel();
    Camera.CameraModel getCameraModel();
}