package com.sygic.maps.module.common.di.util;

import com.sygic.maps.module.common.di.ModulesComponent;

public interface ModuleBuilder<T> {
    ModuleBuilder<T> plus(final ModulesComponent component);
    T build();
}