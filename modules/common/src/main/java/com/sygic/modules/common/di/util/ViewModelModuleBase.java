package com.sygic.modules.common.di.util;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.sygic.tools.viewmodel.ViewModelCreatorFactory;
import com.sygic.tools.viewmodel.ViewModelFactory;
import dagger.Module;
import dagger.Provides;

import javax.inject.Provider;
import java.util.Map;

@Module
public class ViewModelModuleBase {

    @Provides
    ViewModelFactory providesViewModelFactory(@NonNull final Map<Class<? extends ViewModel>, Provider<ViewModelCreatorFactory>> viewModels) {
        return new ViewModelFactory(viewModels);
    }

    @Provides
    ViewModelProvider.Factory bindViewModelFactory(ViewModelFactory factory) {
        return factory;
    }
}