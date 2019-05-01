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

package com.sygic.maps.module.search.di.module;

import com.sygic.maps.module.common.di.util.ViewModelKey;
import com.sygic.maps.module.common.di.util.ViewModelModuleBase;
import com.sygic.maps.module.search.viewmodel.SearchFragmentViewModel;
import com.sygic.maps.module.search.viewmodel.SearchFragmentViewModelFactory;
import com.sygic.maps.tools.viewmodel.factory.ViewModelCreatorFactory;
import com.sygic.maps.uikit.viewmodels.searchtoolbar.SearchToolbarViewModel;
import com.sygic.maps.uikit.viewmodels.searchtoolbar.SearchToolbarViewModelFactory;
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
    @ViewModelKey(SearchFragmentViewModel.class)
    abstract ViewModelCreatorFactory putSearchFragmentViewModelFactory(SearchFragmentViewModelFactory factory);

    @Binds
    @IntoMap
    @ViewModelKey(SearchToolbarViewModel.class)
    abstract ViewModelCreatorFactory putSearchToolbarViewModelFactory(SearchToolbarViewModelFactory factory);
}
