package com.sygic.modules.common.di;

import com.sygic.modules.common.di.component.ModulesComponent;

public interface ModuleBuilder<T> {
    ModuleBuilder<T> plus(final ModulesComponent component);
    T build();
}