package com.sygic.modules.common.di;

import com.sygic.modules.common.di.module.MapModule;
import com.sygic.modules.common.di.module.PermissionsModule;
import com.sygic.modules.common.di.module.PoiDataManagerModule;
import com.sygic.modules.common.di.module.SdkInitializationManagerModule;
import com.sygic.modules.common.initialization.manager.SdkInitializationManager;
import com.sygic.modules.common.mapinteraction.manager.MapInteractionManager;
import com.sygic.modules.common.poi.manager.PoiDataManager;
import com.sygic.sdk.map.Camera;
import com.sygic.ui.common.sdk.model.ExtendedMapDataModel;
import com.sygic.ui.common.sdk.permission.PermissionsManager;
import dagger.Component;

import javax.inject.Singleton;

@Singleton
@Component(
        modules = {
                MapModule.class,
                PoiDataManagerModule.class,
                SdkInitializationManagerModule.class,
                PermissionsModule.class
        }
)
public interface ModulesComponent {
    ExtendedMapDataModel getMapDataModel();
    Camera.CameraModel getCameraModel();
    MapInteractionManager getMapInteractionManager();
    PoiDataManager getPoiDataManager();
    SdkInitializationManager getSdkInitializationManager();
    PermissionsManager getPermissionsManager();
}