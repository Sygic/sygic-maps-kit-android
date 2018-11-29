package com.sygic.modules.browsemap.di;

import com.sygic.modules.browsemap.BrowseMapFragment;
import com.sygic.modules.common.di.ModuleBuilder;
import com.sygic.modules.common.di.component.ModulesComponent;
import dagger.Component;

import javax.inject.Scope;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Scope
@Retention(RetentionPolicy.RUNTIME)
@interface Browse { }

@Browse
@Component(
        modules = {
                ViewModelModule.class
        },
        dependencies = {
                ModulesComponent.class
        }
)
public interface BrowseMapComponent {
    @Component.Builder
    abstract class Builder implements ModuleBuilder<BrowseMapComponent> {
    }

    void inject(BrowseMapFragment fragment);
}