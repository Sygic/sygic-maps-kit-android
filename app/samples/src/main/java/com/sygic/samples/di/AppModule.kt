package com.sygic.samples.di

import android.content.Context
import android.content.SharedPreferences
import com.sygic.samples.app.App
import com.sygic.samples.demo.PersistentRoutingOptionsManager
import com.sygic.samples.demo.RoutingOptionsManager
import dagger.Module
import dagger.Provides
import javax.inject.Named
import javax.inject.Singleton

@Module
internal class AppModule {
    @Singleton
    @Provides
    @Named(ROUTING_OPTIONS_PREFERENCES)
    fun provideRoutingOptionsPreferences(app: App): SharedPreferences
            = app.getSharedPreferences(ROUTING_OPTIONS_PREFERENCES, Context.MODE_PRIVATE)

    @Singleton
    @Provides
    fun provideRoutingOptionsManager(
        @Named(ROUTING_OPTIONS_PREFERENCES) preferences: SharedPreferences
    ): RoutingOptionsManager = PersistentRoutingOptionsManager(preferences)

    companion object {
        private const val ROUTING_OPTIONS_PREFERENCES = "routing_options_preferences"
    }
}