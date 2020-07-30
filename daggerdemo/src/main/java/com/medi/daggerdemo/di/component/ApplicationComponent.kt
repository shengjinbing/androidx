package com.medi.daggerdemo.di.component

import com.medi.daggerdemo.di.module.NetworkModule
import com.medi.daggerdemo.di.module.SubcomponentsModule
import dagger.Component
import javax.inject.Singleton

/**
 * Created by lixiang on 2020/7/28
 * Describe:
 */
@Singleton
@Component(modules = [NetworkModule::class,SubcomponentsModule::class])
interface ApplicationComponent {
    fun loginComponent(): LoginComponent.Factory
}