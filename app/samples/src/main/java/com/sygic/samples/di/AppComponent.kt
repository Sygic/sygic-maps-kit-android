package com.sygic.samples.di

import com.sygic.samples.app.App
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import javax.inject.Singleton

@Singleton
@Component(modules = [
    AndroidInjectionModule::class,
    AppModule::class,
    ComplexDemoActivityModule::class,
    RoutingOptionsFragmentModule::class
])
interface AppComponent : AndroidInjector<App> {
    @Component.Factory
    abstract class Factory : AndroidInjector.Factory<App>
}