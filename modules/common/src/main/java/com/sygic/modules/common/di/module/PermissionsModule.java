package com.sygic.modules.common.di.module;

import com.sygic.ui.common.sdk.permission.PermissionsManager;
import com.sygic.ui.common.sdk.permission.PermissionsManagerImpl;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

@Module
public class PermissionsModule {

    @Singleton
    @Provides
    PermissionsManager providePermissionManager() {
        return new PermissionsManagerImpl();
    }
}
