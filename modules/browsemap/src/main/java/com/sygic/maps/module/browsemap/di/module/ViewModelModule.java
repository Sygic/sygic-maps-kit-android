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
