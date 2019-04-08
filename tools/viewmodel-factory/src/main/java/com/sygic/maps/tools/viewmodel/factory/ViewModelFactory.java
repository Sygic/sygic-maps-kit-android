package com.sygic.maps.tools.viewmodel.factory;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.Map;

@SuppressWarnings("unchecked")
public class ViewModelFactory implements ViewModelProvider.Factory {

    private Object[] assistedValues = new Object[0];
    private final Map<Class<? extends ViewModel>, Provider<ViewModelCreatorFactory>> viewModels;

    @Inject
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

        return (T) viewModelCreatorFactoryProvider.get().create(assistedValues);
    }
}