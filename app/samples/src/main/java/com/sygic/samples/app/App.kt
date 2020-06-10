package com.sygic.samples.app

import com.sygic.maps.module.common.delegate.ApplicationComponentDelegate
import com.sygic.samples.di.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication

class App : DaggerApplication() {
    private val appComponent by lazy {
        DaggerAppComponent.factory().create(this, ApplicationComponentDelegate.getComponent(this))
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> = appComponent
}