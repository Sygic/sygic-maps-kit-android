package com.sygic.maps.module.common.di.module;

import com.sygic.maps.uikit.viewmodels.common.permission.PermissionsManager;
import com.sygic.maps.uikit.viewmodels.common.permission.PermissionsManagerImpl;
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
