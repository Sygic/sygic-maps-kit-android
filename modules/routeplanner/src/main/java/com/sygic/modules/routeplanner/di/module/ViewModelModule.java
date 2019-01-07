package com.sygic.modules.routeplanner.di.module;

import com.sygic.modules.common.di.util.ViewModelKey;
import com.sygic.modules.common.di.util.ViewModelModuleBase;
import com.sygic.modules.routeplanner.viewmodel.RoutePlannerFragmentViewModel;
import com.sygic.modules.routeplanner.viewmodel.RoutePlannerFragmentViewModelFactory;
import com.sygic.tools.viewmodel.ViewModelCreatorFactory;
import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module(
        includes = {
                ViewModelModuleBase.class
        }
)
public abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(RoutePlannerFragmentViewModel.class)
    abstract ViewModelCreatorFactory putRoutePlannerFragmentViewModelFactory(RoutePlannerFragmentViewModelFactory factory);
}
