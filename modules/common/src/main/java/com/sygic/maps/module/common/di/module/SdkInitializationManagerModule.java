package com.sygic.maps.module.common.di.module;

import com.sygic.maps.module.common.initialization.manager.SdkInitializationManager;
import com.sygic.maps.module.common.initialization.manager.SdkInitializationManagerImpl;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

@Module
public class SdkInitializationManagerModule {

    @Singleton
    @Provides
    SdkInitializationManager provideSdkInitializationManager() {
        return new SdkInitializationManagerImpl();
    }
}
