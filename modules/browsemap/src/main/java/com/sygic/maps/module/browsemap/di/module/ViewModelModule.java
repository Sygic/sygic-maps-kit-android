package com.sygic.maps.module.browsemap.di.module;

import com.sygic.maps.module.browsemap.viewmodel.BrowseMapFragmentViewModel;
import com.sygic.maps.module.browsemap.viewmodel.BrowseMapFragmentViewModelFactory;
import com.sygic.maps.module.common.di.util.ViewModelKey;
import com.sygic.maps.module.common.di.util.ViewModelModuleBase;
import com.sygic.maps.tools.viewmodel.factory.ViewModelCreatorFactory;
import com.sygic.maps.uikit.viewmodels.compass.CompassViewModel;
import com.sygic.maps.uikit.viewmodels.compass.CompassViewModelFactory;
import com.sygic.maps.uikit.viewmodels.positionlockfab.PositionLockFabViewModel;
import com.sygic.maps.uikit.viewmodels.positionlockfab.PositionLockFabViewModelFactory;
import com.sygic.maps.uikit.viewmodels.zoomcontrols.ZoomControlsViewModel;
import com.sygic.maps.uikit.viewmodels.zoomcontrols.ZoomControlsViewModelFactory;
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
