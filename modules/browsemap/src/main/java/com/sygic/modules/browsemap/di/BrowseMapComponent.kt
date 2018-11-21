package com.sygic.modules.browsemap.di

import com.sygic.modules.browsemap.BrowseMapFragment
import com.sygic.modules.common.di.ModuleBuilder
import com.sygic.modules.common.di.ModulesComponent
import dagger.Component
import javax.inject.Scope


@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class Browse

@Browse
@Component(
    modules = [
        ViewModelModule::class
    ],
    dependencies = [
        ModulesComponent::class
    ]
)
interface BrowseMapComponent {
    @Component.Builder
    abstract class Builder : ModuleBuilder<BrowseMapComponent>

    fun inject(fragment: BrowseMapFragment)
}