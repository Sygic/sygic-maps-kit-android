package com.sygic.modules.common.di;

import android.app.Application;
import android.content.Context;
import androidx.fragment.app.Fragment;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;


@Module
public class ContextModule {

    private final Application application;

    public ContextModule(final Fragment fragment) {
        application = fragment.requireActivity().getApplication();
    }

    @Singleton
    @Provides
    Context provideContext() {
        return application;
    }
}
