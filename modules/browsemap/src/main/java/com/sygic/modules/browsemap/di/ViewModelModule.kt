package com.sygic.modules.browsemap.di

import com.sygic.modules.browsemap.viewmodel.BrowseMapFragmentViewModel
import com.sygic.modules.common.di.ViewModelCreatorFactory
import com.sygic.modules.common.di.ViewModelKey
import com.sygic.modules.common.di.ViewModelModuleBase
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module(
    includes = [
        ViewModelModuleBase::class
    ]
)
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(BrowseMapFragmentViewModel::class)
    internal abstract fun putBrowseMapFragmentViewModelFactory(factory: BrowseMapFragmentViewModel.Factory): ViewModelCreatorFactory
}
