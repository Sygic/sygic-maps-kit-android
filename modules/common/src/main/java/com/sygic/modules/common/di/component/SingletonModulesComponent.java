package com.sygic.modules.common.di.component;

import com.sygic.modules.common.di.module.MapModule;
import com.sygic.modules.common.di.module.PoiDataManagerModule;
import com.sygic.modules.common.di.module.SdkInitializationManagerModule;
import dagger.Component;

import javax.inject.Singleton;

@Singleton
@Component(
        modules = {
                PoiDataManagerModule.class,
                SdkInitializationManagerModule.class
        }
)
public interface SingletonModulesComponent {
    ModulesComponent plus(MapModule mapModule);
}