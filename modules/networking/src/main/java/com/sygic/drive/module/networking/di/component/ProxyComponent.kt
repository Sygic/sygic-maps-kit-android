package com.sygic.drive.module.networking.di.component

interface ProxyComponent : NetworkingModulesComponent

class ProxyComponentImpl(private val delegate: NetworkingModulesComponent) : ProxyComponent, NetworkingModulesComponent by delegate