package com.sygic.modules.routeplanner.di;

import com.sygic.modules.common.di.util.ModuleBuilder;
import com.sygic.modules.common.di.ModulesComponent;
import com.sygic.modules.routeplanner.RoutePlannerFragment;
import com.sygic.modules.routeplanner.di.module.ViewModelModule;
import dagger.Component;

import javax.inject.Scope;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Scope
@Retention(RetentionPolicy.RUNTIME)
@interface RoutePlanner { }

@RoutePlanner
@Component(
        modules = {
                ViewModelModule.class
        },
        dependencies = {
                ModulesComponent.class
        }
)
public interface RoutePlannerComponent {
    @Component.Builder
    abstract class Builder implements ModuleBuilder<RoutePlannerComponent> {
    }

    void inject(RoutePlannerFragment fragment);
}