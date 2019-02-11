package com.sygic.modules.common.di.module;

import android.app.Application;
import androidx.fragment.app.Fragment;
import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {

    private final Application app;

    public AppModule(final Fragment fragment) {
        app = fragment.requireActivity().getApplication();
    }

    @Provides
    Application provideApplication() {
        return app;
    }
}
