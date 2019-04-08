package com.sygic.maps.module.common.di.module;

import android.app.Application;
import androidx.annotation.NonNull;
import com.sygic.maps.module.common.theme.ThemeManager;
import com.sygic.maps.module.common.theme.ThemeManagerImpl;
import com.sygic.maps.uikit.viewmodels.common.sdk.model.ExtendedMapDataModel;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

@Module
public class ThemeModule {

    @Singleton
    @Provides
    ThemeManager provideThemeManager(@NonNull final Application app, @NonNull final ExtendedMapDataModel model) {
        return new ThemeManagerImpl(app, model);
    }
}
