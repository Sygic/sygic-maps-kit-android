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

package com.sygic.maps.tools.viewmodel.factory;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.util.Map;

import javax.inject.Provider;

@SuppressWarnings("unchecked")
public class ViewModelFactory implements ViewModelProvider.Factory {

    private static final Object[] EMPTY_ARRAY = new Object[0];

    private Object[] assistedValues = EMPTY_ARRAY;
    private final Map<Class<? extends ViewModel>, Provider<ViewModelCreatorFactory>> viewModels;

    public ViewModelFactory(@NonNull final Map<Class<? extends ViewModel>, Provider<ViewModelCreatorFactory>> viewModels) {
        this.viewModels = viewModels;
    }

    @NonNull
    public final ViewModelFactory with(@NonNull final Object... assistedValues) {
        this.assistedValues = assistedValues;
        return this;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull final Class<T> modelClass) {
        final Provider<ViewModelCreatorFactory> viewModelCreatorFactoryProvider = viewModels.get(modelClass);
        if (viewModelCreatorFactoryProvider == null) {
            throw new IllegalStateException("ViewModel factory provider for class " + modelClass.getSimpleName() + " not found. Make sure you have bind it to the ViewModel map!");
        }

        final T viewModel = (T) viewModelCreatorFactoryProvider.get().create(assistedValues);
        assistedValues = EMPTY_ARRAY;
        return viewModel;
    }
}