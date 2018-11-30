package com.sygic.modules.browsemap.di.module;

import com.sygic.modules.browsemap.viewmodel.BrowseMapFragmentViewModel;
import com.sygic.modules.browsemap.viewmodel.BrowseMapFragmentViewModelFactory;
import com.sygic.modules.common.di.util.ViewModelKey;
import com.sygic.modules.common.di.util.ViewModelModuleBase;
import com.sygic.tools.viewmodel.ViewModelCreatorFactory;
import com.sygic.ui.viewmodel.compass.CompassViewModel;
import com.sygic.ui.viewmodel.compass.CompassViewModelFactory;
import com.sygic.ui.viewmodel.positionlockfab.PositionLockFabViewModel;
import com.sygic.ui.viewmodel.positionlockfab.PositionLockFabViewModelFactory;
import com.sygic.ui.viewmodel.zoomcontrols.ZoomControlsViewModel;
import com.sygic.ui.viewmodel.zoomcontrols.ZoomControlsViewModelFactory;
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
    @ViewModelKey(BrowseMapFragmentViewModel.class)
    abstract ViewModelCreatorFactory putBrowseMapFragmentViewModelFactory(BrowseMapFragmentViewModelFactory factory);

    @Binds
    @IntoMap
    @ViewModelKey(CompassViewModel.class)
    abstract ViewModelCreatorFactory putCompassViewModelFactory(CompassViewModelFactory factory);

    @Binds
    @IntoMap
    @ViewModelKey(PositionLockFabViewModel.class)
    abstract ViewModelCreatorFactory putPositionLockFabViewModelFactory(PositionLockFabViewModelFactory factory);

    @Binds
    @IntoMap
    @ViewModelKey(ZoomControlsViewModel.class)
    abstract ViewModelCreatorFactory putZoomControlsViewModelFactory(ZoomControlsViewModelFactory factory);
}
