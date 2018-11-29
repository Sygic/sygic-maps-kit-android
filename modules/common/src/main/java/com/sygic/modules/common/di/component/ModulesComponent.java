package com.sygic.modules.common.di.component;

import com.sygic.modules.common.di.module.MapModule;
import com.sygic.modules.common.di.scope.ActivityScope;
import com.sygic.modules.common.initialization.manager.SdkInitializationManager;
import com.sygic.modules.common.mapinteraction.manager.MapInteractionManager;
import com.sygic.modules.common.poi.manager.PoiDataManager;
import com.sygic.sdk.map.Camera;
import com.sygic.ui.common.sdk.model.ExtendedMapDataModel;
import dagger.Subcomponent;

@ActivityScope
@Subcomponent(
        modules = {
                MapModule.class
        }
)
public interface ModulesComponent {
    ExtendedMapDataModel getMapDataModel();
    Camera.CameraModel getCameraModel();
    MapInteractionManager getMapInteractionManager();
    PoiDataManager getPoiDataManager();
    SdkInitializationManager getSdkInitializationManager();
}