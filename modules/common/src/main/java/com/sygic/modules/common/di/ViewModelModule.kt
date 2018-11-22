package com.sygic.modules.common.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.MapKey
import dagger.Module
import javax.inject.Inject
import javax.inject.Provider
import kotlin.reflect.KClass

interface ViewModelCreatorFactory {
    fun create(vararg assistedValues: Any?): ViewModel
}

@Suppress("UNCHECKED_CAST")
class ViewModelFactory @Inject constructor(private val viewModels: MutableMap<Class<out ViewModel>, Provider<ViewModelCreatorFactory>>) :
    ViewModelProvider.Factory {

    private var assistedValues: Array<out Any?> = emptyArray()

    override fun <T : ViewModel> create(modelClass: Class<T>): T = viewModels[modelClass]?.get()?.create(*assistedValues) as T

    fun with(vararg assistedValues: Any?): ViewModelFactory {
        this.assistedValues = assistedValues
        return this
    }
}

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
@MapKey
annotation class ViewModelKey(val value: KClass<out ViewModel>)

@Module
abstract class ViewModelModuleBase {

    @Binds
    protected abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

}