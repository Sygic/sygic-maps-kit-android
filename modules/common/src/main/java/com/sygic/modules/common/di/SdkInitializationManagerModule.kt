package com.sygic.modules.common.di

import com.sygic.modules.common.initialization.manager.SdkInitializationManager
import com.sygic.modules.common.initialization.manager.SdkInitializationManagerImpl
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class SdkInitializationManagerModule {

    @Singleton
    @Provides
    fun provideSdkInitializationManager(): SdkInitializationManager = SdkInitializationManagerImpl()
}