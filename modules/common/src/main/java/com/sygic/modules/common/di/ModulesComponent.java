package com.sygic.modules.common.di;

import android.app.Application;
import com.sygic.modules.common.di.module.*;
import com.sygic.modules.common.initialization.manager.SdkInitializationManager;
import com.sygic.modules.common.mapinteraction.manager.MapInteractionManager;
import com.sygic.modules.common.poi.manager.PoiDataManager;
import com.sygic.modules.common.theme.ThemeManager;
import com.sygic.ui.common.sdk.location.LocationManager;
import com.sygic.ui.common.sdk.model.ExtendedCameraModel;
import com.sygic.ui.common.sdk.model.ExtendedMapDataModel;
import com.sygic.ui.common.sdk.permission.PermissionsManager;
import dagger.Component;

import javax.inject.Singleton;

@Singleton
@Component(
        modules = {
                AppModule.class,
                MapModule.class,
                PoiDataManagerModule.class,
                SdkInitializationManagerModule.class,
                PermissionsModule.class,
                LocationModule.class,
                ThemeModule.class
        }
)
public interface ModulesComponent {
    ExtendedMapDataModel getMapDataModel();
    ExtendedCameraModel getCameraModel();
    MapInteractionManager getMapInteractionManager();
    PoiDataManager getPoiDataManager();
    SdkInitializationManager getSdkInitializationManager();
    PermissionsManager getPermissionsManager();
    LocationManager getLocationManager();
    ThemeManager getThemeManager();
    Application getApplication();
}