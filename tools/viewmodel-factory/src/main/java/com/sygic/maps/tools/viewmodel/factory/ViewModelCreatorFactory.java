package com.sygic.maps.tools.viewmodel.factory;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

public interface ViewModelCreatorFactory {
    @NonNull
    ViewModel create(@NonNull Object... assistedValues);
}