package com.sygic.maps.module.common.di.util;

import androidx.lifecycle.ViewModelProvider;
import com.sygic.maps.tools.viewmodel.factory.ViewModelFactory;
import dagger.Binds;
import dagger.Module;

@Module
public abstract class ViewModelModuleBase {

    @Binds
    protected abstract ViewModelProvider.Factory bindViewModelFactory(ViewModelFactory factory);
}