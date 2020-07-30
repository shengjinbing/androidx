package com.medi.daggerdemo.di.module

import com.medi.daggerdemo.di.component.LoginComponent
import dagger.Module

/**
 * Created by lixiang on 2020/7/28
 * Describe:
 */
@Module(subcomponents = [LoginComponent::class])
class SubcomponentsModule {}