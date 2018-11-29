package com.sygic.modules.common.di;

public interface ModuleBuilder<T> {
    ModuleBuilder<T> plus(final ModulesComponent component);
    T build();
}