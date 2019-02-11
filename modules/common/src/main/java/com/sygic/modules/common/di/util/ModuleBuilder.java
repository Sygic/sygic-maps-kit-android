package com.sygic.modules.common.di.util;

import com.sygic.modules.common.di.AppComponent;
import com.sygic.modules.common.di.ModulesComponent;

public interface ModuleBuilder<T> {
    ModuleBuilder<T> plus(final ModulesComponent component);
    ModuleBuilder<T> plus(final AppComponent component);
    T build();
}