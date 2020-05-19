package com.sygic.samples.di

import com.sygic.samples.demo.RoutingOptionsFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class RoutingOptionsFragmentModule {
    @ContributesAndroidInjector
    abstract fun contributeRoutingOptionsFragmentInjector(): RoutingOptionsFragment
}