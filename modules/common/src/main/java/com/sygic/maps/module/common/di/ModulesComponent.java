package com.sygic.maps.module.common.di;

import android.app.Application;
import com.sygic.maps.module.common.di.module.*;
import com.sygic.maps.module.common.initialization.manager.SdkInitializationManager;
import com.sygic.maps.module.common.mapinteraction.manager.MapInteractionManager;
import com.sygic.maps.module.common.poi.manager.PoiDataManager;
import com.sygic.maps.module.common.theme.ThemeManager;
import com.sygic.maps.uikit.viewmodels.common.location.LocationManager;
import com.sygic.maps.uikit.viewmodels.common.sdk.model.ExtendedCameraModel;
import com.sygic.maps.uikit.viewmodels.common.sdk.model.ExtendedMapDataModel;
import com.sygic.maps.uikit.viewmodels.common.permission.PermissionsManager;
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