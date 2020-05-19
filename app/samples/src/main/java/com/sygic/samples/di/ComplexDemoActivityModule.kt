package com.sygic.samples.di

import com.sygic.samples.demo.ComplexDemoActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ComplexDemoActivityModule {
    @ContributesAndroidInjector
    abstract fun contributeComplexDemoActivityInjector(): ComplexDemoActivity
}