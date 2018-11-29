package com.sygic.modules.common.di;

import androidx.lifecycle.ViewModelProvider;
import com.sygic.tools.viewmodel.ViewModelFactory;
import dagger.Binds;
import dagger.Module;

@Module
public abstract class ViewModelModuleBase {

    @Binds
    protected abstract ViewModelProvider.Factory bindViewModelFactory(ViewModelFactory factory);
}