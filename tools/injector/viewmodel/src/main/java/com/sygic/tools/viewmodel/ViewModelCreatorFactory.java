package com.sygic.tools.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

public interface ViewModelCreatorFactory {
    @NonNull
    ViewModel create(@NonNull Object... assistedValues);
}