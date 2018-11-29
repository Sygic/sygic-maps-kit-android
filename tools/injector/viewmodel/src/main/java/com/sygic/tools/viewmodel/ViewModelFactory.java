package com.sygic.tools.viewmodel;

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
    public final ViewModelFactory with(@NonNull Object... assistedValues) {
        this.assistedValues = assistedValues;
        return this;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) viewModels.get(modelClass).get().create(assistedValues);
    }
}