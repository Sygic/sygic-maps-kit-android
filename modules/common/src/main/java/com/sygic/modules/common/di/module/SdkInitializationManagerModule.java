package com.sygic.modules.common.di.module;

import com.sygic.modules.common.initialization.manager.SdkInitializationManager;
import com.sygic.modules.common.initialization.manager.SdkInitializationManagerImpl;
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
