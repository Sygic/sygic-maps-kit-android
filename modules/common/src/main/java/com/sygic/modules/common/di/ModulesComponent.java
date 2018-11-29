package com.sygic.modules.common.di;

import android.content.Context;
import dagger.Component;

import javax.inject.Singleton;


@Singleton
@Component(
        modules = {
                ContextModule.class
        }
)
public interface ModulesComponent {
    Context getContext();
}