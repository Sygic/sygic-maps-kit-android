package com.sygic.modules.common.di;

import android.app.Application;
import com.sygic.modules.common.di.module.AppModule;
import dagger.Component;

@Component(
        modules = {
                AppModule.class
        }
)
public interface AppComponent {
        Application getApplication();
}
