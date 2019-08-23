/*
 * Copyright (c) 2019 Sygic a.s. All rights reserved.
 *
 * This project is licensed under the MIT License.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.sygic.maps.module.navigation.di.module;

import com.sygic.maps.module.common.di.util.ViewModelKey;
import com.sygic.maps.module.common.di.util.ViewModelModuleBase;
import com.sygic.maps.module.navigation.viewmodel.NavigationFragmentViewModel;
import com.sygic.maps.module.navigation.viewmodel.NavigationFragmentViewModelFactory;
import com.sygic.maps.tools.viewmodel.factory.ViewModelCreatorFactory;
import com.sygic.maps.uikit.viewmodels.navigation.lanes.LanesViewModel;
import com.sygic.maps.uikit.viewmodels.navigation.lanes.LanesViewModelFactory;
import com.sygic.maps.uikit.viewmodels.navigation.preview.RoutePreviewControlsViewModel;
import com.sygic.maps.uikit.viewmodels.navigation.preview.RoutePreviewControlsViewModelFactory;
import com.sygic.maps.uikit.viewmodels.navigation.signpost.FullSignpostViewModel;
import com.sygic.maps.uikit.viewmodels.navigation.signpost.FullSignpostViewModelFactory;
import com.sygic.maps.uikit.viewmodels.navigation.signpost.SimplifiedSignpostViewModel;
import com.sygic.maps.uikit.viewmodels.navigation.signpost.SimplifiedSignpostViewModelFactory;

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
    @ViewModelKey(NavigationFragmentViewModel.class)
    abstract ViewModelCreatorFactory putNavigationFragmentViewModelFactory(NavigationFragmentViewModelFactory factory);

    @Binds
    @IntoMap
    @ViewModelKey(FullSignpostViewModel.class)
    abstract ViewModelCreatorFactory putFullSignpostViewModelFactory(FullSignpostViewModelFactory factory);

    @Binds
    @IntoMap
    @ViewModelKey(SimplifiedSignpostViewModel.class)
    abstract ViewModelCreatorFactory putSimplifiedSignpostViewModelFactory(SimplifiedSignpostViewModelFactory factory);

    @Binds
    @IntoMap
    @ViewModelKey(LanesViewModel.class)
    abstract ViewModelCreatorFactory putLanesViewModelFactory(LanesViewModelFactory factory);

    @Binds
    @IntoMap
    @ViewModelKey(RoutePreviewControlsViewModel.class)
    abstract ViewModelCreatorFactory putRoutePreviewControlsViewModelFactory(RoutePreviewControlsViewModelFactory factory);
}
