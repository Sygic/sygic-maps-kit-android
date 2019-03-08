package com.sygic.modules.common.di.module;

import android.app.Application;
import androidx.fragment.app.Fragment;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

@Module
public class AppModule {

    private final Application app;

    public AppModule(final Fragment fragment) {
        app = fragment.requireActivity().getApplication();
    }

    @Singleton
    @Provides
    Application provideApplication() {
        return app;
    }
}
