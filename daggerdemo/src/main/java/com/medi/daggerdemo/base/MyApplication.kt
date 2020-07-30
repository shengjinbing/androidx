package com.medi.daggerdemo.base

import android.app.Application
import com.medi.daggerdemo.di.component.DaggerApplicationComponent

/**
 * Created by lixiang on 2020/7/28
 * Describe:
 */
class MyApplication : Application() {
    val appComponent = DaggerApplicationComponent.create()
}