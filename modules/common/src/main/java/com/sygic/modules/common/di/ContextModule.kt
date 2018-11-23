package com.sygic.modules.common.di

import android.app.Application
import android.content.Context
import androidx.fragment.app.Fragment
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ContextModule(fragment: Fragment) {

    private val application: Application = fragment.requireActivity().application

    @Singleton
    @Provides
    fun provideContext(): Context = application
}